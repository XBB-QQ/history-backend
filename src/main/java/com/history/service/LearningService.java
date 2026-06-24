package com.history.service;

import com.history.dto.LearningProgressDTO;
import com.history.dto.ReadingListDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LearningService {

    /** 记录浏览 */
    void recordView(String userId, String resourceType, Long resourceId);

    /** 获取学习进度 */
    List<LearningProgressDTO> getProgress(String userId);

    /** 获取阅读清单列表 */
    List<ReadingListDTO> getLists(String userId);

    /** 创建阅读清单 */
    ReadingListDTO createList(String userId, String name, String description);

    /** 向清单添加资源 */
    ReadingListDTO addResource(String userId, Long listId, String resourceType, Long resourceId, String title);

    /** 删除资源 */
    void removeResource(String userId, Long listId, Long resourceId);
}
