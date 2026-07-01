package com.history.service.impl;

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
                .tags(entity.getTags())
                .relatedEvents(entity.getRelatedEvents())
                .relatedPersons(entity.getRelatedPersons())
                .chapters(entity.getChapters())
                .references(entity.getReferences())
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
        entity.setTags(dto.getTags() != null ? dto.getTags() : java.util.List.of());
        entity.setRelatedEvents(dto.getRelatedEvents() != null ? dto.getRelatedEvents() : java.util.List.of());
        entity.setRelatedPersons(dto.getRelatedPersons() != null ? dto.getRelatedPersons() : java.util.List.of());
        entity.setChapters(dto.getChapters());
        entity.setReferences(dto.getReferences() != null ? dto.getReferences() : java.util.List.of());
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
