package com.history.service;

import com.history.entity.*;
import com.history.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 索引灌入器 — 启动时从数据库读取数据，生成向量，存入向量库
 * 异步执行，不阻塞应用启动
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Order(100)
public class RagIndexer implements ApplicationRunner {

    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final EventRepository eventRepository;
    private final PersonRepository personRepository;
    private final DynastyRepository dynastyRepository;
    private final KnowledgeCardRepository knowledgeCardRepository;
    private final TopicRepository topicRepository;

    /** 单批嵌入的最大文本数，避免 API 限制 */
    private static final int BATCH_SIZE = 16;

    @Override
    public void run(ApplicationArguments args) {
        try {
            indexAll();
        } catch (Exception e) {
            log.warn("RAG 索引灌入失败（不影响应用启动）: {}", e.getMessage());
        }
    }

    /**
     * 全量索引灌入
     */
    public void indexAll() {
        log.info("开始 RAG 索引灌入...");
        long start = System.currentTimeMillis();

        vectorStore.clear();
        int total = 0;
        total += indexEvents();
        total += indexPersons();
        total += indexDynasties();
        total += indexKnowledgeCards();
        total += indexTopics();

        long cost = System.currentTimeMillis() - start;
        log.info("RAG 索引灌入完成：共 {} 条，耗时 {}ms", total, cost);
    }

    private int indexEvents() {
        List<EventEntity> events = eventRepository.findAll();
        if (events.isEmpty()) return 0;

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (EventEntity e : events) {
            String text = buildEventText(e);
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "event");
            meta.put("title", e.getTitle());
            meta.put("content", text);
            meta.put("source", e.getUid());
            meta.put("year", e.getYear());
            meta.put("category", e.getCategory());
            entries.add(new VectorStore.VectorEntry("event:" + e.getId(), new float[0], meta));
        }
        return embedAndStore(entries);
    }

    private int indexPersons() {
        List<PersonEntity> persons = personRepository.findAll();
        if (persons.isEmpty()) return 0;

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (PersonEntity p : persons) {
            String text = buildPersonText(p);
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "person");
            meta.put("title", p.getName());
            meta.put("content", text);
            meta.put("source", p.getUid());
            meta.put("dynasty", p.getDynasty() != null ? p.getDynasty().getName() : "");
            entries.add(new VectorStore.VectorEntry("person:" + p.getId(), new float[0], meta));
        }
        return embedAndStore(entries);
    }

    private int indexDynasties() {
        List<DynastyEntity> dynasties = dynastyRepository.findAll();
        if (dynasties.isEmpty()) return 0;

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (DynastyEntity d : dynasties) {
            String text = buildDynastyText(d);
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "dynasty");
            meta.put("title", d.getName());
            meta.put("content", text);
            meta.put("source", d.getUid());
            entries.add(new VectorStore.VectorEntry("dynasty:" + d.getId(), new float[0], meta));
        }
        return embedAndStore(entries);
    }

    private int indexKnowledgeCards() {
        List<KnowledgeCardEntity> cards = knowledgeCardRepository.findAll();
        if (cards.isEmpty()) return 0;

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (KnowledgeCardEntity k : cards) {
            String text = buildKnowledgeText(k);
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "knowledge");
            meta.put("title", k.getTitle());
            meta.put("content", text);
            meta.put("source", k.getUid());
            entries.add(new VectorStore.VectorEntry("knowledge:" + k.getId(), new float[0], meta));
        }
        return embedAndStore(entries);
    }

    private int indexTopics() {
        List<TopicEntity> topics = topicRepository.findAll();
        if (topics.isEmpty()) return 0;

        List<VectorStore.VectorEntry> entries = new ArrayList<>();
        for (TopicEntity t : topics) {
            String text = buildTopicText(t);
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "topic");
            meta.put("title", t.getTitle());
            meta.put("content", text);
            meta.put("source", t.getUid());
            entries.add(new VectorStore.VectorEntry("topic:" + t.getId(), new float[0], meta));
        }
        return embedAndStore(entries);
    }

    /**
     * 分批嵌入并存储
     */
    private int embedAndStore(List<VectorStore.VectorEntry> entries) {
        if (entries.isEmpty()) return 0;

        for (int i = 0; i < entries.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, entries.size());
            List<VectorStore.VectorEntry> batch = entries.subList(i, end);

            List<String> texts = batch.stream()
                    .map(e -> (String) e.metadata().get("content"))
                    .toList();

            try {
                List<float[]> vectors = embeddingService.embedBatch(texts);
                List<VectorStore.VectorEntry> withVectors = new ArrayList<>(batch.size());
                for (int j = 0; j < batch.size(); j++) {
                    VectorStore.VectorEntry e = batch.get(j);
                    withVectors.add(new VectorStore.VectorEntry(e.id(), vectors.get(j), e.metadata()));
                }
                vectorStore.storeBatch(withVectors);
                log.debug("嵌入进度: {}/{}", end, entries.size());
            } catch (Exception e) {
                log.warn("批量嵌入失败（跳过本批）: {}", e.getMessage());
            }
        }
        return entries.size();
    }

    private String buildEventText(EventEntity e) {
        StringBuilder sb = new StringBuilder();
        sb.append("事件：").append(e.getTitle());
        if (e.getYearDisplay() != null) sb.append("（").append(e.getYearDisplay()).append("）");
        sb.append("\n分类：").append(e.getCategory());
        if (e.getDynasty() != null) sb.append("\n朝代：").append(e.getDynasty().getName());
        if (e.getDescription() != null) sb.append("\n描述：").append(e.getDescription());
        if (e.getFulltext() != null) sb.append("\n详情：").append(e.getFulltext());
        if (e.getImpact() != null) sb.append("\n影响：").append(e.getImpact());
        if (e.getClassicalText() != null) sb.append("\n史书原文：").append(e.getClassicalText());
        return sb.toString();
    }

    private String buildPersonText(PersonEntity p) {
        StringBuilder sb = new StringBuilder();
        sb.append("人物：").append(p.getName());
        if (p.getCourtesyName() != null) sb.append("（字：").append(p.getCourtesyName()).append("）");
        if (p.getYearsDisplay() != null) sb.append("\n生卒：").append(p.getYearsDisplay());
        if (p.getDynasty() != null) sb.append("\n朝代：").append(p.getDynasty().getName());
        if (p.getQuote() != null) sb.append("\n名言：").append(p.getQuote());
        if (p.getBio() != null) sb.append("\n生平：").append(p.getBio());
        if (p.getAchievements() != null) sb.append("\n成就：").append(p.getAchievements());
        return sb.toString();
    }

    private String buildDynastyText(DynastyEntity d) {
        StringBuilder sb = new StringBuilder();
        sb.append("朝代：").append(d.getName());
        if (d.getPeriod() != null) sb.append("\n时期：").append(d.getPeriod());
        if (d.getFounder() != null) sb.append("\n开国君主：").append(d.getFounder());
        if (d.getCapital() != null) sb.append("\n都城：").append(d.getCapital());
        if (d.getDescription() != null) sb.append("\n描述：").append(d.getDescription());
        return sb.toString();
    }

    private String buildKnowledgeText(KnowledgeCardEntity k) {
        StringBuilder sb = new StringBuilder();
        sb.append("知识卡片：").append(k.getTitle());
        if (k.getDynasty() != null) sb.append("\n朝代：").append(k.getDynasty().getName());
        if (k.getStartYearDisplay() != null) sb.append("\n年份：").append(k.getStartYearDisplay());
        if (k.getDescription() != null) sb.append("\n描述：").append(k.getDescription());
        if (k.getFulltext() != null) sb.append("\n详情：").append(k.getFulltext());
        return sb.toString();
    }

    private String buildTopicText(TopicEntity t) {
        StringBuilder sb = new StringBuilder();
        sb.append("专题：").append(t.getTitle());
        if (t.getCategory() != null) sb.append("\n分类：").append(t.getCategory());
        if (t.getSummary() != null) sb.append("\n摘要：").append(t.getSummary());
        if (t.getDescription() != null) sb.append("\n内容：").append(t.getDescription());
        return sb.toString();
    }
}
