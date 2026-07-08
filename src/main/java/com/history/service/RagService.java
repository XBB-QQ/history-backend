package com.history.service;

import com.history.config.RagProperties;
import com.history.dto.LlmChatRequest;
import com.history.dto.LlmMessage;
import com.history.dto.RagQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG 核心服务 — 检索增强生成
 * 流程：embed(question) → search(topK) → buildPrompt(context+question) → chatStream
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final LlmService llmService;
    private final RagProperties ragProps;

    /**
     * 流式查询 — 将 LLM SSE 流原样写入 outputStream
     */
    public void queryStream(RagQueryRequest request, OutputStream outputStream) {
        String question = request.getQuestion();
        log.info("RAG 查询: {}", question);

        // 1. 嵌入查询
        float[] queryVector = embeddingService.embed(question);

        // 2. 检索相关文档
        List<VectorStore.SearchResult> results = vectorStore.search(queryVector, ragProps.getTopK());
        log.info("RAG 检索到 {} 条相关文档", results.size());

        // 3. 过滤文档类型（如果指定）
        if (request.getDocType() != null && !request.getDocType().isBlank()) {
            results = results.stream()
                    .filter(r -> request.getDocType().equals(r.metadata().get("type")))
                    .collect(Collectors.toList());
            log.info("过滤 docType={} 后剩余 {} 条", request.getDocType(), results.size());
        }

        // 4. 构建上下文
        String context = buildContext(results);

        // 5. 构建 LLM 请求
        LlmChatRequest chatRequest = LlmChatRequest.builder()
                .messages(List.of(
                        new LlmMessage("system", buildSystemPrompt()),
                        new LlmMessage("user", context + "\n\n用户问题：" + question)
                ))
                .maxTokens(2048)
                .temperature(0.7)
                .build();

        // 6. 流式调用 LLM
        llmService.chatStream(chatRequest, outputStream);
    }

    /**
     * 非流式查询 — 返回完整回答（用于测试）
     */
    public String query(RagQueryRequest request) {
        String question = request.getQuestion();
        log.info("RAG 查询（非流式）: {}", question);

        float[] queryVector = embeddingService.embed(question);
        List<VectorStore.SearchResult> results = vectorStore.search(queryVector, ragProps.getTopK());

        if (request.getDocType() != null && !request.getDocType().isBlank()) {
            results = results.stream()
                    .filter(r -> request.getDocType().equals(r.metadata().get("type")))
                    .collect(Collectors.toList());
        }

        String context = buildContext(results);
        LlmChatRequest chatRequest = LlmChatRequest.builder()
                .messages(List.of(
                        new LlmMessage("system", buildSystemPrompt()),
                        new LlmMessage("user", context + "\n\n用户问题：" + question)
                ))
                .maxTokens(2048)
                .temperature(0.7)
                .build();

        return llmService.chat(chatRequest);
    }

    /**
     * 检索相关文档（不调用 LLM，仅返回检索结果）
     */
    public List<VectorStore.SearchResult> retrieve(RagQueryRequest request) {
        float[] queryVector = embeddingService.embed(request.getQuestion());
        List<VectorStore.SearchResult> results = vectorStore.search(queryVector, ragProps.getTopK());

        if (request.getDocType() != null && !request.getDocType().isBlank()) {
            results = results.stream()
                    .filter(r -> request.getDocType().equals(r.metadata().get("type")))
                    .collect(Collectors.toList());
        }
        return results;
    }

    private String buildContext(List<VectorStore.SearchResult> results) {
        if (results.isEmpty()) {
            return "（未检索到相关历史资料，请基于通用历史知识回答）";
        }

        StringBuilder sb = new StringBuilder("以下是检索到的相关历史资料：\n\n");
        for (int i = 0; i < results.size(); i++) {
            VectorStore.SearchResult r = results.get(i);
            Map<String, Object> meta = r.metadata();
            sb.append(String.format("[%d] 类型:%s 标题:%s\n", i + 1, meta.get("type"), meta.get("title")));
            String content = (String) meta.getOrDefault("content", "");
            // 截断过长的内容，避免超出 token 限制
            if (content.length() > 800) {
                content = content.substring(0, 800) + "...";
            }
            sb.append("    内容：").append(content).append("\n");
            if (meta.get("source") != null) {
                sb.append("    来源：").append(meta.get("source")).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String buildSystemPrompt() {
        return """
                你是"五千年史馆"的历史问答助手。请基于提供的检索资料回答用户问题。

                要求：
                1. 优先使用检索资料中的信息，引用时标注 [序号]
                2. 如果检索资料不足以回答，可结合通用历史知识，但要说明"以下为补充资料"
                3. 保持客观、准确，避免主观评价
                4. 用中文回答，语言简洁明了
                5. 涉及年份时，标注公元前/公元后
                """;
    }
}
