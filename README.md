# Demo Project

## 项目结构

```
project/
├── README.md
├── pom.xml
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java    # 主启动类
│   │   │   ├── models/                 # 数据模型
│   │   │   ├── controllers/            # 控制器
│   │   │   ├── services/               # 业务逻辑层
│   │   │   ├── routes/                 # 路由配置
│   │   │   └── utils/                  # 工具类
│   │   └── resources/
│   │       └── application.properties  # 配置文件
│   └── test/                           # 测试代码
├── tests/                              # 额外测试目录（可选）
└── docs/                               # 文档目录（可选）
```

## 技术栈

- Java 17
- Spring Boot 4.0.5
- Maven

## 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+

### 运行项目

```bash
mvn spring-boot:run
```

### 构建项目

```bash
mvn clean package
```

### 运行测试

```bash
mvn test
```

## 模块说明

- **models**: 存放实体类和数据模型
- **controllers**: 处理HTTP请求的控制器
- **services**: 业务逻辑实现
- **routes**: 路由配置和接口定义
- **utils**: 通用工具类和辅助方法

## 开发规范

1. 遵循Java命名规范
2. 使用适当的注释
3. 编写单元测试
4. 保持代码简洁清晰
