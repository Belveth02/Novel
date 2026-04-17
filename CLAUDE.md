---
# 小说网站项目规范

## 一、核心原则
- 可读性优先：代码是写给人看的
- 单一职责：一个方法只做一件事
- 命名即文档：避免无意义命名
- 失败快速：参数校验前置，异常早抛

## 二、架构规范
后端分层（强制）：Controller → Service → Mapper

禁止：
- Controller 直接操作 Mapper
- 返回 Entity 给前端

统一规范：
- 返回：Result<T>
- 入参：DTO
- 出参：VO

## 三、编码规范
1. 命名：
   - 类：UserService
   - 方法：getUserById()
   - 变量：userName
   - 常量：MAX_SIZE

2. 依赖注入：
   - 必须使用构造器注入（@RequiredArgsConstructor）
   - 禁止 @Autowired 字段注入

3. MyBatis Plus：
   - 使用 LambdaQueryWrapper
   - 禁止 SQL 字符串拼接

## 四、事务与异常
1. 查询方法：@Transactional(readOnly = true)
2. 写操作：@Transactional(rollbackFor = Exception.class)
3. 异常处理：
   - 使用 BusinessException
   - 全局异常处理 @RestControllerAdvice
   - 禁止 e.printStackTrace()

## 五、日志规范
正确：log.info("操作成功, id={}", id)
禁止：字符串拼接、打印敏感信息

## 六、安全规范
- 密码必须 BCrypt 加密
- 禁止 SQL 拼接
- 禁止返回 password 字段
- 禁止日志输出敏感信息

## 七、前端规范（Vue3）
- API 统一封装到 api/ 目录
- 使用 TypeScript 定义类型
- 接口必须处理 loading 状态
- 异常统一 ElMessage 提示

## 八、开发流程（最重要）
每次开发必须遵循：
1. 先分析任务
2. 输出方案
3. 等待确认
4. 再写代码
5. 提供测试方法
6. 完成后停止

禁止：
- 一次实现多个模块
- 输出无关代码
- 未经确认直接开发

## 九、提交前检查
- 代码可编译
- 无异常未处理
- 有参数校验
- 无敏感信息泄露
---