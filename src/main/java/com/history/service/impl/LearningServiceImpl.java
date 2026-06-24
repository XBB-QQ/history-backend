package com.history.service.impl;

import com.history.dto.*;
import com.history.entity.LearningProgressEntity;
import com.history.entity.ReadingListEntity;
import com.history.repository.LearningProgressRepository;
import com.history.repository.ReadingListRepository;
import com.history.service.LearningService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final LearningProgressRepository progressRepo;
    private final ReadingListRepository listRepo;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void recordView(String userId, String resourceType, Long resourceId) {
        progressRepo.incrementViewCount(userId, resourceType, resourceId);
    }

    @Override
    public List<LearningProgressDTO> getProgress(String userId) {
        return progressRepo.findByUserId(userId).stream()
            .sorted(Comparator.comparing(LearningProgressEntity::getLastViewed).reversed())
            .map(this::toDTO)
            .toList();
    }

    @Override
    public List<ReadingListDTO> getLists(String userId) {
        return listRepo.findByUserId(userId).stream()
            .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
            .map(this::toDTO)
            .toList();
    }

    @Override
    @Transactional
    public ReadingListDTO createList(String userId, String name, String description) {
        ReadingListEntity entity = new ReadingListEntity();
        entity.setUserId(userId);
        entity.setName(name);
        entity.setDescription(description);
        entity.setResources("[]");
        return toDTO(listRepo.save(entity));
    }

    @Override
    @Transactional
    public ReadingListDTO addResource(String userId, Long listId, String resourceType, Long resourceId, String title) {
        ReadingListEntity entity = listRepo.findById(listId)
            .orElseThrow(() -> new RuntimeException("清单不存在"));
        if (!entity.getUserId().equals(userId)) throw new RuntimeException("无权操作");

        List<ReadingListDTO.ResourceItem> resources = parseResources(entity.getResources());
        resources.add(new ReadingListDTO.ResourceItem(resourceType, resourceId, title));
        entity.setResources(toJson(resources));
        return toDTO(listRepo.save(entity));
    }

    @Override
    @Transactional
    public void removeResource(String userId, Long listId, Long resourceId) {
        ReadingListEntity entity = listRepo.findById(listId)
            .orElseThrow(() -> new RuntimeException("清单不存在"));
        if (!entity.getUserId().equals(userId)) throw new RuntimeException("无权操作");

        List<ReadingListDTO.ResourceItem> resources = parseResources(entity.getResources());
        resources.removeIf(r -> r.getId().equals(resourceId));
        entity.setResources(toJson(resources));
        listRepo.save(entity);
    }

    private LearningProgressDTO toDTO(LearningProgressEntity entity) {
        return new LearningProgressDTO(
            entity.getId(),
            entity.getResourceType(),
            entity.getResourceId(),
            entity.getViewCount(),
            "", // 资源名称需要从其他服务获取，这里留空
            entity.getLastViewed()
        );
    }

    private ReadingListDTO toDTO(ReadingListEntity entity) {
        ReadingListDTO dto = new ReadingListDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setResources(parseResources(entity.getResources()));
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private List<ReadingListDTO.ResourceItem> parseResources(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<ReadingListDTO.ResourceItem>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String toJson(List<ReadingListDTO.ResourceItem> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception e) {
            return "[]";
        }
    }
}
