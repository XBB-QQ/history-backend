package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LlmChatRequest {
    private List<LlmMessage> messages;
    private String model;
    private Integer maxTokens;
    private Double temperature;
}
