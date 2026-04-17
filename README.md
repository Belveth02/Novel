# 小说网站

一个前后端分离的小说阅读平台，提供用户阅读、小说管理和后台管理功能。

## 技术栈

### 后端
- Java 17.0.10
- Spring Boot 2.7.18
- MyBatis Plus 3.5.7
- MySQL 8.0.37

### 前端
- Vue 3.4+
- Vite 5.x
- Element Plus 2.x
- TypeScript

## 项目结构

```
src/main/java/com/example/novel/
├── config/          # 配置类
├── controller/      # 控制器层
├── service/         # 服务层
├── mapper/          # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── vo/              # 视图对象
├── exception/       # 异常处理
└── NovelApplication.java
```

## 数据库配置

- 用户名：root
- 密码：123456
- 数据库：novel_db（需手动创建）

## 开发规范

请阅读项目根目录的 CLAUDE.md 文件，遵循其中的编码规范和开发流程。

## 运行方式

### 后端启动
```bash
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

## 功能模块

1. **用户系统**：注册、登录、个人中心
2. **小说阅读**：分类浏览、小说列表、详情、章节阅读
3. **后台管理**：小说管理、章节管理、用户管理