package com.history.service;

import com.history.entity.UserPersonaEntity;
import com.history.repository.UserPersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户 AI 画像服务
 * <p>
 * - get(): 获取当前用户的 persona JSON
 * - save(): upsert 保存（已存在则更新）
 * - clear(): 清空（删除记录，前端再调用会拿到 null）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPersonaService {

    private final UserPersonaRepository repository;

    @Transactional(readOnly = true)
    public Optional<String> get(String username) {
        return repository.findByUsername(username).map(UserPersonaEntity::getPersonaJson);
    }

    @Transactional
    public void save(String username, String personaJson) {
        UserPersonaEntity entity = repository.findByUsername(username)
            .orElseGet(() -> {
                UserPersonaEntity e = new UserPersonaEntity();
                e.setUsername(username);
                return e;
            });
        entity.setPersonaJson(personaJson);
        repository.save(entity);
        log.debug("用户 {} persona 已保存 ({} chars)", username, personaJson.length());
    }

    @Transactional
    public void clear(String username) {
        repository.deleteByUsername(username);
        log.debug("用户 {} persona 已清空", username);
    }
}
