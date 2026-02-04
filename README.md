# 即时通讯系统

一个基于 Spring Boot + WebSocket 的分布式即时通讯系统，支持单聊、群聊、消息推送等功能，前后端分离架构。

## 功能特性

- 支持单聊、群聊消息实时推送
- 支持文本、图片、文件、语音、视频等多种消息类型
- 好友关系管理、群组管理
- 消息已读未读状态
- 分布式部署，支持水平扩展
- JWT 身份认证
- 提供 Web 端和移动端（UniApp）

## 项目结构

| 模块          | 说明                                      |
|-------------|------------------------------------------|
| **im-platform** | 业务平台服务，处理 HTTP 业务逻辑（用户、好友、群组、消息存储等） |
| **im-server**   | WebSocket 消息推送服务，负责实时消息下发             |
| **im-client**   | Java SDK，可集成到其他服务与 im-server 通信         |
| **im-common**   | 公共模块，包含工具类、实体类等                        |
| **im-web**      | Vue Web 前端                              |
| **im-uniapp**   | UniApp 移动端，可编译为 App/H5/小程序             |

## 架构设计

### 消息推送方案

在分布式场景下，发送者和接收者可能连接不同的 im-server 节点，本项目采用 Redis 队列实现跨节点消息推送：

![消息推送架构图](截图/消息推送集群化.jpg)

**核心机制：**

1. 每个 im-server 节点拥有唯一 ID，在 Redis 中维护独立的消息队列 `im:unread:${serverId}`
2. Redis 记录每个用户连接的 im-server 节点 ID
3. im-platform 收到消息后，根据接收者所在节点将消息推入对应队列
4. im-server 消费自己的队列，将消息推送给连接的用户

**优势：** 各节点独立消费，解耦业务与推送，支持水平扩展。

## 快速开始

### 环境要求

- **JDK**: 17
- **Maven**: 3.9.6
- **Node.js**: v18.19.0
- **MySQL**: 8.0
- **Redis**: 6.2+
- **MinIO**: RELEASE.2024-xx（可选，用于文件存储）

### 1. 数据库初始化

创建数据库并执行初始化脚本：

```sql
CREATE DATABASE im_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行以下脚本：
- [db/im-platform.sql](db/im-platform.sql) - 主数据库脚本
- [db/敏感词库初始化.sql](db/敏感词库初始化.sql) - 敏感词库（可选）

### 2. 修改配置

修改配置文件中的数据库、Redis、MinIO 连接信息：

- [im-platform/src/main/resources/application.yml](im-platform/src/main/resources/application.yml)
- [im-server/src/main/resources/application.yml](im-server/src/main/resources/application.yml)

**⚠️ 重要：** 确保 `im-platform` 和 `im-server` 的 JWT `accessToken.secret` 配置一致。

### 3. 启动后端服务

```bash
# 编译打包
mvn clean package -DskipTests

# 启动业务平台
java -jar im-platform/target/im-platform.jar

# 启动消息推送服务
java -jar im-server/target/im-server.jar
```

或在 IDE 中直接运行主类：
- `com.lwf.implatform.IMPlatformApp`
- `com.lwf.imserver.IMServerApp`

### 4. 启动前端

#### Web 端

```bash
cd im-web
npm install
npm run serve
```

访问：http://localhost:8080

#### UniApp 端

1. 使用 HBuilderX 导入 `im-uniapp` 目录
2. 修改 [im-uniapp/common/url.js](im-uniapp/common/url.js) 中的后端地址
3. 运行到浏览器/手机/小程序

### 5. Docker 部署（可选）

```bash
docker-compose up -d
```

查看 [docker-compose.yml](docker-compose.yml) 了解详细配置。

## 默认端口

| 服务           | 端口   | 说明               |
|--------------|------|--------------------|
| im-platform  | 8888 | HTTP 业务接口       |
| im-server    | 8878 | WebSocket 消息推送  |
| MySQL        | 3306 | 数据库              |
| Redis        | 6379 | 缓存/消息队列        |
| MinIO        | 9000 | 文件存储（可选）      |

## 相关文档

- 数据库设计：查看 [db](db) 目录
- API 接口文档：启动后访问 Swagger UI
- 前端项目说明：查看 [im-web/README.md](im-web/README.md)

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

本项目采用 [LICENSE](LICENSE) 协议开源。

---

**项目预览：**

查看 [截图](截图) 目录了解系统界面。


