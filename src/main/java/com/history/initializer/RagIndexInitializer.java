package com.history.initializer;

import com.history.entity.*;
import com.history.repository.*;
import com.history.service.EmbeddingService;
import com.history.service.VectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 索引初始化器
 * 在应用启动完成后（ApplicationReadyEvent），从数据库读取所有历史资料，
 * 调用 EmbeddingService 生成向量，存入 VectorStore。
 *
 * 用 ApplicationReadyEvent 而非 CommandLineRunner，确保：
 * 1. 在 DataInitializer 之后执行（数据已灌入 DB）
 * 2. 不阻塞应用启动（灌库失败不影响应用运行）
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "rag.auto-index", havingValue = "true", matchIfMissing = true)
public class RagIndexInitializer {

    private final EventRepository eventRepo;
    private final PersonRepository personRepo;
    private final DynastyRepository dynastyRepo;
    private final KnowledgeCardRepository knowledgeCardRepo;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        index();
    }

    public void index() {
        if (vectorStore.size() > 0) {
            log.info("RAG 索引已存在 {} 条，跳过灌库", vectorStore.size());
            return;
        }
        log.info("开始构建 RAG 索引...");
        int total = 0;
        try {
            total += indexEvents();
            total += indexPersons();
            total += indexDynasties();
            total += indexKnowledgeCards();
            log.info("RAG 索引构建完成，共 {} 条", total);
        } catch (Exception e) {
            log.error("RAG 索引构建中断（已索引 {} 条）: {}", total, e.getMessage(), e);
        }
    }

    private int indexEvents() {
        List<EventEntity> events = eventRepo.findAll();
        if (events.isEmpty()) return 0;
        log.info("索引事件：{} 条", events.size());

        List<String> texts = events.stream().map(this::buildEventText).toList();
        List<float[]> vectors = embeddingService.embedBatch(texts);

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            EventEntity e = events.get(i);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "event");
            metadata.put("title", e.getTitle());
            metadata.put("content", texts.get(i));
            metadata.put("source", e.getUid());
            if (e.getYear() != null) metadata.put("year", e.getYear());
            if (e.getCategory() != null) metadata.put("category", e.getCategory());
            entries.add(new VectorStore.VectorEntry("event-" + e.getId(), vectors.get(i), metadata));
        }
        vectorStore.storeBatch(entries);
        return entries.size();
    }

    private int indexPersons() {
        List<PersonEntity> persons = personRepo.findAll();
        if (persons.isEmpty()) return 0;
        log.info("索引人物：{} 条", persons.size());

        List<String> texts = persons.stream().map(this::buildPersonText).toList();
        List<float[]> vectors = embeddingService.embedBatch(texts);

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (int i = 0; i < persons.size(); i++) {
            PersonEntity p = persons.get(i);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "person");
            metadata.put("title", p.getName());
            metadata.put("content", texts.get(i));
            metadata.put("source", p.getUid());
            if (p.getYearsDisplay() != null) metadata.put("years", p.getYearsDisplay());
            entries.add(new VectorStore.VectorEntry("person-" + p.getId(), vectors.get(i), metadata));
        }
        vectorStore.storeBatch(entries);
        return entries.size();
    }

    private int indexDynasties() {
        List<DynastyEntity> dynasties = dynastyRepo.findAll();
        if (dynasties.isEmpty()) return 0;
        log.info("索引朝代：{} 条", dynasties.size());

        List<String> texts = dynasties.stream().map(this::buildDynastyText).toList();
        List<float[]> vectors = embeddingService.embedBatch(texts);

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (int i = 0; i < dynasties.size(); i++) {
            DynastyEntity d = dynasties.get(i);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "dynasty");
            metadata.put("title", d.getName());
            metadata.put("content", texts.get(i));
            metadata.put("source", d.getUid());
            if (d.getPeriod() != null) metadata.put("period", d.getPeriod());
            entries.add(new VectorStore.VectorEntry("dynasty-" + d.getId(), vectors.get(i), metadata));
        }
        vectorStore.storeBatch(entries);
        return entries.size();
    }

    private int indexKnowledgeCards() {
        List<KnowledgeCardEntity> cards = knowledgeCardRepo.findAll();
        if (cards.isEmpty()) return 0;
        log.info("索引知识卡片：{} 条", cards.size());

        List<String> texts = cards.stream().map(this::buildKnowledgeText).toList();
        List<float[]> vectors = embeddingService.embedBatch(texts);

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            KnowledgeCardEntity c = cards.get(i);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("type", "knowledge");
            metadata.put("title", c.getTitle());
            metadata.put("content", texts.get(i));
            metadata.put("source", c.getUid());
            entries.add(new VectorStore.VectorEntry("knowledge-" + c.getId(), vectors.get(i), metadata));
        }
        vectorStore.storeBatch(entries);
        return entries.size();
    }

    private String buildEventText(EventEntity e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getTitle());
        if (e.getYearDisplay() != null) sb.append("（").append(e.getYearDisplay()).append("）");
        if (e.getCategory() != null) sb.append("，分类：").append(e.getCategory());
        if (e.getDescription() != null) sb.append("。").append(e.getDescription());
        return sb.toString();
    }

    private String buildPersonText(PersonEntity p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getName());
        if (p.getCourtesyName() != null) sb.append("，字").append(p.getCourtesyName());
        if (p.getYearsDisplay() != null) sb.append("（").append(p.getYearsDisplay()).append("）");
        if (p.getBio() != null) sb.append("。").append(p.getBio());
        if (p.getQuote() != null) sb.append(" 名言：").append(p.getQuote());
        return sb.toString();
    }

    private String buildDynastyText(DynastyEntity d) {
        StringBuilder sb = new StringBuilder();
        sb.append(d.getName());
        if (d.getFullName() != null) sb.append("（").append(d.getFullName()).append("）");
        if (d.getPeriod() != null) sb.append("，时期：").append(d.getPeriod());
        if (d.getFounder() != null) sb.append("，建立者：").append(d.getFounder());
        if (d.getCapital() != null) sb.append("，都城：").append(d.getCapital());
        if (d.getHighlights() != null) sb.append("。").append(d.getHighlights());
        if (d.getDescription() != null) sb.append(" ").append(d.getDescription());
        if (d.getLegacy() != null) sb.append(" 遗产：").append(d.getLegacy());
        return sb.toString();
    }

    private String buildKnowledgeText(KnowledgeCardEntity c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getTitle());
        if (c.getStartYearDisplay() != null) sb.append("（").append(c.getStartYearDisplay()).append("）");
        if (c.getDescription() != null) sb.append("。").append(c.getDescription());
        if (c.getFulltext() != null) sb.append(" ").append(c.getFulltext());
        return sb.toString();
    }
}
