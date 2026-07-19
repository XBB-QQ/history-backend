# 部署文档

> **状态**: ✅ 已完成
> **最后更新**: 2026-07-18

---

## 1. 部署架构

```
┌─────────────────┐        ┌─────────────────┐        ┌──────────────┐
│   浏览器/PWA    │  HTTP  │  history-backend│  JDBC  │   MySQL 8    │
│  (Vite 构建产物)│ ─────▶ │  (Spring Boot)  │ ─────▶ │ history_museum│
└─────────────────┘        │   :8080         │        └──────────────┘
                           └─────────────────┘
                                   │
                                   │ HTTPS
                                   ▼
                           ┌─────────────────┐
                           │  智谱 GLM-4 API │  (LLM + Embedding)
                           └─────────────────┘
```

### 1.1 Docker Compose 拓扑

参考 [docker-compose.yml](../docker-compose.yml)：

| 服务 | 镜像 | 端口 | 数据卷 |
|---|---|---|---|
| mysql | mysql:8.0 | 3306:3306 | mysql_data |
| backend | 本地构建（见 [Dockerfile](../Dockerfile)） | 8080:8080 | — |

> 当前 docker-compose.yml 仅含 MySQL，后端在生产用独立 Dockerfile 构建。完整 Compose 拓扑待补 `backend` 服务定义。

### 1.2 单机部署（最小化）

- JDK 17 + Maven 3.9+
- MySQL 8.x（数据库 `history_museum`）
- 后端 JAR 一个，前端 Nginx 静态托管

---

## 2. 环境变量清单

参考 [application.yml](../src/main/resources/application.yml)，所有配置项均支持环境变量覆盖（`${VAR:default}` 语法）：

### 2.1 数据库

| 变量 | 默认值 | 说明 |
|---|---|---|
| `DB_HOST` | localhost | MySQL 主机 |
| `DB_USERNAME` | root | 数据库用户名 |
| `DB_PASSWORD` | root123 | 数据库密码（**生产必须改**） |

### 2.2 LLM（智谱 GLM-4）

| 变量 | 默认值 | 说明 |
|---|---|---|
| `LLM_BASE_URL` | https://open.bigmodel.cn/api/paas/v4 | LLM API 地址 |
| `LLM_API_KEY` | （空） | **必填**，否则 AI 功能不可用 |
| `LLM_MODEL` | glm-4-flash | 模型名 |

### 2.3 Embedding（RAG 向量化）

| 变量 | 默认值 | 说明 |
|---|---|---|
| `EMBEDDING_PROVIDER` | zhipu | 提供方 |
| `EMBEDDING_BASE_URL` | https://open.bigmodel.cn/api/paas/v4 | Embedding API |
| `EMBEDDING_API_KEY` | （空） | **必填**，与 LLM_API_KEY 通常一致 |
| `EMBEDDING_MODEL` | embedding-3 | 模型名 |
| `EMBEDDING_DIMENSIONS` | 1024 | 向量维度 |

### 2.4 RAG 向量存储

| 变量 | 默认值 | 说明 |
|---|---|---|
| `RAG_VECTOR_STORE` | mysql | 向量存储后端（mysql / in-memory） |
| `RAG_TOP_K` | 5 | 检索返回 Top N |
| `RAG_AUTO_INDEX` | true | 启动时是否自动灌库 |

### 2.5 Redis（可选，预留）

| 变量 | 默认值 | 说明 |
|---|---|---|
| `REDIS_HOST` | localhost | Redis 主机 |
| `REDIS_PORT` | 6379 | Redis 端口 |
| `RAG_REDIS_INDEX` | history_vectors | Redis 向量索引名 |

> ⚠️ 当前代码默认使用 `mysql` 向量存储，Redis 配置为预留项，未实际启用。

### 2.6 生产 `.env` 示例

```bash
# 数据库
DB_HOST=mysql
DB_USERNAME=museum
DB_PASSWORD=<强密码>

# LLM
LLM_API_KEY=<智谱 API Key>
EMBEDDING_API_KEY=<智谱 API Key>

# RAG
RAG_VECTOR_STORE=mysql
RAG_AUTO_INDEX=true
```

---

## 3. 部署流程

### 3.1 开发环境

```bash
# 1. 启动 MySQL（用 docker-compose）
docker-compose up -d mysql

# 2. 配置环境变量（可选，默认值适用于本地开发）
export LLM_API_KEY=<your_key>
export EMBEDDING_API_KEY=<your_key>

# 3. 启动后端
mvn spring-boot:run

# 4. 启动前端（另开终端）
cd ../history-frontend
npm install
npm run dev   # http://localhost:3000
```

### 3.2 生产环境 — Docker 镜像

#### 3.2.1 构建后端镜像

```bash
cd history-backend

# 构建镜像（多阶段：maven 编译 → jre-alpine 运行）
docker build -t history-backend:latest .
```

[Dockerfile](../Dockerfile) 已配置：
- 阶段 1：`maven:3.9-eclipse-temurin-17` 编译打包
- 阶段 2：`eclipse-temurin:17-jre-alpine` 运行（镜像约 200MB）
- 启动命令：`java -jar app.jar --spring.profiles.active=prod`

#### 3.2.2 运行容器

```bash
docker run -d \
  --name history-backend \
  -p 8080:8080 \
  -e DB_HOST=mysql \
  -e DB_USERNAME=museum \
  -e DB_PASSWORD=<强密码> \
  -e LLM_API_KEY=<智谱 Key> \
  -e EMBEDDING_API_KEY=<智谱 Key> \
  -e RAG_VECTOR_STORE=mysql \
  history-backend:latest
```

#### 3.2.3 前端部署

```bash
cd history-frontend
npm run build    # 产物在 dist/
# 用 Nginx 托管 dist/，参考 nginx.conf
```

### 3.3 数据库迁移

Flyway 启动时**自动执行**迁移脚本（[db/migration/](../src/main/resources/db/migration/)），无需手动操作。

当前迁移版本：

| 版本 | 说明 |
|---|---|
| V20260621_003~005 | 初始 seed（events/persons/knowledge） |
| V20260625_002 | 古文经典 seed |
| V20260712_006~011 | 朝代 / 古文补充 / 人物深度 / 清理测试数据 |
| V20260718_001 | 创建 `rag_vectors` 表（向量持久化） |
| V20260718_002 | 创建 `page_views` 表（页面访问埋点） |
| V20260718_003 | 创建 `user_personas` 表（AI 画像持久化） |

> 修复历史迁移：如需重跑某个版本，删除 `flyway_schema_history` 对应记录并恢复 DB 状态，参考 [scripts/fix_rag_vectors_column.sql](../scripts/fix_rag_vectors_column.sql)。

---

## 4. 监控与告警

### 4.1 日志

- 输出：`stdout`（容器友好）
- 格式：`MM-dd HH:mm:ss [thread] LEVEL logger - msg`（见 [application.yml](../src/main/resources/application.yml) `logging.pattern.console`）
- 等级：`com.history=DEBUG, sql=DEBUG`（生产建议改为 `INFO`）

### 4.2 性能监控

- **async-profiler JFR**：IDEA Run/Debug 配置中启用，输出 `.jfr` 文件可用 JMC 分析
  - 示例命令行参数：`-agentpath:libasyncProfiler.dll=version,jfr,event=wall,interval=10ms,file=...jfr`
- **Spring Boot Actuator**：当前未启用，建议生产添加 `spring-boot-starter-actuator` + `micrometer-prometheus`

### 4.3 关键指标

| 指标 | 查询方式 | 说明 |
|---|---|---|
| RAG 索引数量 | `SELECT COUNT(*) FROM rag_vectors` | 启动时应 > 0，二次启动不重建 |
| 页面热度 | `SELECT page_path, COUNT(*) FROM page_views GROUP BY page_path ORDER BY 2 DESC LIMIT 20` | A1.3 埋点 |
| 用户画像数 | `SELECT COUNT(*) FROM user_personas` | T088.9 持久化 |
| 在线游戏房间 | `GET /api/game/rooms` | 联机剧本杀 |

### 4.4 告警建议

- LLM API 调用失败率 > 5% → 检查 `LLM_API_KEY` 与配额
- RAG 索引为 0 → 检查 `EMBEDDING_API_KEY` 与首次启动日志
- MySQL 连接池满 → HikariCP `maximum-pool-size` 调优

---

## 5. 回滚方案

### 5.1 应用回滚

```bash
# 1. 停止当前版本
docker stop history-backend

# 2. 启动旧版本镜像
docker run -d --name history-backend ... history-backend:<旧版本>

# 3. 验证健康
curl http://localhost:8080/actuator/health   # 待 actuator 启用
```

### 5.2 数据库回滚

Flyway 不支持自动回滚（社区版）。建议：

1. **备份**：每次发布前 `mysqldump history_museum > backup_YYYYMMDD.sql`
2. **回滚**：`mysql history_museum < backup_YYYYMMDD.sql`
3. **同步 `flyway_schema_history`**：删除回滚版本之后的记录

### 5.3 紧急降级

| 故障 | 降级方案 |
|---|---|
| LLM API 不可用 | 前端 `ragApi` / `llmApi` 已有 try-catch，降级为提示"AI 服务暂不可用" |
| MySQL 不可用 | 后端启动失败，无降级（数据库是核心依赖） |
| 向量检索慢 | 设置 `RAG_TOP_K=3`，或临时切回 `RAG_VECTOR_STORE=in-memory` |

---

## 6. 参考资料

- [README.md](../README.md) — 项目快速开始
- [SETUP.md](./SETUP.md) — 详细开发环境搭建
- [API.md](./API.md) — 接口文档
- [history-museum/ARCHITECTURE.md](../../history-museum/ARCHITECTURE.md) — 架构设计（注意部分为设计稿，与实际实现有偏差）

---

*本文档基于实际 `application.yml` + `Dockerfile` + `docker-compose.yml` 编写，2026-07-18 完成。*
