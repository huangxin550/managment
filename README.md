# 任务管理系统 (Task Management System)

基于 Spring Boot + MySQL + Redis 的高性能任务管理系统，支持任务的完整 CRUD 操作、智能筛选、依赖管理和全文搜索功能。

## 📋 项目简介

本系统是一个企业级任务管理平台，采用前后端分离架构设计，提供 RESTful API 接口。系统使用 MySQL 作为主数据库进行数据持久化，Redis 作为缓存层提升查询性能，满足十万级以上任务数据的扩容需求。

## ✨ 核心功能

### 1. 基础功能（CRUD）
- ✅ 创建任务 - 支持标题、描述、状态、优先级、标签等字段
- ✅ 查询任务 - 根据 ID 查询单个任务或分页查询所有任务
- ✅ 更新任务 - 修改任务的所有属性
- ✅ 删除任务 - 安全删除任务（检查依赖关系）

### 2. 高级筛选与排序
- 🔍 按状态筛选（待处理/进行中/已完成）
- 🔍 按优先级筛选（低/中/高）
- 🔍 按标签筛选（支持模糊匹配）
- 🔍 多条件组合筛选
- 📊 按创建时间、优先级等字段排序
- 📄 支持分页查询

### 3. 任务依赖管理
- 🔗 设置任务依赖关系
- 🔗 依赖约束检查（未完成依赖时禁止完成任务）
- 🔗 查询任务依赖树结构（递归展示）
- 🔗 防止循环依赖

### 4. 全文搜索
- 🔎 支持标题和描述的关键词搜索
- 🔎 高性能模糊匹配

### 5. 性能优化
- ⚡ Redis 缓存热点数据（24小时过期）
- ⚡ 数据库索引优化（状态、优先级、创建时间）
- ⚡ 连接池配置（HikariCP）
- ⚡ 懒加载优化（任务依赖关系）

## 🏗️ 技术栈

### 后端技术
- **框架**: Spring Boot 4.0.5
- **语言**: Java 17
- **ORM**: Spring Data JPA (Hibernate)
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **验证**: Jakarta Validation
- **构建工具**: Maven

### 数据存储
- **主数据库**: MySQL（关系型数据）
- **缓存层**: Redis（非关系型，K-V 存储）

## 📁 项目结构

```
src/main/java/com/example/demo/
├── config/                      # 配置类
│   └── RedisConfig.java        # Redis 配置
├── controllers/                 # 控制器层
│   └── TaskController.java     # RESTful API 接口
├── models/                      # 数据模型
│   ├── Task.java               # 任务实体
│   ├── TaskStatus.java         # 状态枚举
│   ├── TaskPriority.java       # 优先级枚举
│   ├── CreateTaskRequest.java  # 创建请求 DTO
│   ├── UpdateTaskRequest.java  # 更新请求 DTO
│   └── TaskResponse.java       # 响应 DTO
├── services/                    # 服务层
│   ├── TaskService.java        # 服务接口定义
│   ├── TaskRepository.java     # 数据访问接口
│   └── Impl/                   # 服务实现层
│       └── TaskServiceImpl.java # 任务服务实现类
├── utils/                       # 工具类
│   ├── ApiResponse.java        # 统一响应封装
│   ├── PageResult.java         # 分页结果封装
│   ├── TaskNotFoundException.java      # 自定义异常
│   ├── TaskDependencyException.java    # 依赖异常
│   └── GlobalExceptionHandler.java     # 全局异常处理
└── DemoApplication.java         # 启动类
```

## 🚀 快速开始

### 前置要求

1. **JDK 17+**
2. **Maven 3.6+**
3. **MySQL 8.0+**
4. **Redis 6.0+**

### 安装步骤

#### 1. 克隆项目
```bash
git clone <repository-url>
cd demo
```

#### 2. 配置数据库

确保 MySQL 服务已启动，然后修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_management?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

系统会自动创建数据库和表结构。

#### 3. 配置 Redis

确保 Redis 服务已启动，默认配置为：
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

#### 4. 编译项目
```bash
mvn clean install
```

#### 5. 运行应用
```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 📖 API 文档

### 基础路径
```
http://localhost:8080/api/tasks
```

### 1. 创建任务
**接口**: `POST /api/tasks`

**请求体**:
```json
{
  "title": "完成项目文档",
  "description": "编写完整的 API 文档和使用说明",
  "status": "pending",
  "priority": "high",
  "tags": ["文档", "紧急"],
  "dependencies": [1, 2]
}
```

**响应**:
```json
{
  "success": true,
  "message": "任务创建成功",
  "data": {
    "id": 3,
    "title": "完成项目文档",
    "description": "编写完整的 API 文档和使用说明",
    "status": "pending",
    "priority": "high",
    "createdAt": "2026-04-19T10:30:00",
    "updatedAt": "2026-04-19T10:30:00",
    "tags": ["文档", "紧急"],
    "dependencies": [1, 2],
    "dependents": []
  },
  "timestamp": 1713500000000
}
```

### 2. 查询任务（单个）
**接口**: `GET /api/tasks/{id}`

**示例**: `GET /api/tasks/1`

### 3. 查询所有任务（分页）
**接口**: `GET /api/tasks?page=0&size=10&sortBy=createdAt&order=desc`

**参数**:
- `page`: 页码（从 0 开始）
- `size`: 每页数量
- `sortBy`: 排序字段（createdAt, priority, status）
- `order`: 排序方式（asc, desc）

### 4. 筛选任务
**接口**: `GET /api/tasks/filter?status=pending&priority=high&tag=紧急&page=0&size=10`

**参数**:
- `status`: 任务状态（pending, in_progress, completed）
- `priority`: 优先级（low, medium, high）
- `tag`: 标签（模糊匹配）
- `page`, `size`, `sortBy`, `order`: 分页和排序参数

### 5. 搜索任务
**接口**: `GET /api/tasks/search?keyword=文档&page=0&size=10`

**参数**:
- `keyword`: 搜索关键词（匹配标题和描述）
- `page`, `size`: 分页参数

### 6. 更新任务
**接口**: `PUT /api/tasks/{id}`

**请求体**:
```json
{
  "title": "更新后的标题",
  "status": "in_progress",
  "priority": "medium"
}
```

### 7. 删除任务
**接口**: `DELETE /api/tasks/{id}`

**注意**: 如果有其他任务依赖此任务，将无法删除。

### 8. 获取任务依赖树
**接口**: `GET /api/tasks/{id}/dependencies`

**响应**:
```json
{
  "success": true,
  "data": {
    "task": {...},
    "dependencies": [
      {
        "task": {...},
        "dependencies": [...]
      }
    ],
    "dependents": [
      {
        "task": {...},
        "dependents": [...]
      }
    ]
  }
}
```

## 🗄️ 数据库设计

### 主要表结构

#### tasks 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(200) | 任务标题（必填） |
| description | TEXT | 任务描述 |
| status | VARCHAR(20) | 状态枚举 |
| priority | VARCHAR(20) | 优先级枚举 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### task_tags 表
| 字段 | 类型 | 说明 |
|------|------|------|
| task_id | BIGINT | 外键，关联 tasks 表 |
| tag | VARCHAR(255) | 标签内容 |

#### task_dependencies 表
| 字段 | 类型 | 说明 |
|------|------|------|
| task_id | BIGINT | 外键，当前任务 |
| depends_on_task_id | BIGINT | 外键，依赖的任务 |

### 索引优化
- `idx_status`: 状态字段索引
- `idx_priority`: 优先级字段索引
- `idx_created_at`: 创建时间索引

## 🔧 配置说明

### application.properties 关键配置

```properties
# 服务器端口
server.port=8080

# 数据库连接池（HikariCP）
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis 缓存
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=3000ms
```

## 📊 性能优化策略

### 1. 缓存策略
- **缓存内容**: 单个任务详情
- **缓存键**: `task:{id}`
- **过期时间**: 24 小时
- **缓存更新**: 任务更新或删除时自动清除

### 2. 数据库优化
- 关键字段添加索引
- 使用懒加载减少不必要的查询
- 连接池优化（最大 20 个连接）

### 3. 查询优化
- 分页查询避免全表扫描
- 精确匹配优先于模糊匹配
- 复合查询使用专用方法

### 4. 扩展性设计
- 支持水平扩展（无状态服务）
- 读写分离准备（Repository 层抽象）
- 微服务拆分准备（模块化设计）

## 🧪 测试示例

### 使用 cURL 测试

```bash
# 创建任务
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "学习 Spring Boot",
    "description": "深入学习 Spring Boot 框架",
    "status": "pending",
    "priority": "high",
    "tags": ["学习", "技术"]
  }'

# 查询所有任务
curl http://localhost:8080/api/tasks?page=0&size=10

# 筛选高优先级待处理任务
curl "http://localhost:8080/api/tasks/filter?status=pending&priority=high"

# 搜索任务
curl "http://localhost:8080/api/tasks/search?keyword=Spring"

# 更新任务状态
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"status": "in_progress"}'

# 删除任务
curl -X DELETE http://localhost:8080/api/tasks/1
```

## 🐛 异常处理

系统提供完善的异常处理机制：

| 异常类型 | HTTP 状态码 | 说明 |
|---------|------------|------|
| TaskNotFoundException | 404 | 任务不存在 |
| TaskDependencyException | 409 | 依赖冲突 |
| MethodArgumentNotValidException | 400 | 参数验证失败 |
| IllegalArgumentException | 400 | 非法参数 |
| Exception | 500 | 服务器内部错误 |

## 📈 系统架构说明

### 分层架构
```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← RESTful API 接口
├─────────────────────────────────────┤
│          Service Interface          │  ← 服务接口定义（面向接口编程）
├─────────────────────────────────────┤
│       Service Implementation        │  ← 业务逻辑、缓存管理
├─────────────────────────────────────┤
│       Repository Layer              │  ← 数据访问、查询优化
├─────────────────────────────────────┤
│      Database & Cache Layer         │  ← MySQL + Redis
└─────────────────────────────────────┘
```

### 十万级数据扩容方案

1. **数据库层面**
   - 垂直分表：大字段（description）单独存储
   - 水平分库：按用户 ID 或时间范围分片
   - 读写分离：主库写，从库读

2. **缓存层面**
   - Redis 集群部署
   - 多级缓存（本地缓存 + 分布式缓存）
   - 缓存预热策略

3. **应用层面**
   - 负载均衡（Nginx）
   - 服务集群部署
   - 异步处理（消息队列）

4. **监控与优化**
   - 慢查询日志分析
   - 缓存命中率监控
   - 接口响应时间监控

## 📝 Git 提交规范

```bash
# 功能开发
git commit -m "feat: 添加任务筛选功能"

# Bug 修复
git commit -m "fix: 修复任务依赖检查逻辑"

# 文档更新
git commit -m "docs: 更新 API 文档"

# 代码重构
git commit -m "refactor: 优化任务查询性能"

# 测试相关
git commit -m "test: 添加任务创建单元测试"
```

### 4. 缓存策略
- 缓存单个任务详情（`task:{id}`）
- 24 小时过期时间
- 更新/删除时自动清除缓存
- 平衡了性能和数据一致性

### 5. 异常处理
使用全局异常处理器统一处理：
- `TaskNotFoundException` (404)
- `TaskDependencyException` (409)
- 参数验证异常 (400)

## Challenges & Solutions

### Challenge 1: 任务依赖循环检测
**Problem:** 用户可能创建循环依赖（A→B→C→A）

**Solution:**
- 在构建依赖树时使用 `visited` 集合记录已访问节点
- 检测到循环时跳过该节点，避免无限递归
- 删除任务时检查是否有其他任务依赖，防止破坏依赖关系

### Challenge 2: 依赖约束验证
**Problem:** 完成任务时必须确保所有依赖任务已完成

**Solution:**
- 在更新任务状态时检查依赖约束
- 如果存在未完成的依赖任务，抛出 `TaskDependencyException`
- 提供清晰的错误信息，列出未完成的依赖任务 ID

### Challenge 3: 多条件组合筛选
**Problem:** 需要支持状态、优先级、标签的任意组合筛选

**Solution:**
- 在 Repository 中定义多个查询方法
- 根据参数组合动态选择对应的查询方法
- 使用 Spring Data JPA 的方法命名规则简化查询实现

### Challenge 4: 性能优化
**Problem:** 大量任务数据时查询性能下降

**Solution:**
- 添加 Redis 缓存层，减少数据库查询
- 为常用查询字段添加数据库索引
- 使用分页查询避免全表扫描
- 配置 HikariCP 连接池优化数据库连接

## Future Improvements

### 短期改进
- [ ] 添加单元测试和集成测试
- [ ] 实现用户认证和授权（JWT）
- [ ] 添加任务截止时间字段和提醒功能
- [ ] 支持任务分类/项目组

### 中期改进
- [ ] 实现 WebSocket 实时通知
- [ ] 添加任务统计和报表功能
- [ ] 支持任务附件上传
- [ ] 实现任务评论/讨论功能

### 长期改进
- [ ] 微服务架构拆分（用户服务、任务服务、通知服务）
- [ ] 消息队列异步处理（RabbitMQ/Kafka）
- [ ] Elasticsearch 全文搜索引擎
- [ ] 水平扩展支持（数据库分片、读写分离）
- [ ] Docker 容器化部署
- [ ] CI/CD 自动化部署流程

## Time Spent
Approximately 8 hours

---

**Note:** 使用前请确保 MySQL 和 Redis 服务已正常启动。
