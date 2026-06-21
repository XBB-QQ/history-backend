# 五千年史馆 — 后端服务

Java 17 + Spring Boot 3.2 + JPA + MySQL/H2

## 快速开始

### 开发环境（H2 内存数据库，无需安装 MySQL）

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run
```

启动后访问：
- 应用：http://localhost:8080
- Swagger UI：http://localhost:8080/swagger-ui.html
- H2 Console：http://localhost:8080/h2-console

### 生产环境（MySQL）

```bash
# 1. 启动 MySQL
docker compose up -d

# 2. 修改 application.yml 中的 MySQL 配置（取消注释）

# 3. 初始化数据库
mysql -uroot -proot history_museum < src/main/resources/data/init.sql

# 4. 导入种子数据
mysql -uroot -proot history_museum < src/main/resources/data/seed.sql

# 5. 运行
mvn spring-boot:run
```

## API 文档

| 模块 | 路径 |
|------|------|
| 事件 | `/api/v1/events` |
| 人物 | `/api/v1/persons` |
| 朝代 | `/api/v1/dynasties` |
| 知识卡片 | `/api/v1/knowledge` |

### 核心接口

```
GET    /api/v1/events             事件列表（分页/筛选）
GET    /api/v1/events/timeline    时间轴事件（按年份排序）
GET    /api/v1/events/{id}        事件详情
GET    /api/v1/events/search?keyword=xxx  搜索

GET    /api/v1/persons            人物列表
GET    /api/v1/persons/{id}       人物详情
GET    /api/v1/persons/search?keyword=xxx 搜索

GET    /api/v1/dynasties          朝代列表
GET    /api/v1/dynasties/{id}     朝代详情

GET    /api/v1/knowledge          知识卡片列表
GET    /api/v1/knowledge/{id}     知识卡片详情
```

## 项目结构

```
src/main/java/com/history/
├── HistoryApplication.java       # 启动类
├── config/                       # 配置类
│   └── WebConfig.java           # CORS 跨域配置
├── entity/                       # JPA 实体
│   ├── EventEntity.java
│   ├── PersonEntity.java
│   ├── DynastyEntity.java
│   └── KnowledgeCardEntity.java
├── dto/                          # 数据传输对象
│   ├── EventDTO.java
│   ├── PersonDTO.java
│   ├── DynastyDTO.java
│   └── KnowledgeCardDTO.java
├── repository/                   # 数据访问层
│   ├── EventRepository.java
│   ├── PersonRepository.java
│   ├── DynastyRepository.java
│   └── KnowledgeCardRepository.java
├── service/                      # 业务逻辑层
│   ├── EventService.java
│   ├── PersonService.java
│   ├── DynastyService.java
│   ├── KnowledgeCardService.java
│   └── impl/
├── controller/                   # REST 控制器
│   ├── EventController.java
│   ├── PersonController.java
│   ├── DynastyController.java
│   └── KnowledgeCardController.java
└── exception/                    # 异常处理
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```
