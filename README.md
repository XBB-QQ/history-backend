# 五千年史馆 — 后端服务

Java 17 + Spring Boot 3.2 + JPA + MySQL + Flyway + JWT + Spring AI（RAG）+ WebSocket

## 快速开始

### 1. 启动 MySQL（Docker）

```bash
docker compose up -d
```

容器密码通过环境变量注入，默认值仅用于本地开发，见 [docker-compose.yml](docker-compose.yml)。

### 2. 配置环境变量

复制 [.env.example](.env.example) 为 `.env`，填入真实值（至少需要 LLM_API_KEY 和 EMBEDDING_API_KEY，否则启动会因 `@NotBlank` 校验失败）：

```bash
copy .env.example .env
# 编辑 .env 填入智谱 API Key
```

> **安全提示**：切勿提交 `.env` 到 git，已在 `.gitignore` 中忽略。

### 3. 运行

```bash
mvn spring-boot:run
```

启动后访问：
- 应用：http://localhost:8080
- Swagger UI：http://localhost:8080/swagger-ui.html

数据库 schema 由 Flyway 自动迁移（`db/migration/` 目录），无需手动执行 SQL。种子数据由 `DataInitializer` 和 `QuizDataSeeder` 在启动时注入。

## API 文档

详细接口说明见 [docs/API.md](docs/API.md)。以下为模块速查表（路径以代码中 `@RequestMapping` 为准）：

### 公共接口（无需认证）

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 事件 | `/api/events` | 历史事件 CRUD、搜索、时间轴 |
| 人物 | `/api/persons` | 历史人物 CRUD、搜索、筛选 |
| 朝代 | `/api/dynasties` | 朝代 CRUD |
| 知识卡片 | `/api/knowledge` | 知识卡片 CRUD、搜索 |
| 专题 | `/api/topics` | 历史专题 |
| 地图 | `/api/map` | 地图区域数据 |
| 历史上的今天 | `/api/public` | 今日历史事件 |
| RAG 问答 | `/api/v1/rag` | 检索增强生成（SSE 流式） |
| LLM 对话 | `/api/llm` | LLM 直接对话 |

### 用户接口（JWT 认证）

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 用户认证 | `/api/auth` | 登录、注册 |
| 收藏 | `/api/favorites` | 用户收藏 |
| 学习进度 | `/api/user/learning` | 研学线进度 |
| 答题 | `/api/user/quiz` | 答题记录 |
| 用户投稿 | `/api/user/contributions` | 用户提交内容 |

### 管理后台（API Key 认证）

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 管理员认证 | `/api/admin/auth` | 管理员登录 |
| 事件管理 | `/api/admin/events` | 事件 CRUD |
| 人物管理 | `/api/admin/persons` | 人物 CRUD |
| 朝代管理 | `/api/admin/dynasties` | 朝代 CRUD |
| 知识管理 | `/api/admin/knowledge` | 知识卡片 CRUD |
| 专题管理 | `/api/admin/topics` | 专题 CRUD |
| 投稿审核 | `/api/admin/contributions` | 用户投稿审核 |

### 课堂

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 课堂 | `/api/classroom` | 作业、学生进度 |

### 联机剧本杀（WebSocket）

通过 STOMP over WebSocket 通信，端点见 [WebSocketConfig.java](src/main/java/com/history/config/WebSocketConfig.java)，业务逻辑在 [game/](src/main/java/com/history/game/) 目录。

## 项目结构

```
src/main/java/com/history/
├── HistoryApplication.java          # 启动类
├── config/                          # 配置类（11 个）
│   ├── SecurityConfig.java         # Spring Security + JWT
│   ├── JwtAuthenticationFilter.java # JWT 过滤器
│   ├── ApiKeyAuthenticationFilter.java # API Key 过滤器（管理后台）
│   ├── WebSocketConfig.java        # STOMP 配置
│   ├── WebConfig.java              # CORS
│   ├── I18nConfig.java             # 国际化
│   ├── RateLimitConfig.java        # Guava 限流
│   ├── LlmProperties.java          # LLM 配置（@NotBlank 校验）
│   ├── EmbeddingProperties.java    # Embedding 配置
│   ├── RagProperties.java          # RAG 配置
│   └── ...
├── controller/                      # REST 控制器
│   ├── EventController.java
│   ├── PersonController.java
│   ├── DynastyController.java
│   ├── KnowledgeCardController.java
│   ├── RagController.java
│   ├── LlmController.java
│   ├── QuizController.java
│   ├── TopicController.java
│   ├── MapController.java
│   ├── FavoriteController.java
│   ├── LearningController.java
│   ├── ClassroomController.java
│   ├── TodayInHistoryController.java
│   ├── admin/                      # 管理后台（7 个）
│   └── user/                       # 用户接口（2 个）
├── dto/                             # 数据传输对象（30+ 个）
├── entity/                          # JPA 实体（12 个）
├── repository/                      # 数据访问层（12 个）
├── service/                         # 业务逻辑层
│   ├── *.java                      # 接口
│   └── impl/                       # 实现
├── game/                            # 联机剧本杀
│   ├── GameRoomService.java
│   ├── GameStompController.java    # STOMP 控制器
│   ├── dto/
│   └── model/
├── initializer/                     # 启动时数据初始化
│   ├── DataInitializer.java
│   ├── QuizDataSeeder.java
│   └── RagIndexInitializer.java    # RAG 向量索引灌入
├── util/                            # 工具类（JWT、Security）
└── exception/                       # 异常处理
```

## 配置

| 配置项 | 环境变量 | 默认值 | 说明 |
|--------|----------|--------|------|
| 数据库主机 | `DB_HOST` | localhost | MySQL 主机 |
| 数据库端口 | `DB_PORT` | 3306 | MySQL 端口 |
| 数据库用户 | `DB_USER` | root | MySQL 用户 |
| 数据库密码 | `DB_PASSWORD` | root123 | MySQL 密码（生产必须修改） |
| LLM API Key | `LLM_API_KEY` | （无，必填） | 智谱 API Key |
| Embedding API Key | `EMBEDDING_API_KEY` | （无，必填） | 智谱 API Key |
| RAG 向量库 | `RAG_VECTOR_STORE` | memory | memory / redis |
| Redis 主机 | `REDIS_HOST` | localhost | 生产用 Redis 向量库 |

完整配置项见 [.env.example](.env.example) 和 [application.yml](src/main/resources/application.yml)。

## 默认账号

- 管理员：`admin` / `admin123`（生产环境务必修改，见 [NOTES.md](NOTES.md)）

## 部署

```bash
# 后端构建
mvn clean package -DskipTests

# Docker 镜像
docker build -t history-backend .

# 前端构建产物会被后端作为静态资源服务（可选）
# 详见 docs/DEPLOY.md
```
