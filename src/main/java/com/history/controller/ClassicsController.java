package com.history.controller;

import com.history.dto.CtextBookDTO;
import com.history.dto.CtextFulltextDTO;
import com.history.dto.WikisourceBookDTO;
import com.history.dto.WikisourceFulltextDTO;
import com.history.service.ClassicsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * 典籍 API 控制器
 * 双源代理：ctext.org（权威但需 key） + Wikisource（无需 key，国内快速）
 *
 * ctext 端点：
 * - GET /api/classics/search?title=xxx            按书名搜索 ctext 典籍
 * - GET /api/classics/text/{urn}                  根据 URN 获取典籍全文
 * - GET /api/classics/status                      查询 ctext 认证状态
 * - GET /api/classics/readlink                    从 ctext URL 反查 URN
 *
 * Wikisource 端点：
 * - GET /api/classics/wikisource/search?title=xxx 按书名搜索 Wikisource 页面
 * - GET /api/classics/wikisource/text?page=xxx    根据页面标题获取 Wikisource 全文
 *
 * AI 翻译端点（SSE 流式）：
 * - POST /api/classics/translate                  调用 GLM-4-Flash 将古文翻译为白话文，逐段推送
 */
@Slf4j
@RestController
@RequestMapping("/api/classics")
@RequiredArgsConstructor
@Tag(name = "典籍", description = "典籍原典代理接口（ctext + Wikisource 双源）+ AI 翻译")
public class ClassicsController {

    private final ClassicsService classicsService;

    // ──────────────────────────────────────────────
    // ctext.org 端点
    // ──────────────────────────────────────────────

    @GetMapping("/search")
    @Operation(summary = "按书名搜索 ctext 典籍", description = "公开端点，无需 API key")
    public ResponseEntity<List<CtextBookDTO>> search(@RequestParam String title) {
        return ResponseEntity.ok(classicsService.searchBooks(title));
    }

    @GetMapping("/text/{urn}")
    @Operation(summary = "根据 URN 获取 ctext 典籍全文", description = "需 API key，未配置时返回 ERR_REQUIRES_AUTHENTICATION")
    public ResponseEntity<CtextFulltextDTO> getText(@PathVariable String urn) {
        return ResponseEntity.ok(classicsService.getText(urn));
    }

    @GetMapping("/status")
    @Operation(summary = "查询 ctext 认证状态")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(classicsService.getStatus());
    }

    @GetMapping("/readlink")
    @Operation(summary = "从 ctext URL 反查 URN", description = "公开端点，无需 API key")
    public ResponseEntity<Map<String, String>> readLink(@RequestParam String url) {
        String urn = classicsService.readLink(url);
        return ResponseEntity.ok(Map.of("urn", urn));
    }

    // ──────────────────────────────────────────────
    // Wikisource 端点（无需 API key，国内访问稳定）
    // ──────────────────────────────────────────────

    @GetMapping("/wikisource/search")
    @Operation(summary = "按书名搜索 Wikisource 典籍", description = "无需 API key，国内访问稳定")
    public ResponseEntity<List<WikisourceBookDTO>> searchWikisource(@RequestParam String title) {
        return ResponseEntity.ok(classicsService.searchWikisourceBooks(title));
    }

    @GetMapping("/wikisource/text")
    @Operation(summary = "根据页面标题获取 Wikisource 全文", description = "返回已解析的纯文本，无需 API key")
    public ResponseEntity<WikisourceFulltextDTO> getWikisourceText(@RequestParam String page) {
        return ResponseEntity.ok(classicsService.getWikisourceText(page));
    }

    @GetMapping("/wikisource/diag")
    @Operation(summary = "诊断 Wikisource API 连通性", description = "返回原始响应、URL、耗时、异常类型，用于排查问题")
    public ResponseEntity<String> debugWikisource(@RequestParam(defaultValue = "论语") String title) {
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(classicsService.debugWikisourceRaw(title));
    }

    @GetMapping("/netdiag")
    @Operation(summary = "网络连通性诊断", description = "测试 Java 进程能否连接多个外部目标，定位网络问题")
    public ResponseEntity<Map<String, Object>> netdiag() {
        return ResponseEntity.ok(classicsService.testConnectivity());
    }

    // ──────────────────────────────────────────────
    // AI 翻译端点（GLM-4-Flash，SSE 流式）
    // ──────────────────────────────────────────────

    /**
     * 翻译请求体
     * H2: 限制 text 最大 20000 字，防止恶意大请求耗尽 LLM 资源
     * （正常典籍原文最长约 5000 字，20000 留充足余量）
     */
    public record TranslateRequest(
            @NotBlank(message = "text 不能为空") @Size(max = 20000, message = "text 不能超过 20000 字") String text,
            @Size(max = 200, message = "title 不能超过 200 字") String title) {}

    @PostMapping(value = "/translate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI 翻译古文为白话文（流式 SSE）",
            description = "调用 GLM-4-Flash，800 字分段翻译，每段完成立即推送 chunk 事件；带 24 小时缓存，命中时一次性返回")
    public ResponseEntity<SseEmitter> translate(@Valid @RequestBody TranslateRequest request) {
        // 2 分钟超时（长原文多段翻译需要时间）
        SseEmitter emitter = new SseEmitter(120_000L);
        // 翻译是耗时操作，在 daemon 线程执行避免阻塞请求线程
        Thread worker = new Thread(() -> {
            try {
                classicsService.translateClassicStream(request.text(), request.title(), emitter);
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error")
                            .data(Map.of("message", e.getMessage() == null ? "未知错误" : e.getMessage())));
                } catch (Exception ignored) {
                    // 发送 error 事件失败，客户端可能已断开
                }
                emitter.completeWithError(e);
            }
        });
        worker.setDaemon(true);
        worker.setName("translate-stream-" + worker.getId());

        // M1: 注册 emitter 回调，客户端断开/超时时中断 worker，避免浪费 LLM 调用
        emitter.onTimeout(() -> {
            log.warn("SSE timeout, interrupting worker {}", worker.getName());
            worker.interrupt();
            emitter.complete();
        });
        emitter.onError((e) -> {
            log.warn("SSE error: {}", e.getMessage());
            worker.interrupt();
        });
        emitter.onCompletion(() -> {
            // 正常完成时不 interrupt（让 worker 自然结束）；仅打日志
            log.debug("SSE completed for worker {}", worker.getName());
        });

        worker.start();
        return ResponseEntity.ok(emitter);
    }
}
