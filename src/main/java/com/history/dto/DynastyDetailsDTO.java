package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 朝代详情 — 包含该朝代所有事件、人物、知识卡片
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynastyDetailsDTO {
    private DynastyDTO dynasty;
    private List<EventDTO> events;
    private List<PersonDTO> persons;
    private List<KnowledgeCardDTO> knowledgeCards;
}
