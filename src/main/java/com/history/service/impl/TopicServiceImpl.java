package com.history.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.TopicDTO;
import com.history.entity.TopicEntity;
import com.history.repository.TopicRepository;
import com.history.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<TopicDTO> findAll(Pageable pageable) {
        return topicRepository.findByPublishedTrue(pageable).map(this::toDTO);
    }

    @Override
    public TopicDTO findById(Long id) {
        return topicRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("专题", id));
    }

    @Override
    public TopicDTO findByUid(String uid) {
        return topicRepository.findByUid(uid)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("专题", uid));
    }

    @Override
    public Page<TopicDTO> search(String keyword, Pageable pageable) {
        return topicRepository.search(keyword, pageable).map(this::toDTO);
    }

    @Override
    public List<String> getCategories() {
        return topicRepository.findDistinctCategories();
    }

    @Override
    public List<TopicDTO> findByCategory(String category) {
        return topicRepository.findByCategoryAndPublishedTrueOrderBySortOrderAsc(category)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<TopicDTO> findAllOrdered() {
        return topicRepository.findByPublishedTrue(Pageable.unpaged()).getContent().stream()
                .map(this::toDTO).toList();
    }

    @Override
    public List<TopicDTO> findByTag(String tag) {
        return topicRepository.findByTag(tag).stream().map(this::toDTO).toList();
    }

    private List<String> parseJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String toJson(List<String> list) {
        if (list == null) return "[]";
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private TopicDTO toDTO(TopicEntity entity) {
        return TopicDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .coverImage(entity.getCoverImage())
                .summary(entity.getSummary())
                .description(entity.getDescription())
                .chapterCount(entity.getChapterCount())
                .estimatedMinutes(entity.getEstimatedMinutes())
                .tags(parseJson(entity.getTagsJson()))
                .relatedEvents(parseJson(entity.getRelatedEventsJson()))
                .relatedPersons(parseJson(entity.getRelatedPersonsJson()))
                .chapters(entity.getChapters())
                .references(parseJson(entity.getReferencesJson()))
                .sortOrder(entity.getSortOrder())
                .published(entity.getPublished())
                .build();
    }

    @Override
    @Transactional
    public TopicDTO createOrUpdate(TopicDTO dto) {
        TopicEntity entity;
        if (dto.getId() != null) {
            entity = topicRepository.findById(dto.getId())
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("专题", dto.getId()));
        } else {
            entity = new TopicEntity();
        }
        entity.setUid(dto.getUid());
        entity.setTitle(dto.getTitle());
        entity.setCategory(dto.getCategory());
        entity.setCoverImage(dto.getCoverImage());
        entity.setSummary(dto.getSummary());
        entity.setDescription(dto.getDescription());
        entity.setChapterCount(dto.getChapterCount());
        entity.setEstimatedMinutes(dto.getEstimatedMinutes());
        entity.setTagsJson(toJson(dto.getTags()));
        entity.setRelatedEventsJson(toJson(dto.getRelatedEvents()));
        entity.setRelatedPersonsJson(toJson(dto.getRelatedPersons()));
        entity.setChapters(dto.getChapters());
        entity.setReferencesJson(toJson(dto.getReferences()));
        entity.setSortOrder(dto.getSortOrder());
        entity.setPublished(dto.getPublished());
        return toDTO(topicRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }
}
