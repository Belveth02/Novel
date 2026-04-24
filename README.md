# 小说网站

一个前后端分离的小说阅读平台，提供用户阅读、小说管理和后台管理功能。

## 项目概述

本系统采用前后端分离架构，基于 Java 17 + Spring Boot 3.5.13 技术栈开发，前端使用 Vue 3 + Element Plus 构建。系统包含前台用户模块和后台管理模块，支持小说浏览、章节阅读、用户互动、内容管理等完整功能。

## 功能模块

### 前台用户模块

| 模块 | 功能说明 |
|------|----------|
| 用户管理 | 用户注册、登录、个人中心、修改密码 |
| 小说浏览 | 分类浏览、小说搜索、小说详情、排行榜 |
| 阅读功能 | 章节阅读、目录导航、阅读设置、章节切换 |
| 互动功能 | 发表评论、查看评论、收藏小说、我的书架 |

### 后台管理模块

| 模块 | 功能说明 |
|------|----------|
| 小说管理 | 添加小说、编辑小说、删除小说、上架/下架 |
| 章节管理 | 添加章节、编辑章节、删除章节、章节排序 |
| 用户管理 | 查看用户、禁用/启用用户、重置密码 |
| 分类管理 | 添加分类、编辑分类、删除分类 |

## 技术栈

### 后端
- Java 17
- Spring Boot 3.5.13
- Spring Security（安全认证）
- Spring Validation（参数校验）
- Spring Cache + Caffeine（缓存）
- MyBatis Plus 3.5.15（ORM框架）
- MySQL 8.0（数据库）
- Maven（构建工具）

### 前端
- Vue 3.4+
- Vite 5.x（构建工具）
- Element Plus 2.x（UI组件库）
- Vue Router 4（路由管理）
- Pinia（状态管理）
- TypeScript
- Axios（HTTP请求）

### 部署运维
- Docker（容器化）
- Docker Compose（编排部署）
- Nginx（反向代理）

## 系统架构

### 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端层                              │
│  ┌─────────────┐    ┌─────────────┐                        │
│  │   浏览器     │    │   移动端     │                        │
│  └──────┬──────┘    └──────┬──────┘                        │
└─────────┼──────────────────┼────────────────────────────────┘
          │                  │
          └──────────┬───────┘
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                      负载均衡层                              │
│                     ┌─────────┐                            │
│                     │  Nginx  │                            │
│                     └────┬────┘                            │
└──────────────────────────┼──────────────────────────────────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   前端服务层     │ │    后端服务层    │ │   数据存储层     │
│  ┌───────────┐  │ │  ┌───────────┐  │ │  ┌───────────┐  │
│  │  Vue前端  │  │ │  │ Controller│  │ │  │   MySQL   │  │
│  │  Element  │  │ │  │   Service │  │ │  │    8.0    │  │
│  │  VueRouter│  │ │  │   Mapper  │  │ │  └─────┬─────┘  │
│  │   Pinia   │  │ │  │  Security │  │ │        │        │
│  │   Axios   │  │ │  │   Cache   │  │ │  ┌─────┴─────┐  │
│  └───────────┘  │ │  └───────────┘  │ │  │  Caffeine │  │
│                 │ │                 │ │  │   缓存    │  │
│   novel-frontend│ │   Spring Boot   │ │  └───────────┘  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
┌─────────────────────────────────────────────────────────────┐
│                      基础设施层                              │
│   Docker + Docker Compose（容器化部署）                      │
└─────────────────────────────────────────────────────────────┘
```

### 分层架构

系统后端采用经典的三层架构设计：

```
┌─────────────────────────────────────┐
│           Controller 层              │
│   接收请求、参数校验、响应封装        │
├─────────────────────────────────────┤
│            Service 层                │
│   业务逻辑处理、事务管理             │
├─────────────────────────────────────┤
│            Mapper 层                 │
│   数据访问、数据库操作               │
├─────────────────────────────────────┤
│            Entity 层                 │
│   数据实体定义                       │
└─────────────────────────────────────┘
```

## 数据库设计

### 核心实体

| 实体 | 说明 | 主要字段 |
|------|------|----------|
| User | 用户 | id, username, password, email, avatar, role, createTime |
| Novel | 小说 | id, title, author, cover, description, categoryId, status, chapterCount |
| Chapter | 章节 | id, novelId, title, content, chapterOrder, createTime |
| Category | 分类 | id, name, description, sortOrder |
| Comment | 评论 | id, userId, novelId, content, createTime |
| Favorite | 收藏 | id, userId, novelId, createTime |

### 实体关系

- 一个用户可以有多个收藏（1:N）
- 一个小说属于一个分类（N:1）
- 一个小说包含多个章节（1:N）
- 一个用户可以对多个小说发表评论（1:N）

## 项目结构

```
Novel/
├── src/main/java/com/example/novel/     # 后端源码
│   ├── config/                           # 配置类
│   ├── controller/                       # 控制器层
│   ├── service/                          # 服务层
│   ├── mapper/                           # 数据访问层
│   ├── entity/                           # 实体类
│   ├── dto/                              # 数据传输对象
│   ├── vo/                               # 视图对象
│   ├── common/                           # 公共组件
│   │   ├── core/                         # 核心类（Result等）
│   │   └── exception/                    # 异常处理
│   └── NovelApplication.java             # 启动类
├── src/main/resources/
│   ├── application.yml                   # 主配置
│   ├── application-dev.yml               # 开发配置
│   ├── application-prod.yml              # 生产配置
│   └── mapper/                           # MyBatis映射文件
├── novel-frontend/                       # 前端项目
│   ├── src/
│   │   ├── api/                          # API接口
│   │   ├── components/                   # 公共组件
│   │   ├── router/                       # 路由配置
│   │   ├── stores/                       # Pinia状态管理
│   │   ├── views/                        # 页面视图
│   │   └── types/                        # TypeScript类型
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml                    # Docker编排文件
├── Dockerfile                            # 后端Dockerfile
├── nginx.conf                            # Nginx配置
└── README.md
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0
- Docker（可选）

### 本地开发

**1. 启动后端**
```bash
# 配置数据库（application-dev.yml）
mvn clean install
mvn spring-boot:run
```

**2. 启动前端**
```bash
cd novel-frontend
npm install
npm run dev
```

### Docker 部署

```bash
# 一键启动所有服务
docker-compose up -d
```

访问地址：
- 前台页面：http://localhost
- 后台管理：http://localhost/admin
- 后端API：http://localhost:8080/api

### 默认账号
- 管理员：admin / 123456

## API 接口

### 用户模块
- POST `/auth/register` - 用户注册
- POST `/auth/login` - 用户登录
- GET `/users/profile` - 获取个人信息
- PUT `/users/profile` - 修改个人信息

### 小说模块
- GET `/novels` - 分页查询小说列表
- GET `/novels/{id}` - 获取小说详情
- POST `/novels` - 添加小说（管理员）
- PUT `/novels/{id}` - 修改小说（管理员）
- DELETE `/novels/{id}` - 删除小说（管理员）

### 章节模块
- GET `/chapters` - 获取小说章节列表
- GET `/chapters/{id}` - 获取章节内容
- POST `/chapters` - 添加章节（管理员）
- PUT `/chapters/{id}` - 修改章节（管理员）
- DELETE `/chapters/{id}` - 删除章节（管理员）

### 评论模块
- GET `/comments` - 获取小说评论列表
- POST `/comments` - 发表评论
- DELETE `/comments/{id}` - 删除评论
- GET `/comments/count` - 获取评论数量

### 收藏模块
- GET `/favorites` - 获取用户收藏列表
- POST `/favorites/toggle` - 收藏/取消收藏
- GET `/favorites/check` - 检查收藏状态
- GET `/favorites/count` - 获取收藏数量

## 技术特点

1. **前后端分离**：前后端独立开发、部署，通过 RESTful API 通信
2. **分层架构**：严格遵循 Controller → Service → Mapper 三层架构
3. **统一响应**：使用 `Result<T>` 统一API响应格式
4. **参数校验**：Spring Validation 确保输入数据合法性
5. **全局异常**：统一异常处理，返回友好的错误信息
6. **缓存优化**：Spring Cache + Caffeine 提升系统性能
7. **安全防护**：密码 BCrypt 加密，防止 SQL 注入
8. **容器化部署**：Docker 一键部署，环境一致性保障

## 开发规范

请参考项目根目录的 CLAUDE.md 文件，遵循其中的编码规范和开发流程。

## 许可证

MIT License
