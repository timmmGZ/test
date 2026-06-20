# CloudBase 部署指南

本文档详细说明如何将 GymCN 后端部署到腾讯云 CloudBase。

## 目录

- [前提条件](#前提条件)
- [步骤1：创建 CloudBase 环境](#步骤1创建-cloudbase-环境)
- [步骤2：创建云 MySQL 数据库](#步骤2创建云-mysql-数据库)
- [步骤3：初始化数据库](#步骤3初始化数据库)
- [步骤4：本地打包](#步骤4本地打包)
- [步骤5：部署云函数](#步骤5部署云函数)
- [步骤6：配置 Android 前端](#步骤6配置-android-前端)
- [步骤7：测试 API](#步骤7测试-api)
- [步骤8：GitHub Actions 自动部署](#步骤8github-actions-自动部署)

---

## 前提条件

1. 腾讯云账号
2. CloudBase 控制台访问权限
3. Maven 3.6+
4. JDK 17

---

## 步骤1：创建 CloudBase 环境

1. 登录 [腾讯云 CloudBase 控制台](https://console.cloud.tencent.com/tcb)
2. 点击「创建环境」
3. 选择套餐：
   - **免费版**：适合 MVP 阶段
   - **按量付费**：用户量增加后切换
4. 选择地域：**广州**（离你最近）
5. 点击「立即开通」

---

## 步骤2：创建云 PostgreSQL 数据库

1. 在 CloudBase 控制台，点击「云数据库」→「PostgreSQL」
2. 点击「创建数据库」
3. 配置：
   - **数据库版本**：PostgreSQL 14
   - **实例规格**：学习版（免费）
   - **存储空间**：20GB
   - **地域**：广州
4. 点击「立即购买」
5. 创建完成后，记录以下信息：
   - 主机地址（内网地址）
   - 端口（默认 5432）
   - 数据库名：gymcn
   - 用户名（通常是 postgres）
   - 密码

---

## 步骤3：初始化数据库（CloudBase CLI 自动）

CloudBase CLI v3.4+ 支持 `tcb db execute` 命令，**GitHub Actions 会自动完成数据库初始化**。

### 手动初始化（可选，仅首次）

如果你想在部署前先手动测试，可以本地执行：

```bash
# 安装 CloudBase CLI
npm install -g @cloudbase/cli@latest

# 登录
tcb login

# 执行 SQL 初始化
tcb db execute -e <envId> --sql "CREATE TABLE IF NOT EXISTS users (...);"
```

### GitHub Actions 自动初始化

**无需手动操作！** 每次 push 时：

1. 检查 `venues` 表是否存在
2. 不存在 → 自动执行建表 SQL + 插入示例数据
3. 已存在 → 跳过初始化

---

## 步骤4：本地打包

1. 进入后端目录：
   ```bash
   cd GymCN/backend
   ```

2. 打包：
   ```bash
   mvn clean package -DskipTests
   ```

3. 打包成功后，jar 文件位置：
   ```
   backend/target/gymcn-backend.jar
   ```

---

## 步骤5：部署云函数

### 方式一：手动上传（推荐新手）

1. 在 CloudBase 控制台，点击「云函数」→「新建云函数」
2. 配置：
   - **函数名称**：gymcn-backend
   - **运行环境**：Java 17
   - **上传方式**：上传 ZIP/JAR 包
   - **执行方法**：com.gymcn.GymcnApplication::main
3. 上传 `backend/target/gymcn-backend.jar`
4. **配置环境变量**：
   ```
   POSTGRES_HOST=<你的数据库主机地址>
   POSTGRES_PORT=5432
   POSTGRES_DATABASE=gymcn
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=<你的数据库密码>
   ```
5. **配置触发器**：
   - 触发方式：HTTP 触发
   - 认证方式：未认证（暂时）
6. 点击「完成」

### 方式二：CloudBase CLI（推荐）

1. 安装 CloudBase CLI：
   ```bash
   npm install -g @cloudbase/cli
   ```

2. 登录：
   ```bash
   tcb login
   ```

3. 创建部署配置 `cloudbaserc.json`：
   ```json
   {
     "envId": "your-env-id",
     "functionRoot": "./",
     "functions": [
       {
         "name": "gymcn-backend",
         "timeout": 60,
         "memorySize": 256,
         "handler": "com.gymcn.GymcnApplication::main",
         "runtime": "Java17"
       }
     ]
   }
   ```

4. 部署：
   ```bash
   cloudbase functions:deploy gymcn-backend
   ```

---

## 步骤6：配置 Android 前端

1. 获取云函数 HTTP 访问地址：
   - 在 CloudBase 控制台，云函数详情页
   - 找到「访问路径」，格式如：
     ```
     https://your-env-id.service.cloudbase.cn/api
     ```

2. 修改 Android 文件：
   - 打开 `frontend/android/app/src/main/java/com/gymcn/network/NetworkModule.kt`
   - 修改 BASE_URL：
     ```kotlin
     private const val BASE_URL = "https://your-env-id.service.cloudbase.cn/api/"
     ```

3. 重新编译安装到手机

---

## 步骤7：测试 API

使用 curl 或 Postman 测试：

```bash
# 测试发送验证码
curl -X POST https://your-env-id.service.cloudbase.cn/api/auth/sms/send \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000"}'

# 测试登录（任意6位数字验证码都可以）
curl -X POST https://your-env-id.service.cloudbase.cn/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'

# 获取场馆列表
curl https://your-env-id.service.cloudbase.cn/api/venues
```

---

## 步骤8：GitHub Actions 自动部署

配置完成后，每次 push 到 main 分支会自动部署。

### 8.1 获取腾讯云密钥

1. 登录 [腾讯云控制台](https://console.cloud.tencent.com/)
2. 进入「访问管理」→「API 密钥管理」
3. 点击「新建密钥」，记录：
   - `SecretId`
   - `SecretKey`

### 8.2 获取 CloudBase 环境 ID

1. 进入 CloudBase 控制台
2. 在「概览」页面找到「环境 ID」

### 8.3 配置 GitHub Secrets

1. 进入你的 GitHub 仓库
2. 点击「Settings」→「Secrets and variables」→「Actions」
3. 点击「New repository secret」添加以下 secrets：

| Secret 名称 | 值 |
|------------|----|
| `CLOUDBASE_SECRET_ID` | 腾讯云 API SecretId |
| `CLOUDBASE_SECRET_KEY` | 腾讯云 API SecretKey |
| `CLOUDBASE_ENV_ID` | CloudBase 环境 ID |
| `POSTGRES_HOST` | PostgreSQL 主机地址 |
| `POSTGRES_PORT` | PostgreSQL 端口（默认 5432） |
| `POSTGRES_DATABASE` | 数据库名（gymcn） |
| `POSTGRES_USER` | 数据库用户名（postgres） |
| `POSTGRES_PASSWORD` | 数据库密码 |

### 8.4 测试自动部署

1. 修改后端代码（如修改注释）
2. 提交并 push 到 main 分支：
   ```bash
   git add .
   git commit -m "test: trigger deployment"
   git push origin main
   ```

3. 在 GitHub 仓库点击「Actions」查看部署进度

### 8.5 部署成功后的效果

每次 push 代码后：
- GitHub Actions 自动编译项目
- 自动部署到 CloudBase 云函数
- 自动配置环境变量

---

## 常见问题

### Q1：云函数启动失败怎么办？

检查日志：
1. 在 CloudBase 控制台点击云函数
2. 点击「日志」查看错误信息
3. 常见问题：
   - 数据库连接失败 → 检查环境变量
   - 端口被占用 → 修改 application.yml 中的 port

### Q2：冷启动太慢？

Spring Boot 冷启动需要 20-40 秒。可以：
1. 开启「预启动」功能（CloudBase 付费功能）
2. 或使用 Spring Native 优化

### Q3：如何更新代码？

**手动方式：**
1. 修改代码
2. 重新打包 `mvn clean package -DskipTests`
3. 在 CloudBase 控制台重新上传 jar
4. 或使用 CLI：`cloudbase functions:deploy gymcn-backend`

**自动方式：**
1. 修改代码后 push 到 GitHub
2. GitHub Actions 自动完成部署

---

## 下一步

部署完成后，可以开始：
1. 测试完整登录流程
2. 添加更多 API（场馆详情、入场记录等）
3. 实现支付功能
