package com.history.service.impl;

import com.history.dto.EventDTO;
import com.history.entity.DynastyEntity;
import com.history.entity.EventEntity;
import com.history.repository.EventRepository;
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
                .build();
    }
}
