package com.history.service.impl;

import com.history.dto.*;
import com.history.entity.DynastyEntity;
import com.history.entity.EventEntity;
import com.history.entity.KnowledgeCardEntity;
import com.history.entity.PersonEntity;
import com.history.repository.EventRepository;
import com.history.repository.PersonRepository;
import com.history.repository.KnowledgeCardRepository;
import com.history.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PersonRepository personRepository;
    private final KnowledgeCardRepository knowledgeCardRepository;

    @Override
    public Page<EventDTO> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public List<EventDTO> findAllOrdered() {
        return eventRepository.findAllOrderedByYear().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public EventDTO findById(Long id) {
        return eventRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("事件", id));
    }

    @Override
    public EventDTO findByUid(String uid) {
        return eventRepository.findByUid(uid)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("事件", uid));
    }

    @Override
    public Page<EventDTO> search(String keyword, Pageable pageable) {
        return eventRepository.search(keyword, pageable).map(this::toDTO);
    }

    @Override
    public Page<EventDTO> findByCategory(String category, Pageable pageable) {
        return eventRepository.findByCategory(category, pageable).map(this::toDTO);
    }

    @Override
    public Page<EventDTO> findByDynasty(String dynastyName, Pageable pageable) {
        return eventRepository.findByDynasty_Name(dynastyName, pageable).map(this::toDTO);
    }

    @Override
    public Page<EventDTO> findByYearRange(Integer start, Integer end, Pageable pageable) {
        return eventRepository.findByYearBetween(start, end, pageable).map(this::toDTO);
    }

    @Override
    @Transactional
    public EventDTO createOrUpdate(EventDTO dto) {
        EventEntity entity;
        if (dto.getId() != null) {
            entity = eventRepository.findById(dto.getId())
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("事件", dto.getId()));
        } else {
            entity = new EventEntity();
        }
        entity.setUid(dto.getUid());
        entity.setTitle(dto.getTitle());
        entity.setYear(dto.getYear());
        entity.setYearDisplay(dto.getYearDisplay());
        entity.setYearPrecision(dto.getYearPrecision());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setFulltext(dto.getFulltext());
        entity.setTags(dto.getTags() != null ? dto.getTags() : java.util.List.of());
        entity.setRelatedEvents(dto.getRelatedEvents() != null ? dto.getRelatedEvents() : java.util.List.of());
        entity.setRelatedPersons(dto.getRelatedPersons() != null ? dto.getRelatedPersons() : java.util.List.of());
        entity.setImpact(dto.getImpact());
        entity.setSignificance(dto.getSignificance());
        entity.setRelatedArticles(dto.getRelatedArticles() != null ? dto.getRelatedArticles() : java.util.List.of());
        return toDTO(eventRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    private EventDTO toDTO(EventEntity entity) {
        return EventDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .title(entity.getTitle())
                .year(entity.getYear())
                .yearDisplay(entity.getYearDisplay())
                .yearPrecision(entity.getYearPrecision())
                .category(entity.getCategory())
                .dynastyName(entity.getDynasty() != null ? entity.getDynasty().getName() : null)
                .description(entity.getDescription())
                .fulltext(entity.getFulltext())
                .tags(entity.getTags())
                .relatedEvents(entity.getRelatedEvents())
                .relatedPersons(entity.getRelatedPersons())
                .impact(entity.getImpact())
                .significance(entity.getSignificance())
                .relatedArticles(entity.getRelatedArticles())
                .build();
    }

    @Override
    public EventRelatedDTO getRelatedData(Long eventId) {
        EventEntity entity = eventRepository.findById(eventId)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("事件", eventId));
        EventDTO eventDTO = toDTO(entity);

        // 从 relatedPersons 获取关联人物
        List<PersonDTO> relatedPersons = java.util.Collections.emptyList();
        if (entity.getRelatedPersons() != null && !entity.getRelatedPersons().isEmpty()) {
            java.util.List<PersonDTO> list = new java.util.ArrayList<>();
            for (String uid : entity.getRelatedPersons()) {
                personRepository.findByUid(uid).ifPresent(p -> list.add(toPersonDTO(p)));
            }
            relatedPersons = list;
        }

        // 通过标签匹配相关的知识卡片
        List<KnowledgeCardDTO> relatedKnowledge = java.util.Collections.emptyList();
        if (entity.getTags() != null && !entity.getTags().isEmpty()) {
            java.util.List<KnowledgeCardDTO> list = new java.util.ArrayList<>();
            for (KnowledgeCardEntity k : knowledgeCardRepository.findAll()) {
                if (k.getTags() != null && k.getTags().stream().anyMatch(t -> entity.getTags().contains(t))) {
                    list.add(toKnowledgeDTO(k));
                    if (list.size() >= 5) break;
                }
            }
            relatedKnowledge = list;
        }

        return new EventRelatedDTO(eventDTO, relatedPersons, relatedKnowledge);
    }

    private PersonDTO toPersonDTO(PersonEntity entity) {
        return PersonDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .name(entity.getName())
                .courtesyName(entity.getCourtesyName())
                .dynastyName(entity.getDynasty() != null ? entity.getDynasty().getName() : null)
                .years(entity.getYears())
                .yearsDisplay(entity.getYearsDisplay())
                .gender(entity.getGender())
                .roles(entity.getRoles())
                .quote(entity.getQuote())
                .bio(entity.getBio())
                .tags(entity.getTags())
                .relatedEvents(entity.getRelatedEvents())
                .relatedPersons(entity.getRelatedPersons())
                .build();
    }

    private KnowledgeCardDTO toKnowledgeDTO(KnowledgeCardEntity entity) {
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
