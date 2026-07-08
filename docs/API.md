# 五千年史馆 API 文档

> Base URL: `http://localhost:8080/api/v1`

## 目录

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 事件 | `/events` | 历史事件 CRUD、搜索、时间轴 |
| 人物 | `/persons` | 历史人物 CRUD、搜索、筛选 |
| 朝代 | `/dynasties` | 朝代 CRUD |
| 知识卡片 | `/knowledge` | 知识卡片 CRUD、搜索 |
| RAG 问答 | `/rag` | 检索增强生成（向量检索 + LLM 流式回答） |


---

## 1. 事件 (Events)

### 1.1 获取事件列表（分页）

```
GET /api/v1/events?page=0&size=20&category=战争&dynasty=秦&yearMin=-221&yearMax=-207
```

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码（从 0 开始） |
| size | int | 否 | 20 | 每页数量 |
| category | string | 否 | - | 分类筛选：`朝代更迭`/`战争`/`改革`/`文化`/`盛世`/`屈辱`/`革命`/`经济` |
| dynasty | string | 否 | - | 朝代名称筛选（如"秦"、"汉"） |
| yearMin | int | 否 | - | 起始年份（公元前为负） |
| yearMax | int | 否 | - | 结束年份 |

**响应示例：**
```json
{
  "content": [
    {
      "id": 1,
      "uid": "qin-unify",
      "title": "秦统一六国",
      "year": -221,
      "yearDisplay": "公元前221年",
      "yearPrecision": "exact",
      "category": "朝代更迭",
      "dynastyName": "秦",
      "description": "秦始皇灭六国，建立中国第一个统一的中央集权帝国...",
      "fulltext": "",
      "tags": ["统一", "战争", "政治"],
      "relatedEvents": ["chu-han-contend"],
      "relatedPersons": ["ying-zheng"]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 87,
  "totalPages": 5,
  "last": false,
  "first": true,
  "sort": [{"direction": "ASC", "property": "year"}]
}
```

### 1.2 获取时间轴事件

```
GET /api/v1/events/timeline
```

返回所有事件，按年份升序排列，无分页。用于时间轴可视化组件。

### 1.3 获取事件详情（按 ID）

```
GET /api/v1/events/{id}
```

### 1.4 获取事件详情（按 UID）

```
GET /api/v1/events/uid/{uid}
```

例如：`GET /api/v1/events/uid/qin-unify`

### 1.5 搜索事件

```
GET /api/v1/events/search?keyword=秦始皇&page=0&size=20
```

搜索范围：title、description、fulltext

---

## 2. 人物 (Persons)

### 2.1 获取人物列表

```
GET /api/v1/persons?page=0&size=20&gender=男&dynasty=汉&role=政治家
```

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 0 | 页码 |
| size | int | 否 | 20 | 每页数量 |
| gender | string | 否 | - | 性别：`男`/`女` |
| dynasty | string | 否 | - | 朝代名称 |
| role | string | 否 | - | 角色：`政治家`/`军事家`/`思想家`/`文学家`/`科学家`/`帝王` |

### 2.2 获取人物详情

```
GET /api/v1/persons/{id}
GET /api/v1/persons/uid/{uid}
```

### 2.3 搜索人物

```
GET /api/v1/persons/search?keyword=孔子
```

搜索范围：name、bio、quote

---

## 3. 朝代 (Dynasties)

### 3.1 获取朝代列表

```
GET /api/v1/dynasties?page=0&size=20
```

### 3.2 获取朝代详情

```
GET /api/v1/dynasties/{id}
GET /api/v1/dynasties/uid/{uid}       # 如 /uid/tang
GET /api/v1/dynasties/name/{name}     # 如 /name/唐
```

---

## 4. 知识卡片 (Knowledge Cards)

### 4.1 获取知识卡片列表

```
GET /api/v1/knowledge?page=0&size=20
```

### 4.2 获取知识卡片详情

```
GET /api/v1/knowledge/{id}
GET /api/v1/knowledge/uid/{uid}
```

### 4.3 搜索知识卡片

```
GET /api/v1/knowledge/search?keyword=丝绸之路
```

---

## 5. RAG 问答 (Retrieval-Augmented Generation)

> Iteration #94 实现。基于向量检索的 RAG 问答，支持 SSE 流式输出。
>
> 工作流程：用户问题 → Embedding 向量化 → 向量库 Top-K 检索 → 构建上下文 → LLM 流式生成回答

### 5.1 流式查询（SSE）

```
POST /api/v1/rag/query
Content-Type: application/json
Accept: text/event-stream
```

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| question | string | 是 | 用户问题 |
| docType | string | 否 | 限定文档类型：`event`/`person`/`dynasty`/`knowledge`/`topic` |

**请求示例：**
```json
{
  "question": "安史之乱原因",
  "docType": "event"
}
```

**响应：** `text/event-stream`，OpenAI 兼容 SSE 格式

```
data: {"choices":[{"delta":{"content":"安"}}]}

data: {"choices":[{"delta":{"content":"史"}}]}

data: {"choices":[{"delta":{"content":"之乱的原因包括..."}}]}

data: [DONE]
```

错误时返回：
```
data: {"error":"错误信息"}
```

### 5.2 非流式查询

```
POST /api/v1/rag/chat
```

**请求体：** 同 5.1

**响应示例：**
```json
{
  "answer": "安史之乱的原因包括藩镇割据、朝廷腐败等..."
}
```

### 5.3 仅检索（不调用 LLM）

```
POST /api/v1/rag/retrieve
```

返回 Top-K 相关文档列表，不调用 LLM 生成回答。

**响应示例：**
```json
[
  {
    "id": "event:1",
    "score": 0.85,
    "type": "event",
    "title": "安史之乱",
    "content": "事件：安史之乱（公元755年）\n分类：战争...",
    "source": "an-lushan",
    "year": 755,
    "category": "战争"
  }
]
```

| 字段 | 说明 |
|------|------|
| id | 文档 ID，格式 `<type>:<entityId>` |
| score | 余弦相似度（0~1，越大越相关） |
| type | 文档类型：`event`/`person`/`dynasty`/`knowledge`/`topic` |
| title | 文档标题（事件标题/人物名/朝代名等） |
| content | 用于向量化的完整文本 |
| source | 实体 uid，可用于前端跳转 |

### 5.4 向量库状态

```
GET /api/v1/rag/status
```

**响应示例：**
```json
{
  "vectorCount": 248,
  "storeType": "memory"
}
```

| 字段 | 说明 |
|------|------|
| vectorCount | 当前向量库中的文档总数 |
| storeType | 存储类型：`memory`（开发）或 `redis`（生产） |

### 5.5 配置

通过环境变量或 `application.yml` 配置：

```yaml
embedding:
  provider: ${EMBEDDING_PROVIDER:zhipu}        # zhipu | openai
  base-url: ${EMBEDDING_BASE_URL:https://open.bigmodel.cn/api/paas/v4}
  api-key: ${EMBEDDING_API_KEY:...}
  model: ${EMBEDDING_MODEL:embedding-3}
  dimensions: ${EMBEDDING_DIMENSIONS:1024}

rag:
  vector-store: ${RAG_VECTOR_STORE:memory}    # memory | redis
  top-k: ${RAG_TOP_K:5}                       # 检索 Top-K
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    index: ${RAG_REDIS_INDEX:history_vectors}
```

### 5.6 索引灌入

应用启动时 `RagIndexer`（`ApplicationRunner`，`@Order(100)`）自动从数据库读取所有事件、人物、朝代、知识卡片、专题，生成向量并灌入向量库。批量嵌入（BATCH_SIZE=16），失败批次跳过不阻塞启动。

---

## 通用规范

### 分页格式
遵循 Spring Data Page 标准格式：`content` + `totalElements` + `totalPages` + `pageable`

### 错误响应
```json
{
  "timestamp": "2026-06-21T12:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "未找到资源: 事件 999",
  "path": "/api/v1/events/999"
}
```

### Swagger UI
开发环境可通过 `http://localhost:8080/swagger-ui.html` 查看交互式 API 文档。
