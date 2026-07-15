-- 清理测试代码污染的数据
-- EventControllerTest 和 PersonControllerTest 在 @BeforeEach 里 deleteAll() 清空了
-- seed 数据并插入 test-event/test-person，且没有 @Transactional 回滚。
-- 此迁移删除残留的 test 数据及其关联表记录，让 DataInitializer 能检测到空表并自动重新 seed。
-- （已给两个测试类加 @Transactional，防止再次污染）

-- 先删除关联表里引用 test 数据的记录（避免外键约束阻止主表删除）
DELETE FROM event_entity_tags WHERE event_entity_id IN (SELECT id FROM events WHERE uid LIKE 'test-event-%');
DELETE FROM event_entity_related_persons WHERE event_entity_id IN (SELECT id FROM events WHERE uid LIKE 'test-event-%');
DELETE FROM event_entity_related_events WHERE event_entity_id IN (SELECT id FROM events WHERE uid LIKE 'test-event-%');
DELETE FROM event_entity_related_articles WHERE event_entity_id IN (SELECT id FROM events WHERE uid LIKE 'test-event-%');

DELETE FROM person_entity_tags WHERE person_entity_id IN (SELECT id FROM persons WHERE uid LIKE 'test-person-%');
DELETE FROM person_entity_roles WHERE person_entity_id IN (SELECT id FROM persons WHERE uid LIKE 'test-person-%');
DELETE FROM person_entity_related_persons WHERE person_entity_id IN (SELECT id FROM persons WHERE uid LIKE 'test-person-%');
DELETE FROM person_entity_related_events WHERE person_entity_id IN (SELECT id FROM persons WHERE uid LIKE 'test-person-%');
DELETE FROM person_years WHERE person_id IN (SELECT id FROM persons WHERE uid LIKE 'test-person-%');

-- 再删除主表 test 数据
DELETE FROM events WHERE uid LIKE 'test-event-%';
DELETE FROM persons WHERE uid LIKE 'test-person-%';
