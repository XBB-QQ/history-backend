# MySQL 数据库初始化指引

## 环境信息

- 主机: `localhost`
- 端口: `3306`
- 用户名: `root`
- 密码: `root123`
- 数据库: `history_museum`
- 字符集: `utf8mb4`

## 初始化步骤

### 1. 创建数据库

```bash
mysql -hlocalhost -uroot -proot123 -e "CREATE DATABASE IF NOT EXISTS history_museum CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 2. 执行建表迁移

```bash
mysql -hlocalhost -uroot -proot123 history_museum < src/main/resources/db/migration/V20260621_001__create_base_tables.sql
```

### 3. 导入朝代种子数据

```bash
mysql -hlocalhost -uroot -proot123 history_museum < src/main/resources/db/migration/V20260621_002__insert_dynasties.sql
```

### 4. 导入事件/人物/知识卡片种子数据

（由 JSON 转换脚本生成后执行）

```bash
mysql -hlocalhost -uroot -proot123 history_museum < src/main/resources/db/seed/V20260621_003__seed_events.sql
mysql -hlocalhost -uroot -proot123 history_museum < src/main/resources/db/seed/V20260621_004__seed_persons.sql
mysql -hlocalhost -uroot -proot123 history_museum < src/main/resources/db/seed/V20260621_005__seed_knowledge.sql
```

### 5. 验证数据

```bash
mysql -hlocalhost -uroot -proot123 history_museum -e "
SELECT COUNT(*) AS events FROM events;
SELECT COUNT(*) AS persons FROM persons;
SELECT COUNT(*) AS dynasties FROM dynasties;
SELECT COUNT(*) AS knowledge FROM knowledge_cards;
"
```

预期结果:
- events: 87
- persons: 50
- dynasties: 13
- knowledge: 14

## Spring Boot 切换到 MySQL

编辑 `src/main/resources/application.yml`，取消 MySQL 配置的注释，并将 H2 相关配置注释掉：

```yaml
spring.datasource.url=jdbc:mysql://localhost:3306/history_museum?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root123
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
# spring.h2.console.enabled=false
```

## 连接信息速查

| 项目 | 值 |
|------|-----|
| JDBC URL | `jdbc:mysql://localhost:3306/history_museum?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4` |
| 驱动类 | `com.mysql.cj.jdbc.Driver` |
| 用户名 | `root` |
| 密码 | `root123` |
| Dialect | `org.hibernate.dialect.MySQLDialect` |
