package com.history.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.CharEvolutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 汉字字源数据抓取器
 * 从 hanziyuan.net（汉字叔叔字源数据库）抓取任意汉字的真实字源 SVG 数据
 * 数据源：https://hanziyuan.net/etymology（POST endpoint，需 cookie + 自定义 header）
 *
 * 反爬绕过关键点：
 * - 必须先 GET 主页获取 Oracle/Bronze cookie（ASP.NET Core antiforgery token）
 * - POST body 须带 Bronze 字段
 * - 须带 Chinese（codepoint）、Seal（Bronze 值）自定义 header
 * - Java HttpClient 默认 TLS 指纹与浏览器接近，能成功（curl/PowerShell 会被识别为 bot 返回 404）
 *
 * 缓存：内存 ConcurrentHashMap + 24h TTL，避免重复抓取
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HanziyuanFetcher {

    private final ObjectMapper objectMapper;

    private static final String HOME_URL = "https://hanziyuan.net/";
    private static final String ETYMOLOGY_URL = "https://hanziyuan.net/etymology";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /** 5 个阶段名称（hanziyuan 网站使用的标题） */
    private static final String[] STAGE_NAMES = {"甲骨文", "金文", "篆字", "隶书", "楷书"};
    /** 楷书阶段如果缺失，用「篆书」作为后备（部分字可能没有楷书阶段） */
    private static final String[] STAGE_FALLBACK = {"篆书", "篆字", "隶书"};

    /** 缓存 24 小时 */
    private static final long CACHE_TTL_MS = 24L * 60 * 60 * 1000;
    private static final Map<String, CacheEntry> CACHE = new ConcurrentHashMap<>();

    /** 阶段标题正则：匹配「甲骨文 (42)」这种形式 */
    private static final Pattern STAGE_HEADER_PATTERN =
            Pattern.compile("(甲骨文|金文|篆字|篆书|隶书|楷书)\\s*\\(\\s*(\\d+)\\s*\\)");

    /** data:image/svg+xml;base64,XXX 数据 URI 正则 */
    private static final Pattern DATA_URI_PATTERN =
            Pattern.compile("data:image/svg\\+xml;base64,([A-Za-z0-9+/=]+)");

    private HttpClient httpClient;
    private CookieManager cookieManager;

    @PostConstruct
    void init() {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        log.info("HanziyuanFetcher 初始化完成");
    }

    /** 抓取并解析汉字字源数据；返回 null 表示抓取失败 */
    public CharEvolutionResponse fetch(String ch) {
        if (ch == null || ch.isBlank()) return null;

        // 命中缓存
        CacheEntry cached = CACHE.get(ch);
        if (cached != null && cached.expiresAt > Instant.now().toEpochMilli()) {
            log.debug("hanziyuan cache hit: {}", ch);
            return cached.data;
        }
        if (cached != null) {
            CACHE.remove(ch);
        }

        try {
            String html = fetchHtml(ch);
            if (html == null || html.isEmpty()) return null;
            CharEvolutionResponse data = parseHtml(ch, html);
            if (data != null) {
                CACHE.put(ch, new CacheEntry(data, Instant.now().toEpochMilli() + CACHE_TTL_MS));
                log.info("hanziyuan 抓取成功: {} ({} 阶段)", ch, data.getStages().size());
            }
            return data;
        } catch (Exception e) {
            log.warn("hanziyuan 抓取失败: {} - {}", ch, e.getMessage());
            return null;
        }
    }

    /** GET 主页 + POST /etymology 拿 HTML */
    private String fetchHtml(String ch) throws Exception {
        // 1. GET 主页刷新 cookie（每次抓取都重新拿，避免 cookie 过期）
        HttpRequest homeReq = HttpRequest.newBuilder()
                .uri(URI.create(HOME_URL))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .GET()
                .build();
        httpClient.send(homeReq, HttpResponse.BodyHandlers.ofString());

        // 取 Bronze cookie
        String bronze = cookieManager.getCookieStore().getCookies().stream()
                .filter(c -> c.getName().equals("Bronze"))
                .map(java.net.HttpCookie::getValue)
                .findFirst()
                .orElse("");
        if (bronze.isEmpty()) {
            log.warn("未拿到 Bronze cookie");
            return null;
        }

        // 2. POST /etymology
        String body = "chinese=" + URLEncoder.encode(ch, StandardCharsets.UTF_8)
                + "&Bronze=" + URLEncoder.encode(bronze, StandardCharsets.UTF_8);
        HttpRequest etyReq = HttpRequest.newBuilder()
                .uri(URI.create(ETYMOLOGY_URL))
                .header("User-Agent", USER_AGENT)
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", HOME_URL)
                .header("Origin", HOME_URL.substring(0, HOME_URL.length() - 1))
                .header("Chinese", String.valueOf(ch.codePointAt(0)))
                .header("Seal", bronze)
                .header("Accept", "*/*")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = httpClient.send(etyReq, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            log.warn("hanziyuan POST /etymology 返回 {} for {}", resp.statusCode(), ch);
            return null;
        }
        return resp.body();
    }

    /** 解析 HTML，提取 5 阶段的第 1 个字形 SVG，构造 CharEvolutionResponse */
    private CharEvolutionResponse parseHtml(String ch, String html) {
        // 1. 找各阶段标题位置
        Map<String, Integer> stagePositions = new LinkedHashMap<>();
        Matcher m = STAGE_HEADER_PATTERN.matcher(html);
        while (m.find()) {
            String name = m.group(1);
            // 篆字/篆书 统一为「篆字」
            if (name.equals("篆书")) name = "篆字";
            if (!stagePositions.containsKey(name)) {
                stagePositions.put(name, m.start());
            }
        }
        if (stagePositions.isEmpty()) {
            log.warn("未找到阶段标题: {}", ch);
            return null;
        }

        // 2. 找所有 data URI 及其在 HTML 中的位置
        List<int[]> uriPositions = new ArrayList<>(); // {start, end}
        List<String> uriValues = new ArrayList<>();
        Matcher dm = DATA_URI_PATTERN.matcher(html);
        while (dm.find()) {
            uriPositions.add(new int[]{dm.start(), dm.end()});
            uriValues.add(dm.group(0));
        }
        if (uriPositions.isEmpty()) {
            log.warn("未找到 SVG data URI: {}", ch);
            return null;
        }

        // 3. 为每个阶段分配 data URI（按位置落在阶段区间内）
        // 阶段区间：[stagePos, nextStagePos)
        List<String> stageOrder = new ArrayList<>(stagePositions.keySet());
        Map<String, String> stageToFirstSvg = new LinkedHashMap<>();
        for (int i = 0; i < stageOrder.size(); i++) {
            String stage = stageOrder.get(i);
            int stageStart = stagePositions.get(stage);
            int stageEnd = (i + 1 < stageOrder.size()) ? stagePositions.get(stageOrder.get(i + 1)) : html.length();
            // 找该区间内第 1 个 data URI
            for (int j = 0; j < uriPositions.size(); j++) {
                int uriStart = uriPositions.get(j)[0];
                if (uriStart >= stageStart && uriStart < stageEnd) {
                    stageToFirstSvg.put(stage, uriValues.get(j));
                    break;
                }
            }
        }

        // 4. 构造 5 阶段（缺失阶段用 fallback）
        List<CharEvolutionResponse.CharStageDTO> stages = new ArrayList<>();
        for (String stageName : STAGE_NAMES) {
            String svgDataUri = stageToFirstSvg.get(stageName);
            if (svgDataUri == null) {
                // fallback：用其他阶段的数据
                for (String fb : STAGE_FALLBACK) {
                    if (stageToFirstSvg.containsKey(fb)) {
                        svgDataUri = stageToFirstSvg.get(fb);
                        break;
                    }
                }
                if (svgDataUri == null) continue;
            }
            String svgXml = decodeDataUri(svgDataUri);
            if (svgXml == null) continue;

            String era = getEraForStage(stageName);
            String desc = getDescriptionForStage(stageName, ch);
            stages.add(new CharEvolutionResponse.CharStageDTO(
                    stageName, era, desc, null, svgXml));
        }

        if (stages.isEmpty()) {
            log.warn("未提取到任何阶段 SVG: {}", ch);
            return null;
        }

        String meaning = extractMeaning(html, ch);
        return new CharEvolutionResponse(ch, meaning, stages);
    }

    /** 解码 data:image/svg+xml;base64,XXX 为原始 SVG XML */
    private String decodeDataUri(String dataUri) {
        try {
            String b64 = dataUri.substring(dataUri.indexOf(',') + 1);
            byte[] bytes = Base64.getDecoder().decode(b64);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("base64 解码失败: {}", e.getMessage());
            return null;
        }
    }

    /** 从 HTML 中提取「Original meaning 本义」字段 */
    private String extractMeaning(String html, String ch) {
        Pattern p = Pattern.compile("Original meaning[^:]*:\\s*([^<\\n]+)");
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "汉字「" + ch + "」的字源演变";
    }

    private String getEraForStage(String stage) {
        return switch (stage) {
            case "甲骨文" -> "商（前14-11世纪）";
            case "金文" -> "西周（前10-8世纪）";
            case "篆字" -> "秦（前3世纪）";
            case "隶书" -> "汉（前2-2世纪）";
            case "楷书" -> "魏晋至今（3世纪起）";
            default -> "";
        };
    }

    private String getDescriptionForStage(String stage, String ch) {
        return switch (stage) {
            case "甲骨文" -> "商代甲骨文中的「" + ch + "」字象形";
            case "金文" -> "西周金文中的「" + ch + "」字，线条更加圆润";
            case "篆字" -> "秦代小篆中的「" + ch + "」字，结构规整";
            case "隶书" -> "汉代隶书中的「" + ch + "」字，蚕头燕尾";
            case "楷书" -> "楷书「" + ch + "」字，方正端庄，沿用至今";
            default -> "";
        };
    }

    private record CacheEntry(CharEvolutionResponse data, long expiresAt) {}
}
