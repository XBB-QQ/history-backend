package com.history.service.impl;

import com.history.dto.DynastyDTO;
import com.history.entity.DynastyEntity;
import com.history.repository.DynastyRepository;
import com.history.service.DynastyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DynastyServiceImpl implements DynastyService {

    private final DynastyRepository dynastyRepository;

    @Override
    public Page<DynastyDTO> findAll(Pageable pageable) {
        return dynastyRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public DynastyDTO findById(Long id) {
        return dynastyRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("朝代", id));
    }

    @Override
    public DynastyDTO findByUid(String uid) {
        return dynastyRepository.findByUid(uid)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("朝代", uid));
    }

    @Override
    public DynastyDTO findByName(String name) {
        return dynastyRepository.findByName(name)
                .map(this::toDTO)
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("朝代", name));
    }

    private DynastyDTO toDTO(DynastyEntity entity) {
        return DynastyDTO.builder()
                .id(entity.getId())
                .uid(entity.getUid())
                .name(entity.getName())
                .fullName(entity.getFullName())
                .period(entity.getPeriod())
                .periodStart(entity.getPeriodStart())
                .periodEnd(entity.getPeriodEnd())
                .founder(entity.getFounder())
                .lastRuler(entity.getLastRuler())
                .capital(entity.getCapital())
                .duration(entity.getDuration())
                .highlights(entity.getHighlights())
                .description(entity.getDescription())
                .fallReason(entity.getFallReason())
                .legacy(entity.getLegacy())
                .populationPeak(entity.getPopulationPeak())
                .gdpEstimate(entity.getGdpEstimate())
                .majorTradeRoutes(entity.getMajorTradeRoutes())
                .culturalHighlights(entity.getCulturalHighlights())
                .build();
    }

    @Override
    @Transactional
    public DynastyDTO createOrUpdate(DynastyDTO dto) {
        DynastyEntity entity;
        if (dto.getId() != null) {
            entity = dynastyRepository.findById(dto.getId())
                .orElseThrow(() -> new com.history.exception.ResourceNotFoundException("朝代", dto.getId()));
        } else {
            entity = new DynastyEntity();
        }
        entity.setUid(dto.getUid());
        entity.setName(dto.getName());
        entity.setFullName(dto.getFullName());
        entity.setPeriod(dto.getPeriod());
        entity.setPeriodStart(dto.getPeriodStart());
        entity.setPeriodEnd(dto.getPeriodEnd());
        entity.setFounder(dto.getFounder());
        entity.setLastRuler(dto.getLastRuler());
        entity.setCapital(dto.getCapital());
        entity.setDuration(dto.getDuration());
        entity.setHighlights(dto.getHighlights());
        entity.setDescription(dto.getDescription());
        entity.setFallReason(dto.getFallReason());
        entity.setLegacy(dto.getLegacy());
        entity.setPopulationPeak(dto.getPopulationPeak());
        entity.setGdpEstimate(dto.getGdpEstimate());
        entity.setMajorTradeRoutes(dto.getMajorTradeRoutes());
        entity.setCulturalHighlights(dto.getCulturalHighlights());
        return toDTO(dynastyRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        dynastyRepository.deleteById(id);
    }

    @Override
    public java.util.List<DynastyDTO> findAllOrdered() {
        return dynastyRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
}
