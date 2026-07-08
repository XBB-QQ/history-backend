package com.history.controller;

import com.history.service.RagService;
import com.history.service.VectorStore;
import com.history.dto.RagQueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RAG Controller 切片测试 — 对应 Iteration #94 验收用例 T94-01/02/03/04
 *
 * 策略：@WebMvcTest 只加载 Web 层，@MockBean 替换 Service/VectorStore，不连数据库
 */
@WebMvcTest(controllers = RagController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RagService ragService;

    @MockBean
    private VectorStore vectorStore;

    /**
     * T94-04：未登录访问 GET /api/v1/rag/status → 200（公开）
     */
    @Test
    void testRagStatusPublicAccess_T94_04() throws Exception {
        when(vectorStore.size()).thenReturn(42);

        mockMvc.perform(get("/api/v1/rag/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vectorCount").value(42))
                .andExpect(jsonPath("$.storeType").exists());
    }

    /**
     * T94-01：POST /api/v1/rag/chat 有结果 → 200 + answer 非空
     */
    @Test
    void testRagChatWithResults_T94_01() throws Exception {
        when(ragService.query(any(RagQueryRequest.class)))
                .thenReturn("安史之乱的原因包括藩镇割据、朝廷腐败等。");

        String body = "{\"question\":\"安史之乱原因\"}";

        mockMvc.perform(post("/api/v1/rag/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists())
                .andExpect(jsonPath("$.answer").isNotEmpty());
    }

    /**
     * T94-02：POST /api/v1/rag/chat 无结果 → 200 + answer 包含"未找到"
     */
    @Test
    void testRagChatNoResults_T94_02() throws Exception {
        when(ragService.query(any(RagQueryRequest.class)))
                .thenReturn("未找到相关资料，请基于通用历史知识补充。");

        String body = "{\"question\":\"xyz不存在\"}";

        mockMvc.perform(post("/api/v1/rag/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.containsString("未找到")));
    }

    /**
     * T94-03（后端部分）：POST /api/v1/rag/retrieve → 200 + 返回文档列表
     * 前端部分（RagQaPage 改用后端 API）已在 history-frontend 完成
     */
    @Test
    void testRagRetrieve_T94_03() throws Exception {
        VectorStore.SearchResult result = new VectorStore.SearchResult(
                "event:1", 0.85f,
                Map.of("type", "event", "title", "安史之乱", "content", "唐代安史之乱", "source", "an-lushan")
        );
        when(ragService.retrieve(any(RagQueryRequest.class)))
                .thenReturn(List.of(result));

        String body = "{\"question\":\"唐朝\"}";

        mockMvc.perform(post("/api/v1/rag/retrieve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("event:1"))
                .andExpect(jsonPath("$[0].score").value(0.85))
                .andExpect(jsonPath("$[0].type").value("event"))
                .andExpect(jsonPath("$[0].title").value("安史之乱"));
    }

    /**
     * T94-01（流式变体）：POST /api/v1/rag/query SSE → 200 + text/event-stream
     */
    @Test
    void testRagQueryStream_T94_01_stream() throws Exception {
        // 模拟 RagService.queryStream：向 OutputStream 写入一段 SSE 数据
        doAnswer(inv -> {
            OutputStream out = inv.getArgument(1);
            out.write("data: {\"choices\":[{\"delta\":{\"content\":\"安史之乱\"}}]}\n\n".getBytes());
            out.write("data: [DONE]\n\n".getBytes());
            out.flush();
            return null;
        }).when(ragService).queryStream(any(RagQueryRequest.class), any(OutputStream.class));

        String body = "{\"question\":\"安史之乱原因\"}";

        mockMvc.perform(post("/api/v1/rag/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/event-stream"));
    }
}
