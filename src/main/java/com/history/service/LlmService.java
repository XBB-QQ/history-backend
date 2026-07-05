package com.history.service;

import com.history.dto.LlmChatRequest;

import java.io.OutputStream;

/**
 * LLM 代理服务接口
 */
public interface LlmService {

    /**
     * 非流式对话，返回完整文本
     */
    String chat(LlmChatRequest request);

    /**
     * 流式对话，将智谱 SSE 字节流原样写入 OutputStream
     */
    void chatStream(LlmChatRequest request, OutputStream outputStream);
}
