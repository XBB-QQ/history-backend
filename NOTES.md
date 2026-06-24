# 注意事项

## 后台管理系统

### 访问地址
- 前台首页：`http://localhost:5173`
- 后台登录：`http://localhost:5173/admin/login`

### 默认账号
- 用户名：`admin`
- 密码：`admin123`
- 角色：管理员（admin）

### 管理模块
- 事件管理 — 增删改查历史事件
- 人物管理 — 增删改查历史人物
- 朝代管理 — 增删改查朝代信息
- 知识卡片 — 增删改查知识卡片

### 注意事项
1. 必须先启动后端服务（`mvn spring-boot:run`）再访问后台
2. 首次启动后端会自动初始化管理员账号和示例数据
3. 后台使用 API Key 认证，登录后会获得 API Key 并存储在 localStorage
4. 如果需要重置管理员密码，可以在数据库中删除 `admin_users` 表的数据，重启后端会自动重建
5. 生产环境务必修改默认密码和 API Key

---

### 用户系统

#### 访问地址
- 登录：`http://localhost:5173/login`
- 注册：`http://localhost:5173/register`
- 个人资料：`http://localhost:5173/profile`

#### 功能说明
- 用户注册后自动登录
- 收藏和评论需要登录才能操作
- 用户信息存储在 localStorage，刷新页面保持登录状态
- 密码使用 SHA-256 + Salt 加密存储

---

### 后端服务

#### 启动方式
```bash
cd history-backend
mvn spring-boot:run
```

#### 数据库
- MySQL 8.x
- 默认端口：3306
- 数据库名：`history_museum`
- 需要先在 MySQL 中创建数据库

#### 配置文件
- `src/main/resources/application.yml` — 开发环境配置
- `src/main/resources/application-prod.yml` — 生产环境配置

#### 端口
- 后端 API：8080
- 前端开发服务器：5173

---

### 部署相关

#### Docker 一键启动
```bash
docker-compose up -d
```

#### 前端构建
```bash
cd history-frontend
npm run build
```

#### 后端构建
```bash
cd history-backend
mvn package
```
