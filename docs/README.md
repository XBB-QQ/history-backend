# 五千年史馆后端 · 代码契约文档

> 本目录**只放与代码强耦合的文档**。
> 项目知识（规划/迭代/设计/ADR）在 `D:\claudeCode\history-museum\`。

---

## 📂 目录内容

```
history-backend/docs/
├── README.md     # 本文档
├── API.md        # 接口契约（前端依据此对接）
├── SETUP.md      # 本地开发环境搭建 + 数据库初始化
└── DEPLOY.md     # 部署文档（待补）
```

---

## 🔗 查文档导航

| 需求 | 去哪里 |
|---|---|
| 项目整体规划 | `history-museum/PROJECT-PLAN.md` |
| 架构设计/插件机制 | `history-museum/ARCHITECTURE.md` |
| 任务清单 | `history-museum/TASKS.md` |
| **迭代记录**（做了什么） | `history-museum/ITERATIONS.md` |
| **某功能的设计方案** | `history-museum/design/` |
| **技术选型决策** | `history-museum/adr/` |
| **API 接口契约** | 本目录 `API.md` |
| **本地环境搭建** | 本目录 `SETUP.md` |
| **生产部署** | 本目录 `DEPLOY.md` |
| 原型文件 | `history-museum/index.html` |
| 结构化数据 | `history-museum/data/*.json` |

---

## 📋 何时更新本目录文档

### `API.md`
- 新增/修改/删除任何 REST 接口
- 请求/响应字段变更
- 认证方式调整

### `SETUP.md`
- 数据库迁移脚本新增
- 环境变量变更
- 依赖版本升级

### `DEPLOY.md`
- Docker 配置变更
- CI/CD 流程调整
- 服务器架构变化

---

## ⚠️ 注意

**不要在本目录写：**
- ❌ 迭代记录（去 `history-museum/ITERATIONS.md`）
- ❌ 设计方案（去 `history-museum/design/`）
- ❌ 架构决策（去 `history-museum/adr/`）
- ❌ 任务规划（去 `history-museum/TASKS.md`）

保持本目录精简，只保留**代码契约类**文档。

---

*最后更新：2026-06-25*
