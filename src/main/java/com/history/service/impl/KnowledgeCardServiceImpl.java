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
}
