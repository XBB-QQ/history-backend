package com.history.controller;

import com.history.dto.LlmChatRequest;
import com.history.service.LlmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

/**
 * LLM 代理 Controller — 前端统一调用此接口，API Key 在后端管理
 */
@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
@Tag(name = "LLM 代理", description = "前端 LLM 调用代理，API Key 在后端管理")
public class LlmController {

    private final LlmService llmService;

    @PostMapping("/chat")
    @Operation(summary = "非流式对话", description = "调用 LLM 生成完整回答")
    public ResponseEntity<Map<String, String>> chat(@RequestBody LlmChatRequest request) {
        String content = llmService.chat(request);
        return ResponseEntity.ok(Map.of("content", content));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话（SSE 透传）", description = "调用 LLM 流式生成，SSE 原样透传给前端")
    public StreamingResponseBody chatStream(@RequestBody LlmChatRequest request) {
        return out -> llmService.chatStream(request, out);
    }
}
