# 五千年史馆 API 文档

> Base URL: `http://localhost:8080/api/v1`

## 目录

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 事件 | `/events` | 历史事件 CRUD、搜索、时间轴 |
| 人物 | `/persons` | 历史人物 CRUD、搜索、筛选 |
| 朝代 | `/dynasties` | 朝代 CRUD |
| 知识卡片 | `/knowledge` | 知识卡片 CRUD、搜索 |

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
