package com.history.controller;

import com.history.dto.RagQueryRequest;
import com.history.service.RagService;
import com.history.service.VectorStore;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 问答接口 — 检索增强生成
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;
    private final VectorStore vectorStore;

    /**
     * 流式查询 — SSE 格式返回
     * Content-Type: text/event-stream
     */
    @PostMapping(value = "/query", produces = "text/event-stream;charset=UTF-8")
    public void queryStream(@Valid @RequestBody RagQueryRequest request,
                            HttpServletResponse response) throws IOException {
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (OutputStream out = response.getOutputStream()) {
            ragService.queryStream(request, out);
        } catch (Exception e) {
            log.error("RAG 流式查询失败: {}", e.getMessage(), e);
            writeErrorSse(response.getOutputStream(), e.getMessage());
        }
    }

    /**
     * 非流式查询 — 返回完整 JSON
     */
    @PostMapping("/chat")
    public Map<String, String> query(@Valid @RequestBody RagQueryRequest request) {
        String answer = ragService.query(request);
        Map<String, String> result = new HashMap<>();
        result.put("answer", answer);
        return result;
    }

    /**
     * 仅检索 — 返回相关文档列表，不调用 LLM
     */
    @PostMapping("/retrieve")
    public List<Map<String, Object>> retrieve(@Valid @RequestBody RagQueryRequest request) {
        return ragService.retrieve(request).stream()
                .map(r -> {
                    Map<String, Object> item = new HashMap<>(r.metadata());
                    item.put("id", r.id());
                    item.put("score", r.score());
                    return item;
                })
                .toList();
    }

    /**
     * 向量库状态
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("vectorCount", vectorStore.size());
        status.put("storeType", System.getProperty("rag.vector-store", "memory"));
        return status;
    }

    private void writeErrorSse(OutputStream out, String message) throws IOException {
        String errorEvent = "data: {\"error\":\"" + message.replace("\"", "\\\"") + "\"}\n\n";
        out.write(errorEvent.getBytes("UTF-8"));
        out.flush();
    }
}
