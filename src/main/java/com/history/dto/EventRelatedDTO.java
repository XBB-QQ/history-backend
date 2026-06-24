package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 事件关联数据 — 关联人物和知识卡片
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRelatedDTO {
    private EventDTO event;
    private List<PersonDTO> relatedPersons;
    private List<KnowledgeCardDTO> relatedKnowledge;
}
