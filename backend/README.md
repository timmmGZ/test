# GymCN 后端项目

广州健身一卡通（GymCN）后端服务，基于 Spring Boot + MySQL 构建。

## 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot 3.2 | 后端框架 |
| MySQL | 数据库（CloudBase 云 MySQL） |
| MyBatis Plus | ORM 框架 |
| Maven | 项目构建 |

## 项目结构

```
backend/
├── src/main/java/com/gymcn/
│   ├── GymcnApplication.java      # 启动类
│   ├── models/
│   │   ├── entity/                # 数据库实体
│   │   ├── dto/                   # 数据传输对象
│   │   └── mapper/               # MyBatis Mapper
│   ├── api/
│   │   └── controllers/          # REST API 控制器
│   ├── services/                  # 业务逻辑
│   └── utils/                     # 工具类
├── src/main/resources/
│   └── application.yml           # 配置文件
├── database/
│   └── migrations/               # 数据库迁移脚本
└── deployment/
    └── ci/                       # CI/CD 配置
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+（或 CloudBase 云 MySQL）

### 2. 本地运行

```bash
# 克隆项目
git clone https://github.com/your-username/GymCN.git
cd GymCN/backend

# 修改数据库配置（本地开发）
vim src/main/resources/application.yml

# 启动服务
mvn spring-boot:run
```

### 3. API 列表

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/sms/send` | POST | 发送验证码 |
| `/api/auth/login` | POST | 用户登录 |
| `/api/venues` | GET | 获取场馆列表 |
| `/api/venues/{id}` | GET | 获取场馆详情 |

## 部署到 CloudBase

### 1. 配置云数据库

在 CloudBase 控制台创建 MySQL 数据库，获取连接信息。

### 2. 配置环境变量

在 CloudBase 控制台配置以下环境变量：

```
MYSQL_HOST=your-mysql-host
MYSQL_PORT=3306
MYSQL_DATABASE=gymcn
MYSQL_USER=your-username
MYSQL_PASSWORD=your-password
```

### 3. 初始化数据库

执行 `database/migrations/001_init.sql` 脚本。

### 4. 部署云函数

使用 CloudBase CLI 或 GitHub Actions 自动部署。

## 开发指南

### API 响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

- `code: 0` 表示成功
- `code: 1` 表示失败

### 添加新的 API

1. 在 `models/entity/` 添加实体类
2. 在 `models/mapper/` 添加 Mapper 接口
3. 在 `services/` 添加业务逻辑
4. 在 `api/controllers/` 添加 Controller

## License

MIT
