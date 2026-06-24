# 迭代规划

> 本文档记录项目功能迭代的完整规划，包含已完成功能和待开发功能。

---

## 已完成迭代

### Iteration #19 — 用户体系 ✅
### Iteration #20 — 历史上的今天 + 每日推荐 ✅

**目标：** 完整的用户注册/登录/个人资料系统

**功能清单：**
- [x] 用户实体（UserEntity）— 用户名、密码、邮箱、手机号、头像、简介、角色
- [x] JWT 认证（JwtUtil + JwtAuthenticationFilter）
- [x] 用户注册接口（POST /api/auth/register）
- [x] 用户登录接口（POST /api/auth/login）
- [x] 个人信息管理（GET/PUT /api/auth/me）
- [x] 前端用户 Store（Zustand + localStorage 持久化）
- [x] 登录/注册/个人中心页面
- [x] Navbar 用户菜单（头像下拉、登录按钮、退出登录）
- [x] 收藏和评论功能绑定用户认证
- [x] SHA-256 + Salt 密码加密
- [x] API Key 认证过滤器（ApiKeyAuthenticationFilter）— 解决 admin 登录 403 问题
- [x] SecurityConfig 完善 — JWT 过滤器角色权限设置、CORS 暴露 X-API-Key

**技术栈：** Spring Security、JWT（jjwt 0.12.6）、Zustand、localStorage

---

### Iteration #20 — 历史上的今天 + 每日推荐 ✅

**目标：** 增加每日内容更新，提升用户回访率

**功能清单：**
- [x] 后端 API — `GET /api/public/today` 返回今天发生的历史事件
- [x] 后端 API — `GET /api/public/daily-recommend` 返回随机推荐事件
- [x] 后端 — `TodayInHistoryService` 按月份日期索引事件（yearDisplay/fulltext/description 提取）
- [x] 前端 — 首页 `TodayBanner` 展示"历史上的今天"
- [x] 前端 — `DailyRecommendCard` 每日推荐卡片（可刷新换一条）
- [x] 数据复用现有事件库，按月份日期索引
- [x] 支持深色模式适配

**技术栈：** Spring Boot REST、Zustand、Tailwind CSS

## 待开发迭代

### Iteration #20 — 历史上的今天 + 每日推荐

**目标：** 增加每日内容更新，提升用户回访率

**功能清单：**
- [ ] 后端 API — `GET /api/public/today` 返回今天发生的历史事件
- [ ] 后端 API — `GET /api/public/random-event` 返回随机推荐事件
- [ ] 前端页面 — 首页顶部 Banner 展示"历史上的今天"
- [ ] 前端页面 — 每日推荐卡片组件（可刷新）
- [ ] 数据复用现有事件库，按月份日期索引
- [ ] 支持深色模式适配

**优先级：** ⭐⭐⭐
**预估工作量：** 1-2 天

---

### Iteration #21 — 朝代专属配色 + 视觉升级

**目标：** 增强视觉沉浸感，不同朝代有不同氛围

**功能清单：**
- [ ] 新增 ThemeManager 组件 — 根据当前页面/选中的朝代切换全局配色
- [ ] 定义 13 个朝代的专属配色方案（主色、背景色、粒子颜色）
- [ ] 背景水墨粒子颜色跟随主题动态变化
- [ ] 页面切换时配色渐变过渡动画（500ms）
- [ ] 暗色模式下自动适配配色
- [ ] 首页 Hero 区域添加多层视差滚动（远山、近水、文字分层）
- [ ] 页面路由切换时添加卷轴展开/收起转场动画

**优先级：** ⭐⭐⭐
**预估工作量：** 2-3 天

---

### Iteration #22 — 人物关系图谱 ✅

**目标：** 可视化展示历史人物之间的关联

**功能清单：**
- [x] 后端 — PersonEntity 新增 `relationships` 字段（JSON TEXT，存储关联人物 ID 和关系类型）
- [x] 后端 — `GET /api/v1/persons/{id}/relationships` 返回人物关系链（含关联人物信息）
- [x] 后端 — `RelationshipDTO`（targetUid, relation, label）
- [x] 后端 — `PersonServiceImpl` 使用 ObjectMapper 序列化/反序列化关系 JSON
- [x] 前端 — 人物详情页新增"关系图谱"Tab（基本信息 / 关系图谱切换）
- [x] 前端 — 自定义 SVG 力导向图（无外部依赖，物理模拟：斥力+引力+居中力）
- [x] 关系类型可视化：15+ 关系类型，每种颜色编码（师徒/父子/敌对/夫妻/君臣等）
- [x] 关系线条样式：敌对/仇敌用虚线，其他用实线
- [x] 图例展示所有关系类型及其颜色
- [x] 响应式图表尺寸（窗口 resize 自适应）
- [x] 中心节点固定，外围节点浮动布局
- [x] 前端 `FrontendPerson` 类型新增 `uid` 字段，适配后端 DTO
- [x] 修复 `DailyRecommendCard` 中 `openDetail` 传参错误（之前传字符串而非对象）

**优先级：** ⭐⭐⭐
**预估工作量：** 3-4 天

---

### Iteration #23 — 时间旅行模式 ✅

**目标：** 通过年代滑块联动全局内容展示

**功能清单：**
- [x] `TimeTravelBar` 组件 — 顶部固定年代滑块（-3000 ~ 3000）
- [x] 滑块刻度：千年/百年/十年/年四级精度切换
- [x] 选中年代时，首页展示该年代主要事件卡片（TimeTravelPanel）
- [x] 背景粒子密度随时间旅行激活翻倍（InkParticles 联动）
- [x] 滑块支持键盘方向键微调（store.arrowAdjust）
- [x] 快速跳转按钮：9 个重大历史年代（秦统一、三国、唐、宋、元、明、清、民国、新中国）
- [x] Zustand store（timeTravelStore）管理状态
- [x] useFilteredByYear hook 用于按年代过滤事件/人物
- [x] ESC 键关闭时间旅行面板
- [x] 滑块自动对齐精度步长

**优先级：** ⭐⭐⭐
**预估工作量：** 3-4 天

---

### Iteration #24 — 历史问答挑战 ✅

**目标：** 游戏化互动，提升用户参与度

**功能清单：**
- [x] 后端 — `QuestionEntity`（题目、选项 JSON、正确答案、难度、关联事件/人物/朝代）
- [x] 后端 — `QuestionRepository`（按难度/朝代/分类查询、随机选题）
- [x] 后端 — `POST /api/user/quiz/answer` 提交答案（积分自动累加）
- [x] 后端 — `GET /api/user/quiz/daily` 基于日期哈希选取每日题目
- [x] 后端 — `GET /api/user/quiz/random` 随机题目列表（练习模式）
- [x] 后端 — `GET /api/user/quiz/ranking` 积分排行榜（按 score 降序）
- [x] 后端 — `UserEntity` 新增 score/quizzesAnswered/quizzesCorrect 字段
- [x] 后端 — 55 道题库种子数据（覆盖先秦→现代 + 科技/军事/政治/文化/地理/经济）
- [x] 前端 — `QuizDialog` 弹窗组件（选项点击→提交→即时显示对错+解析）
- [x] 前端 — 每日挑战入口（首页 Banner 下方渐变卡片）
- [x] 前端 — 积分系统（答对 easy +10 / medium +20 / hard +30）
- [x] 前端 — 排行榜页面 `/leaderboard`（Top 20，金银铜奖牌）
- [x] 前端 — Navbar 添加"挑战"导航入口
- [x] 前端 — UserDTO 扩展 score/quizzesAnswered/quizzesCorrect 字段
- [x] 前端 — userStore.updateQuizScore() 本地积分更新
- [x] 安全配置 — quiz/daily、quiz/random、quiz/ranking 公开访问

**优先级：** ⭐⭐⭐
**预估工作量：** 3-4 天

---

### Iteration #25 — 人物对比 ✅

**目标：** 并排展示两位历史人物的详细信息

**功能清单：**
- [x] 后端 — `GET /api/v1/persons/compare?id1=x&id2=y` 返回对比数据（PersonCompareDTO）
- [x] 前端 — `/compare` 路由页面（ComparePage）
- [x] 前端 — 双栏对比视图：姓名、朝代、生卒年、简介、名言、身份、标签
- [x] 前端 — 搜索选人（实时搜索，按姓名/标签匹配，最多显示10条）
- [x] 前端 — 重置对比功能
- [x] Navbar 添加"对比"导航入口

**优先级：** ⭐⭐
**预估工作量：** 1-2 天

---

### Iteration #26 — 学习进度追踪 + 阅读清单 ✅

**目标：** 帮助用户系统化地了解历史知识

**功能清单：**
- [x] 后端 — `LearningProgressEntity`（userId、resourceType、resourceId、viewCount、lastViewed）
- [x] 后端 — `ReadingListEntity`（userId、name、description、resources JSON）
- [x] 后端 — `POST /api/user/learning/view` 记录浏览
- [x] 后端 — `GET /api/user/learning/progress` 获取学习进度
- [x] 后端 — `GET/POST /api/user/learning/lists` 管理阅读清单
- [x] 后端 — `POST /api/user/learning/lists/{id}/resources` 添加资源
- [x] 后端 — `DELETE /api/user/learning/lists/{id}/resources/{resourceId}` 移除资源
- [x] 前端 — `/learning` 学习进度页面
- [x] 前端 — 创建/展开/折叠阅读清单
- [x] 前端 — 清单内资源管理（添加/移除）
- [x] 前端 — Zustand store（learningStore）管理状态
- [x] 前端 — Navbar 添加"学习"导航入口
- [ ] 后端 — `GET /api/user/progress` 获取学习进度
- [ ] 后端 — `POST /api/user/reading-lists` 创建/更新阅读清单
- [ ] 前端 — 个人中心新增"学习进度"Tab
  - [ ] 各模块完成度百分比（事件 XX%、人物 XX%）
  - [ ] 已浏览标记（灰色小圆点）
- [ ] 前端 — 阅读清单管理页面
  - [ ] 创建自定义清单（如"三国专题""唐宋八大家"）
  - [ ] 分享清单链接
- [ ] 前端 — 首页新增"继续学习"入口

**优先级：** ⭐⭐
**预估工作量：** 2-3 天

---

### Iteration #27 — 分享海报生成 ✅

**目标：** 用户可以分享精美历史卡片到社交媒体

**功能清单：**
- [x] 前端 — `PosterGenerator` 组件（Canvas API 生成海报）
- [x] 水墨风海报模板（宣纸色背景、印章风格标题、书法字体）
- [x] 分享弹窗新增"生成海报"Tab，与社交分享 Tab 切换
- [x] 分享内容包括标题、副标题、描述文字、底部水印
- [x] 支持下载 PNG 格式海报
- [x] ShareDialog 集成 `PosterGenerator`，DetailModal 传递 description

**优先级：** ⭐⭐
**预估工作量：** 2-3 天

---

### Iteration #28 — 跨实体关联查询 + 数据增强 ✅

**目标：** 深化数据维度，提升内容价值

**功能清单：**
- [x] 后端 — EventEntity 新增 `impact`(TEXT)、`significance`(int 1-5)、`relatedArticles`(List<String>)
- [x] 后端 — PersonEntity 新增 `birthPlace`、`deathPlace`、`achievements`(TEXT)
- [x] 后端 — DynastyEntity 新增 `populationPeak`、`gdpEstimate`、`majorTradeRoutes`、`culturalHighlights`
- [x] 后端 — 所有 DTO 更新映射新字段（EventDTO、PersonDTO、DynastyDTO）
- [x] 后端 — `GET /api/v1/events/{id}/related` 返回关联人物 + 知识卡片
- [x] 后端 — `GET /api/v1/dynasties/{id}/details` 返回朝代详情 + 关联事件/人物/知识卡片
- [x] 前端 — `BackendEventDTO/PersonDTO/DynastyDTO` 新增字段适配
- [x] 前端 — `FrontendEvent/Person/Dynasty` 类型新增增强字段
- [x] 前端 — 适配器更新（adaptEvent/adaptPerson/adaptDynasty）
- [x] 前端 — EventDetail 展示：重要度星级 ⭐、历史影响、相关文章
- [x] 前端 — PersonDetail 展示：出生地/逝世地标签、主要成就
- [x] 前端 — DynastyDetail 展示：人口峰值、GDP、文化亮点、贸易路线
- [x] 前端 — `fetchDynastyDetails()` / `fetchEventRelated()` API 函数

**优先级：** ⭐⭐⭐
**预估工作量：** 3-4 天

---

### Iteration #29 — 知识卡片标签云 + 筛选增强 ✅

**目标：** 提升知识卡片的发现和浏览体验

**功能清单：**
- [x] 后端 — `GET /api/v1/knowledge/tags` 返回标签统计（频率排序）
- [x] 后端 — `KnowledgeCardServiceImpl.getTagStatistics()` 聚合标签计数
- [x] 前端 — `TagCloud` 组件（动态字号映射频率，点击筛选）
- [x] 前端 — 知识卡片页面集成标签云 + 筛选
- [x] 前端 — 标签云显示计数徽章，当前选中高亮
- [x] 前端 — 清除筛选按钮
- [x] 前端 — DetailModal Recommendations 新增基于标签相似度的知识卡片推荐
- [x] 前端 — 事件详情页推荐相关知识点（标签交集匹配）
- [ ] 前端 — 知识卡片网格布局改为瀑布流（Masonry）
- [ ] 支持按标签组合筛选（AND/OR 逻辑）

**优先级：** ⭐⭐
**预估工作量：** 1-2 天

---

### Iteration #30 — 评论区增强 ✅

**目标：** 已有评论功能的深度优化

**功能清单：**
- [x] 前端 — 评论支持楼中楼回复（嵌套层级，点击"回复"按钮展开输入框）
- [x] 前端 — 评论点赞功能（空心/实心切换，点赞数实时更新）
- [x] 前端 — 评论排序选项（最新/最热）
- [x] 前端 — 评论 Markdown 基础支持（**粗体**、`代码`、URL 链接自动转<a>）
- [x] 前端 — 评论 textarea 替代 input（支持多行输入）
- [x] 前端 — 评论总数统计（含回复数）
- [x] 前端 — 空状态提示（"暂无评论，快来抢沙发吧！"）
- [x] 前端 — 回复列表左侧竖线缩进样式

**优先级：** ⭐⭐
**预估工作量：** 2-3 天

---

## 技术债务 & 优化

| 编号 | 内容 | 优先级 |
|------|------|--------|
| TD-01 | 后端 API 路径统一（当前混用 /api/v1/ 和 /api/） | ⭐⭐⭐ |
| TD-02 | 后端统一使用 Pageable 分页（部分接口无分页） | ⭐⭐ |
| TD-03 | 前端 API 客户端类型定义统一（减少 any 类型） | ⭐⭐ |
| TD-04 | 添加 E2E 测试（Playwright） | ⭐ |
| TD-05 | 后端添加接口限流（防止恶意请求） | ⭐⭐ |
| TD-06 | 前端添加 PWA 支持（离线缓存） | ⭐ |
| TD-07 | 数据库连接池优化（HikariCP 配置） | ⭐ |
| TD-08 | 添加国际化支持（i18n）— 中英文切换 | ⭐⭐ |

---

## 迭代依赖关系

```
#20 历史上的今天          → 无依赖（可独立开发）
#21 朝代配色 + 视觉升级    → 无依赖，但可与 #23 时间旅行联动
#22 人物关系图谱           → 依赖 #28 数据增强（relationship 字段）
#23 时间旅行模式           → 依赖 #21 视觉升级（主题切换）
#24 历史问答挑战           → 依赖 #19 用户体系（积分存储）
#25 人物对比               → 无依赖
#26 学习进度 + 阅读清单    → 依赖 #19 用户体系
#27 分享海报               → 依赖 #25 人物对比（可选组合）
#28 跨实体关联查询         → 无依赖，但为 #22 提供数据基础
#29 标签云                 → 无依赖
#30 评论区增强             → 依赖 #19 用户体系
```

---

## 功能全景图

```
五千年史馆
├── 内容展示
│   ├── 首页（Hero 动画 + 入口卡片）
│   ├── 时间轴（事件流 + 筛选）
│   ├── 历代朝代（网格 + 详情）
│   ├── 人物志（网格 + 详情 + 关系图谱）
│   ├── 史海钩沉（知识卡片 + 标签云）
│   ├── 中国历史疆域图（SVG 地图 + 朝代切换）
│   ├── 数据可视化（朝代持续时间 + 图表）
│   └── 历史上的今天（每日推荐）
├── 互动体验
│   ├── 时间旅行（年代滑块联动）
│   ├── 历史问答挑战（每日答题 + 排行榜）
│   ├── 人物对比（双栏对比视图）
│   └── 分享海报（社交分享）
├── 用户系统
│   ├── 注册/登录（JWT）
│   ├── 个人资料（编辑 + 学习进度）
│   ├── 我的收藏（分类 + 置顶）
│   ├── 阅读清单（自定义专题）
│   └── 评论互动（楼中楼 + 点赞）
└── 后台管理
    ├── 事件 CRUD
    ├── 人物 CRUD
    ├── 朝代 CRUD
    ├── 知识卡片 CRUD
    └── API Key 认证
```
