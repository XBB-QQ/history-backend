package com.history.service.impl;

import com.history.dto.KnowledgeCardDTO;
import com.history.entity.KnowledgeCardEntity;
import com.history.repository.KnowledgeCardRepository;
import com.history.service.KnowledgeCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KnowledgeCardServiceImpl implements KnowledgeCardService {

    private final KnowledgeCardRepository knowledgeCardRepository;

    @Override
    public Page<KnowledgeCardDTO> findAll(Pageable pageable) {
        return knowledgeCardRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public KnowledgeCardDTO findById(Long id) {
        return knowledgeCardRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("知识卡片", id));
    }

    @Override
    public KnowledgeCardDTO findByUid(String uid) {
        return knowledgeCardRepository.findByUid(uid)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("知识卡片", uid));
    }

    @Override
    public Page<KnowledgeCardDTO> search(String keyword, Pageable pageable) {
        return knowledgeCardRepository.search(keyword, pageable).map(this::toDTO);
    }

    private KnowledgeCardDTO toDTO(KnowledgeCardEntity entity) {
        return KnowledgeCardDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .title(entity.getTitle())
                .startYear(entity.getStartYear())
                .startYearDisplay(entity.getStartYearDisplay())
                .dynastyName(entity.getDynasty() != null ? entity.getDynasty().getName() : null)
                .description(entity.getDescription())
                .fulltext(entity.getFulltext())
                .tags(entity.getTags())
                .relevantEvents(entity.getRelevantEvents())
                .relevantPersons(entity.getRelevantPersons())
                .meta(entity.getMeta())
                .build();
    }

    @Override
    @Transactional
    public KnowledgeCardDTO createOrUpdate(KnowledgeCardDTO dto) {
        KnowledgeCardEntity entity;
        if (dto.getId() != null) {
            entity = knowledgeCardRepository.findById(dto.getId())
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("知识卡片", dto.getId()));
        } else {
            entity = new KnowledgeCardEntity();
        }
        entity.setUid(dto.getUid());
        entity.setTitle(dto.getTitle());
        entity.setStartYear(dto.getStartYear());
        entity.setStartYearDisplay(dto.getStartYearDisplay());
        entity.setDescription(dto.getDescription());
        entity.setFulltext(dto.getFulltext());
        entity.setTags(dto.getTags() != null ? dto.getTags() : java.util.List.of());
        entity.setRelevantEvents(dto.getRelevantEvents() != null ? dto.getRelevantEvents() : java.util.List.of());
        entity.setRelevantPersons(dto.getRelevantPersons() != null ? dto.getRelevantPersons() : java.util.List.of());
        entity.setMeta(dto.getMeta());
        return toDTO(knowledgeCardRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        knowledgeCardRepository.deleteById(id);
    }

    @Override
    public java.util.List<KnowledgeCardDTO> findAllOrdered() {
        return knowledgeCardRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
}
