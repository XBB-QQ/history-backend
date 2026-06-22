package com.history.service;

import com.history.dto.FavoriteDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 收藏服务（内存实现，后续可替换为数据库）
 */
@Service
public class FavoriteService {

    private final java.util.concurrent.ConcurrentHashMap<String, List<FavoriteDTO>> cache = new java.util.concurrent.ConcurrentHashMap<>();

    public List<FavoriteDTO> getFavorites(String userId) {
        return cache.getOrDefault(userId, List.of());
    }

    public FavoriteDTO addFavorite(String userId, String resourceType, Long resourceId, String title) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(System.currentTimeMillis());
        dto.setUserId(userId);
        dto.setResourceType(resourceType);
        dto.setResourceId(resourceId);
        dto.setTitle(title);
        dto.setPinned(false);

        List<FavoriteDTO> list = cache.getOrDefault(userId, List.of());
        list.add(dto);
        cache.put(userId, list);
        return dto;
    }

    public void removeFavorite(String userId, Long resourceId) {
        List<FavoriteDTO> list = cache.get(userId);
        if (list != null) {
            list.removeIf(f -> f.getResourceId().equals(resourceId));
        }
    }

    public void togglePin(String userId, Long resourceId) {
        List<FavoriteDTO> list = cache.get(userId);
        if (list != null) {
            list.stream().filter(f -> f.getResourceId().equals(resourceId)).findFirst()
                .ifPresent(f -> f.setPinned(!f.getPinned()));
        }
    }
}
