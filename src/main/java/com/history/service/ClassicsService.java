package com.history.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.config.CtextProperties;
import com.history.config.WikisourceProperties;
import com.history.dto.CtextBookDTO;
import com.history.dto.CtextFulltextDTO;
import com.history.dto.LlmChatRequest;
import com.history.dto.LlmMessage;
import com.history.dto.WikisourceBookDTO;
import com.history.dto.WikisourceFulltextDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 典籍 API 代理服务
 * 双源策略：
 *   1. ctext.org API（需 API key 才能获取全文）
 *   2. Wikisource API（无需 key，国内访问稳定）
 *
 * ctext 文档：https://ctext.org/tools/api
 *   - searchtexts：按书名搜索，返回 URN 列表（无需 API key）
 *   - readlink：从 ctext.org URL 反查 URN（无需 API key）
 *   - gettext：根据 URN 获取典籍全文（需 API key 或订阅 IP）
 *   - getstatus：查询当前认证状态
 *
 * Wikisource 文档：https://www.mediawiki.org/wiki/API:Main_page
 *   - action=query&list=search：搜索页面（无需 key）
 *   - action=parse&page=xxx：获取页面 wikitext（无需 key）
 *
 * 缓存：基于 ConcurrentHashMap 的简单 TTL 缓存，避免重复请求
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassicsService {

    private final CtextProperties ctextProperties;
    private final WikisourceProperties wikisourceProperties;
    private final LlmService llmService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 简单缓存：key -> [response, expireAt] */
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    /** User-Agent（MediaWiki API 要求，参考 https://meta.wikimedia.org/wiki/User-Agent_policy） */
    private static final String USER_AGENT =
            "FiveMillenniumMuseum/0.1.0 (https://github.com/history-museum; contact@history.local)";

    @PostConstruct
    void init() {
        // IPv4 强制设置已在 HistoryApplication.main 中完成
        // 使用 HttpURLConnection（而非 Java 11+ HttpClient），因为后者在部分 Windows 环境下
        // 存在连接超时问题（TCP 能连但 HttpClient 超时），HttpURLConnection 兼容性更好
        log.info("ClassicsService initialized, using HttpURLConnection for HTTP requests");
    }

    /**
     * 发起带 UA 的 GET 请求，返回响应体字符串
     *
     * 实现说明：Java 17 的 TLS 实现与 Wikisource CDN 不兼容
     * （所有 TLS 模式下都 read timeout，但 PowerShell/curl.exe 能成功），
     * 因此对 wikisource.org 域名走 curl.exe 子进程转发，
     * 其他域名仍用 HttpURLConnection。
     */
    private String getForString(String url) throws Exception {
        long start = System.currentTimeMillis();
        if (url.contains("wikisource.org")) {
            // Wikisource 走 curl.exe 转发（绕过 Java 17 TLS bug）
            return getForStringViaCurl(url, start);
        }
        return getForStringViaHttpURLConnection(url, start);
    }

    /**
     * 通过 curl.exe 子进程发起 GET 请求
     * 用于绕过 Java 17 TLS 实现与某些 CDN 不兼容的问题
     * curl.exe 使用 Windows Schannel TLS（与 PowerShell 相同），兼容性好
     * 在单独线程读取输出，避免 pipe buffer 阻塞
     */
    private String getForStringViaCurl(String url, long start) throws Exception {
        log.info("curl requesting url={}", url);
        ProcessBuilder pb = new ProcessBuilder(
                "curl.exe",
                "-s",                          // 静默模式，不显示进度
                "-S",                          // 出错时显示错误信息
                "--max-time", "30",            // 总超时 30 秒
                "--connect-timeout", "10",     // 连接超时 10 秒
                "-H", "User-Agent: FiveMillenniumMuseum/0.1.0",
                "-H", "Accept: application/json",
                "--noproxy", "*",              // 不使用任何代理
                "-w", "\n%{http_code}",        // 在 body 末尾输出 HTTP 状态码
                url);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 在单独线程读取输出，避免 pipe buffer（4KB）被填满导致进程阻塞
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        Thread reader = new Thread(() -> {
            try (java.io.InputStream is = process.getInputStream()) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1) {
                    baos.write(buf, 0, n);
                }
            } catch (Exception e) {
                // ignore，主线程会处理超时
            }
        });
        reader.setDaemon(true);
        reader.start();

        boolean finished = process.waitFor(45, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            reader.interrupt();
            throw new RuntimeException("curl timeout (45s)");
        }
        reader.join(3000);  // 等待读取线程结束

        long elapsed = System.currentTimeMillis() - start;
        int exitCode = process.exitValue();
        String fullOutput = baos.toString("UTF-8");
        log.info("curl done url={} exitCode={} elapsedMs={} outputLen={}",
                url, exitCode, elapsed, fullOutput.length());

        if (exitCode != 0) {
            throw new RuntimeException("curl failed exitCode=" + exitCode + " output=" +
                    fullOutput.substring(0, Math.min(200, fullOutput.length())));
        }

        // 最后一行是 HTTP 状态码（由 -w "\n%{http_code}" 输出）
        int lastNewline = fullOutput.lastIndexOf('\n');
        if (lastNewline < 0) {
            // 没有换行，整个输出就是 body（curl 可能没输出状态码）
            return fullOutput;
        }
        String httpCode = fullOutput.substring(lastNewline + 1).trim();
        String body = fullOutput.substring(0, lastNewline);
        log.info("curl httpCode={} bodyLen={}", httpCode, body.length());

        if (!httpCode.isEmpty() && !httpCode.startsWith("2")) {
            throw new RuntimeException("curl HTTP " + httpCode + " body=" +
                    body.substring(0, Math.min(200, body.length())));
        }
        return body;
    }

    /**
     * 通过 HttpURLConnection 发起 GET 请求（用于非 Wikisource 域名）
     */
    private String getForStringViaHttpURLConnection(String url, long start) throws Exception {
        log.info("httpurl requesting url={}", url);
        java.net.URL urlObj = new java.net.URL(url);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection(java.net.Proxy.NO_PROXY);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setInstanceFollowRedirects(true);

        int status = conn.getResponseCode();
        long elapsed = System.currentTimeMillis() - start;
        log.info("httpurl done url={} status={} elapsedMs={}", url, status, elapsed);

        if (status < 200 || status >= 300) {
            String errBody = readStream(conn.getErrorStream());
            throw new RuntimeException("HTTP " + status + " body=" +
                    (errBody == null ? "" : errBody.substring(0, Math.min(200, errBody.length()))));
        }
        String body = readStream(conn.getInputStream());
        log.info("httpurl bodyLen={}", body == null ? 0 : body.length());
        return body;
    }

    /** 读取输入流为 UTF-8 字符串 */
    private String readStream(java.io.InputStream is) throws Exception {
        if (is == null) return null;
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString("UTF-8");
        }
    }

    /**
     * 按书名搜索 ctext 典籍
     * 公开端点，无需 API key
     *
     * @param title 书名（中文/繁体均可）
     * @return 匹配的典籍列表
     */
    public List<CtextBookDTO> searchBooks(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }
        String cacheKey = "search:" + title;
        List<CtextBookDTO> cached = getCache(cacheKey, List.class);
        if (cached != null) return cached;

        String url = ctextProperties.getBaseUrl()
                + "/searchtexts?title=" + encode(title);
        try {
            String json = getForString(url);
            JsonNode root = objectMapper.readTree(json);
            JsonNode books = root.path("books");
            List<CtextBookDTO> result = new ArrayList<>();
            if (books.isArray()) {
                Iterator<JsonNode> it = books.elements();
                while (it.hasNext()) {
                    JsonNode b = it.next();
                    result.add(new CtextBookDTO(
                            b.path("title").asText(""),
                            b.path("urn").asText("")
                    ));
                }
            }
            putCache(cacheKey, result);
            log.debug("ctext search '{}' returned {} books", title, result.size());
            return result;
        } catch (Exception e) {
            log.error("ctext searchtexts failed: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * 根据 URN 获取典籍全文
     * 需要 API key，否则返回 errorCode = ERR_REQUIRES_AUTHENTICATION
     *
     * @param urn ctext URN，如 ctp:analects/xue-er
     * @return 全文响应（含 fulltext 或 errorCode）
     */
    public CtextFulltextDTO getText(String urn) {
        if (urn == null || urn.isBlank()) {
            return errorDTO("ERR_INVALID_URN", "URN cannot be empty");
        }
        String cacheKey = "text:" + urn;
        CtextFulltextDTO cached = getCache(cacheKey, CtextFulltextDTO.class);
        if (cached != null) return cached;

        StringBuilder url = new StringBuilder(ctextProperties.getBaseUrl())
                .append("/gettext?urn=").append(encode(urn))
                .append("&if=en");
        if (!ctextProperties.getApiKey().isBlank()) {
            url.append("&apikey=").append(ctextProperties.getApiKey());
        }

        try {
            String json = getForString(url.toString());
            JsonNode root = objectMapper.readTree(json);

            // 错误响应
            JsonNode err = root.path("error");
            if (!err.isMissingNode() && err.isObject()) {
                CtextFulltextDTO dto = errorDTO(
                        err.path("code").asText(""),
                        err.path("description").asText("")
                );
                // 认证错误不缓存，便于 key 配置后立即生效
                if (!"ERR_REQUIRES_AUTHENTICATION".equals(dto.getErrorCode())) {
                    putCache(cacheKey, dto);
                }
                return dto;
            }

            CtextFulltextDTO dto = new CtextFulltextDTO();
            dto.setTitle(root.path("title").asText(""));
            List<String> fulltext = new ArrayList<>();
            JsonNode ft = root.path("fulltext");
            if (ft.isArray()) {
                ft.forEach(n -> fulltext.add(n.asText("")));
            }
            dto.setFulltext(fulltext);

            List<String> subs = new ArrayList<>();
            JsonNode sub = root.path("subsections");
            if (sub.isArray()) {
                sub.forEach(n -> subs.add(n.asText("")));
            }
            dto.setSubsections(subs);

            putCache(cacheKey, dto);
            log.debug("ctext gettext '{}' returned {} paragraphs", urn, fulltext.size());
            return dto;
        } catch (Exception e) {
            log.error("ctext gettext failed: {}", e.getMessage());
            return errorDTO("ERR_GENERIC", e.getMessage());
        }
    }

    /**
     * 查询 ctext 当前认证状态
     */
    public Map<String, Object> getStatus() {
        String url = ctextProperties.getBaseUrl() + "/getstatus";
        try {
            String json = getForString(url);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("ctext getstatus failed: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    /**
     * 从 ctext.org URL 反查 URN
     * 公开端点，无需 API key
     */
    public String readLink(String ctextUrl) {
        if (ctextUrl == null || ctextUrl.isBlank()) return "";
        String cacheKey = "readlink:" + ctextUrl;
        String cached = getCache(cacheKey, String.class);
        if (cached != null) return cached;

        String url = ctextProperties.getBaseUrl()
                + "/readlink?url=" + encode(ctextUrl);
        try {
            String json = getForString(url);
            JsonNode root = objectMapper.readTree(json);
            String urn = root.path("urn").asText("");
            putCache(cacheKey, urn);
            return urn;
        } catch (Exception e) {
            log.error("ctext readlink failed: {}", e.getMessage());
            return "";
        }
    }

    // ──────────────────────────────────────────────
    // Wikisource API（无需 API key，国内访问稳定）
    // ──────────────────────────────────────────────

    /**
     * 按书名搜索 Wikisource 典籍
     * 公开端点，无需 API key
     *
     * @param title 书名（中文/繁体均可）
     * @return 匹配的页面列表
     */
    public List<WikisourceBookDTO> searchWikisourceBooks(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }
        // trim 避免前导/尾随空格导致缓存 key 不一致
        String query = title.trim();
        String cacheKey = "ws:search:" + query;
        List<WikisourceBookDTO> cached = getCache(cacheKey, List.class);
        if (cached != null) return cached;

        String url = wikisourceProperties.getBaseUrl()
                + "?action=query&list=search&format=json"
                + "&srsearch=" + encode(query)
                + "&srlimit=" + wikisourceProperties.getSearchLimit()
                + "&srnamespace=0&origin=*";
        try {
            String json = getForString(url);
            log.info("wikisource search url={}, responseLength={}, preview={}",
                    url, json.length(), json.substring(0, Math.min(200, json.length())));
            JsonNode root = objectMapper.readTree(json);
            // 检查是否有错误响应
            JsonNode err = root.path("error");
            if (!err.isMissingNode()) {
                log.error("wikisource API returned error: {}", err.toString());
                return List.of();
            }
            JsonNode search = root.path("query").path("search");
            List<WikisourceBookDTO> result = new ArrayList<>();
            if (search.isArray()) {
                search.forEach(n -> result.add(new WikisourceBookDTO(
                        n.path("title").asText(""),
                        n.path("pageid").asLong(0),
                        n.path("snippet").asText("")
                )));
            }
            putCache(cacheKey, result);
            log.info("wikisource search '{}' returned {} pages", query, result.size());
            return result;
        } catch (Exception e) {
            log.error("wikisource search failed: {} | exceptionType={} | url={}",
                    e.getMessage(), e.getClass().getName(), url, e);
            return List.of();
        }
    }

    /**
     * 诊断方法：直接返回 Wikisource API 原始响应（用于排查连接/UA/编码问题）
     * 不走缓存，不做解析，直接返回原始字符串
     */
    public String debugWikisourceRaw(String title) {
        String query = (title == null ? "论语" : title).trim();
        String url = wikisourceProperties.getBaseUrl()
                + "?action=query&list=search&format=json"
                + "&srsearch=" + encode(query)
                + "&srlimit=3&srnamespace=0&origin=*";
        try {
            long start = System.currentTimeMillis();
            String json = getForString(url);
            long elapsed = System.currentTimeMillis() - start;
            return "{\"ok\":true,\"elapsedMs\":" + elapsed
                    + ",\"url\":\"" + url.replace("\"", "\\\"") + "\""
                    + ",\"responseLength\":" + json.length()
                    + ",\"response\":" + json + "}";
        } catch (Exception e) {
            return "{\"ok\":false,\"url\":\"" + url.replace("\"", "\\\"") + "\""
                    + ",\"exceptionType\":\"" + e.getClass().getName() + "\""
                    + ",\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    /**
     * 网络连通性诊断：测试 Java 进程能否连接多个外部目标
     * 用于定位是 Java 网络栈问题、防火墙问题、还是特定站点问题
     */
    public Map<String, Object> testConnectivity() {
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("javaVersion", System.getProperty("java.version"));
        result.put("preferIPv4Stack", System.getProperty("java.net.preferIPv4Stack"));
        result.put("preferIPv6Addresses", System.getProperty("java.net.preferIPv6Addresses"));
        result.put("httpProxyHost", System.getProperty("http.proxyHost"));
        result.put("httpsProxyHost", System.getProperty("https.proxyHost"));
        result.put("nonProxyHosts", System.getProperty("http.nonProxyHosts"));

        // 测试多个目标
        String[] targets = {
                "https://www.baidu.com",
                "https://api.ctext.org",
                "https://zh.wikisource.org",
                "https://103.102.166.224",
                "http://localhost:8080"
        };
        Map<String, Object> tests = new java.util.LinkedHashMap<>();
        for (String target : targets) {
            tests.put(target, testSingleTarget(target));
        }
        result.put("tests", tests);
        return result;
    }

    private Map<String, Object> testSingleTarget(String url) {
        Map<String, Object> r = new java.util.LinkedHashMap<>();
        long start = System.currentTimeMillis();
        try {
            java.net.Socket socket = new java.net.Socket();
            java.net.URI uri = java.net.URI.create(url);
            String host = uri.getHost();
            int port = uri.getPort();
            if (port == -1) {
                port = uri.getScheme().equals("https") ? 443 : 80;
            }
            // 解析 DNS
            long dnsStart = System.currentTimeMillis();
            java.net.InetAddress[] addrs = java.net.InetAddress.getAllByName(host);
            long dnsMs = System.currentTimeMillis() - dnsStart;
            r.put("dnsMs", dnsMs);
            r.put("resolvedAddresses", java.util.Arrays.stream(addrs)
                    .map(java.net.InetAddress::getHostAddress)
                    .limit(5)
                    .toArray(String[]::new));

            // 尝试 TCP 连接（用第一个地址）
            java.net.InetSocketAddress addr = new java.net.InetSocketAddress(addrs[0], port);
            socket.connect(addr, 5000);
            long elapsed = System.currentTimeMillis() - start;
            r.put("ok", true);
            r.put("connectedTo", addrs[0].getHostAddress());
            r.put("elapsedMs", elapsed);
            socket.close();
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            r.put("ok", false);
            r.put("elapsedMs", elapsed);
            r.put("exceptionType", e.getClass().getName());
            r.put("message", e.getMessage());
        }
        return r;
    }

    /**
     * 根据页面标题获取 Wikisource 全文
     * 公开端点，无需 API key
     *
     * @param pageTitle 页面标题（如"論語/學而第一"）
     * @return 全文响应（含已解析的纯文本）
     */
    public WikisourceFulltextDTO getWikisourceText(String pageTitle) {
        if (pageTitle == null || pageTitle.isBlank()) {
            return wsErrorDTO("ERR_INVALID_PAGE", "Page title cannot be empty");
        }
        // trim 避免前导/尾随空格导致缓存 key 不一致
        String page = pageTitle.trim();
        String cacheKey = "ws:text:" + page;
        WikisourceFulltextDTO cached = getCache(cacheKey, WikisourceFulltextDTO.class);
        if (cached != null) return cached;

        String url = wikisourceProperties.getBaseUrl()
                + "?action=parse&format=json&prop=wikitext"
                + "&page=" + encode(page)
                + "&origin=*";
        try {
            String json = getForString(url);
            JsonNode root = objectMapper.readTree(json);

            // 错误响应
            JsonNode err = root.path("error");
            if (!err.isMissingNode() && err.isObject()) {
                WikisourceFulltextDTO dto = wsErrorDTO(
                        err.path("code").asText(""),
                        err.path("info").asText("")
                );
                putCache(cacheKey, dto);
                return dto;
            }

            JsonNode parse = root.path("parse");
            String title = parse.path("title").asText("");
            String wikitext = parse.path("wikitext").path("*").asText("");

            WikisourceFulltextDTO dto = new WikisourceFulltextDTO();
            dto.setTitle(title);
            dto.setFulltext(parseWikitext(wikitext));
            dto.setSubsections(extractSubsections(wikitext));
            putCache(cacheKey, dto);
            log.debug("wikisource parse '{}' returned {} chars", page, dto.getFulltext().length());
            return dto;
        } catch (Exception e) {
            log.error("wikisource parse failed: {}", e.getMessage());
            return wsErrorDTO("ERR_GENERIC", e.getMessage());
        }
    }

    /**
     * 解析 wikitext 为纯文本
     * 处理：模板{{...}}、HTML标签、wiki链接[[...]]、注释<!--...-->
     */
    private String parseWikitext(String wikitext) {
        if (wikitext == null || wikitext.isEmpty()) return "";

        String s = wikitext;
        // 移除 HTML 注释
        s = s.replaceAll("<!--.*?-->", "");
        // 移除模板 {{...}}（非贪婪，多层嵌套用循环）
        Pattern templatePattern = Pattern.compile("\\{\\{[^{}]*\\}\\}");
        int prevLen;
        do {
            prevLen = s.length();
            Matcher m = templatePattern.matcher(s);
            s = m.replaceAll("");
        } while (s.length() != prevLen);
        // 移除语言变体标记 -{zh-hant:...; zh-hans:...}-（可能跨行，非贪婪）
        s = s.replaceAll("-\\{[^}]*\\}-", "");
        // 移除 HTML 标签
        s = s.replaceAll("<[^>]+>", "");
        // 移除 <onlyinclude> 等标签内容已被上面处理
        // 处理 wiki 链接 [[目标|显示文字]] -> 显示文字
        Pattern linkPattern = Pattern.compile("\\[\\[([^\\]|]*)\\|?([^\\]]*)\\]\\]");
        Matcher lm = linkPattern.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (lm.find()) {
            String display = lm.group(2);
            if (display == null || display.isEmpty()) {
                display = lm.group(1);
            }
            lm.appendReplacement(sb, Matcher.quoteReplacement(display));
        }
        lm.appendTail(sb);
        s = sb.toString();
        // 移除外链 [http://... 文字] -> 文字
        s = s.replaceAll("\\[https?://[^\\s]*\\s([^\\]]*)\\]", "$1");
        s = s.replaceAll("\\[https?://[^\\]]*\\]", "");
        // 移除表格标记 {| ... |}
        s = s.replaceAll("\\{\\|.*?\\|\\}", "");
        // 移除列表/缩进标记行首
        s = s.replaceAll("(?m)^[\\*:;#]+\\s*", "");
        // 移除加粗/斜体
        s = s.replaceAll("'{2,}", "");
        // 移除分类标记 [[Category:xxx]] 或 [[分類:xxx]]
        s = s.replaceAll("\\[\\[(?:Category|分類|分类):[^\\]]*\\]\\]", "");
        // 移除独立成行的文件链接 [[File:xxx]] / [[圖片:xxx]]
        s = s.replaceAll("\\[\\[(?:File|Image|圖片|图片|文件):[^\\]]*\\]\\]", "");
        // 压缩多空行
        s = s.replaceAll("\\n{3,}", "\n\n");
        return s.trim();
    }

    /** 从 wikitext 提取子章节链接（形如 [[/學而第一|學而第一]]） */
    private List<String> extractSubsections(String wikitext) {
        List<String> subs = new ArrayList<>();
        if (wikitext == null) return subs;
        Pattern p = Pattern.compile("\\[\\[/([^|\\]]+)\\|?([^\\]]*)\\]\\]");
        Matcher m = p.matcher(wikitext);
        while (m.find()) {
            String display = m.group(2);
            if (display == null || display.isEmpty()) {
                display = m.group(1);
            }
            subs.add(display);
        }
        return subs;
    }

    private WikisourceFulltextDTO wsErrorDTO(String code, String desc) {
        WikisourceFulltextDTO dto = new WikisourceFulltextDTO();
        dto.setErrorCode(code);
        dto.setErrorDescription(desc);
        return dto;
    }

    // ──────────────────────────────────────────────
    // AI 古文翻译（GLM-4-Flash）
    // ──────────────────────────────────────────────

    /** 翻译缓存 key 前缀（v2: prompt 注入前文上下文，与 v1 不兼容，旧缓存自动失效） */
    private static final String TRANSLATE_CACHE_PREFIX = "translate:v2:";

    /** 单段翻译最大字符数（GLM-4-Flash 免费模型对长文本不稳定，800 字以内稳定） */
    private static final int TRANSLATE_CHUNK_SIZE = 800;

    /** 单段翻译失败时的最大重试次数 */
    private static final int TRANSLATE_MAX_RETRIES = 1;

    /**
     * 单段翻译结果（L1：用 success 标志判断失败，不依赖字符串前缀）
     *
     * @param translation 译文内容（失败时为错误提示文案）
     * @param success     是否翻译成功
     */
    public record ChunkTranslation(String translation, boolean success) {}

    /**
     * AI 古文翻译（流式 SSE 版） — 每段翻译完成后立即推送给前端
     * 用于优化首屏体验，避免等待所有段翻译完成才显示
     *
     * SSE 事件格式：
     *   event: chunk  data: {"index":1,"total":4,"translation":"...","done":false,"cached":false}
     *   event: done   data: {"total":4,"success":3,"fail":1,"cached":false}
     *   event: error  data: {"message":"..."}
     *
     * @param originalText 古文原文
     * @param title        典籍标题
     * @param emitter      SSE 发射器
     */
    public void translateClassicStream(String originalText, String title, SseEmitter emitter) {
        if (originalText == null || originalText.isBlank()) {
            sendSseEvent(emitter, "done", Map.of("total", 0, "success", 0, "fail", 0, "cached", false));
            return;
        }

        // L2: cacheKey 用 SHA-256 避免 hashCode 碰撞导致不同原文共用译文
        String cacheKey = TRANSLATE_CACHE_PREFIX + sha256(originalText);
        String cached = getCache(cacheKey, String.class);
        if (cached != null) {
            log.info("translate stream cache hit key={} title={}", cacheKey, title);
            // 缓存命中：一次性发送完整译文
            sendSseEvent(emitter, "chunk", Map.of(
                    "index", 1, "total", 1,
                    "translation", cached,
                    "done", true, "cached", true));
            sendSseEvent(emitter, "done", Map.of(
                    "total", 1, "success", 1, "fail", 0, "cached", true));
            return;
        }

        List<String> chunks = splitIntoChunks(originalText, TRANSLATE_CHUNK_SIZE);
        int total = chunks.size();
        log.info("translate stream start title={} totalLen={} chunks={}",
                title, originalText.length(), total);

        StringBuilder fullResult = new StringBuilder();
        int successCount = 0;
        int failCount = 0;
        // 上下文连贯：保存前一段的原文和译文末尾，注入下一段 prompt 保持术语一致
        String prevOriginal = null;
        String prevTranslation = null;

        for (int i = 0; i < chunks.size(); i++) {
            // M1: 检查线程中断（emitter timeout/error 时 Controller 会 interrupt worker）
            if (Thread.interrupted()) {
                log.warn("translate stream interrupted at chunk {}/{}", i + 1, total);
                sendSseEvent(emitter, "error", Map.of("message", "翻译已取消（超时或客户端断开）"));
                return;
            }

            String chunk = chunks.get(i);
            // L1: 用 ChunkTranslation.success 判断失败，不依赖字符串前缀
            // 上下文连贯：传入前一段原文/译文末尾，让 LLM 保持人名地名一致
            ChunkTranslation result = translateChunk(chunk, title, i + 1, total, prevOriginal, prevTranslation);
            if (result.success()) {
                successCount++;
                // 更新前文上下文（只保留末尾 200 字，控制 token 消耗）
                prevOriginal = truncateEnd(chunk, 200);
                prevTranslation = truncateEnd(result.translation(), 200);
            } else {
                failCount++;
                // 翻译失败时不更新上下文，避免错误文案污染下一段
            }
            if (i > 0) fullResult.append("\n\n");
            fullResult.append(result.translation());

            // 推送当前段
            Map<String, Object> chunkData = new HashMap<>();
            chunkData.put("index", i + 1);
            chunkData.put("total", total);
            chunkData.put("translation", result.translation());
            chunkData.put("done", i == total - 1);
            chunkData.put("cached", false);
            if (!sendSseEvent(emitter, "chunk", chunkData)) {
                // 客户端已断开，停止翻译
                log.warn("translate stream client disconnected at chunk {}/{}", i + 1, total);
                return;
            }
            log.info("translate stream sent chunk {}/{} title={}", i + 1, total, title);
        }

        // 缓存完整结果（至少一段成功才缓存）
        if (successCount > 0) {
            putCache(cacheKey, fullResult.toString());
        }

        sendSseEvent(emitter, "done", Map.of(
                "total", total, "success", successCount, "fail", failCount, "cached", false));
        log.info("translate stream done title={} success={} fail={}",
                title, successCount, failCount);
    }

    /**
     * 发送 SSE 事件，返回是否发送成功（false 表示客户端已断开）
     */
    private boolean sendSseEvent(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
            return true;
        } catch (IOException | IllegalStateException e) {
            log.warn("SSE send failed event={}: {}", eventName, e.getMessage());
            return false;
        }
    }

    /**
     * 将长文本按段落边界分割成不超过 maxChars 字的段
     * 优先在换行符处分割，其次在句号处，最后强制按 maxChars 截断
     */
    private List<String> splitIntoChunks(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();
        if (text.length() <= maxChars) {
            chunks.add(text);
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxChars, text.length());
            if (end < text.length()) {
                // 优先在换行符处分割
                int nlPos = text.lastIndexOf('\n', end);
                if (nlPos > start + maxChars / 2) {
                    end = nlPos + 1;
                } else {
                    // 其次在句号处分割
                    int periodPos = Math.max(
                            text.lastIndexOf('。', end),
                            text.lastIndexOf('；', end));
                    if (periodPos > start + maxChars / 2) {
                        end = periodPos + 1;
                    }
                }
            }
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }

    /**
     * 翻译单段古文，带重试机制
     * L1: 返回 ChunkTranslation（含 success 标志），调用方用 .success() 判断失败
     * 上下文连贯：传入前一段原文/译文末尾，让 LLM 保持人名地名等术语一致
     *
     * @param chunk            当前段原文
     * @param title            典籍标题
     * @param chunkIdx         当前段序号（1-based）
     * @param totalChunks      总段数
     * @param prevOriginal     前一段原文末尾（可为 null，首段为 null）
     * @param prevTranslation  前一段译文末尾（可为 null，首段为 null）
     */
    private ChunkTranslation translateChunk(String chunk, String title, int chunkIdx, int totalChunks,
                                            String prevOriginal, String prevTranslation) {
        String systemPrompt = "你是古文翻译专家，精通先秦至明清各类古籍。"
                + "请将给定的古文翻译为现代白话文，要求：\n"
                + "1. 忠于原文，不增删内容\n"
                + "2. 用流畅的现代汉语表达\n"
                + "3. 保留原文的段落结构\n"
                + "4. 只输出译文，不要解释或注释\n"
                + "5. 人名、地名、官职名等专有名词保留原词\n"
                + "6. 全文术语保持一致（参考前文译文，同一人名/地名用相同译法）";

        String chunkHint = totalChunks > 1 ? "（第 " + chunkIdx + "/" + totalChunks + " 段）\n\n" : "";
        // 上下文连贯：从第二段起注入前文末尾，让 LLM 知道前文用了什么术语
        String contextHint = "";
        if (prevOriginal != null && !prevOriginal.isBlank()
                && prevTranslation != null && !prevTranslation.isBlank()) {
            contextHint = "【前文参考（请保持术语一致）】\n"
                    + "前文原文末尾：\n" + prevOriginal + "\n\n"
                    + "前文译文末尾：\n" + prevTranslation + "\n\n"
                    + "【请翻译以下古文】\n\n";
        }
        String userPrompt = (title != null && !title.isBlank() ? "典籍：" + title + "\n\n" : "")
                + contextHint
                + (contextHint.isEmpty() ? "请翻译以下古文：\n\n" + chunkHint + chunk : chunkHint + chunk);

        LlmChatRequest chatRequest = LlmChatRequest.builder()
                .messages(List.of(
                        new LlmMessage("system", systemPrompt),
                        new LlmMessage("user", userPrompt)
                ))
                .maxTokens(4096)
                .temperature(0.3)
                .build();

        for (int attempt = 0; attempt <= TRANSLATE_MAX_RETRIES; attempt++) {
            try {
                String translation = llmService.chat(chatRequest);
                if (translation == null || translation.isBlank()) {
                    log.warn("translate chunk empty chunk={}/{} attempt={}", chunkIdx, totalChunks, attempt);
                    if (attempt < TRANSLATE_MAX_RETRIES) {
                        Thread.sleep(500);
                        continue;
                    }
                    return new ChunkTranslation("（翻译失败：LLM 返回空内容）", false);
                }
                return new ChunkTranslation(translation.trim(), true);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return new ChunkTranslation("（翻译失败：线程中断）", false);
            } catch (Exception e) {
                log.warn("translate chunk failed chunk={}/{} attempt={} | {}",
                        chunkIdx, totalChunks, attempt, e.getMessage());
                if (attempt < TRANSLATE_MAX_RETRIES) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return new ChunkTranslation("（翻译失败：线程中断）", false);
                    }
                    continue;
                }
                return new ChunkTranslation("（翻译失败：" + e.getMessage() + "）", false);
            }
        }
        return new ChunkTranslation("（翻译失败：重试次数耗尽）", false);
    }

    // ──────────────────────────────────────────────
    // 辅助方法
    // ──────────────────────────────────────────────

    private String encode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    /**
     * L2: 计算字符串 SHA-256，用于翻译缓存 key
     * 比 String.hashCode() 更抗碰撞（hashCode 仅 32 位，碰撞概率高）
     */
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 是 JDK 标准算法，理论上不会缺失；兜底用 hashCode
            log.warn("SHA-256 unavailable, fallback to hashCode");
            return Integer.toString(input.hashCode());
        }
    }

    /**
     * 上下文连贯：截取字符串末尾 maxLen 字符，用于注入下一段 prompt
     * 保留末尾而非开头，因为末尾与当前段衔接最紧密
     */
    private String truncateEnd(String s, int maxLen) {
        if (s == null || s.length() <= maxLen) return s;
        return s.substring(s.length() - maxLen);
    }

    @SuppressWarnings("unchecked")
    private <T> T getCache(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry == null) return null;
        if (Instant.now().toEpochMilli() > entry.expireAt) {
            cache.remove(key);
            return null;
        }
        // 类型匹配检查
        Object v = entry.value;
        if (type.isInstance(v) || (type == List.class && v instanceof List)) {
            return (T) v;
        }
        return null;
    }

    private void putCache(String key, Object value) {
        long expireAt = Instant.now().toEpochMilli() + ctextProperties.getCacheTtlMs();
        cache.put(key, new CacheEntry(value, expireAt));
    }

    private CtextFulltextDTO errorDTO(String code, String desc) {
        CtextFulltextDTO dto = new CtextFulltextDTO();
        dto.setErrorCode(code);
        dto.setErrorDescription(desc);
        return dto;
    }

    /** 缓存条目 */
    private record CacheEntry(Object value, long expireAt) {}
}
