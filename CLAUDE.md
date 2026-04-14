# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**box-im** 是一个基于 Spring Boot + Netty + WebSocket 的分布式即时通讯系统，支持单聊、群聊、WebRTC 音视频通话。采用前后端分离架构，提供 Web 端（Vue）和移动端（UniApp）。

## 命令速查

### 后端（Maven 多模块）

```bash
# 编译所有模块（跳过测试）
mvn clean package -DskipTests

# 启动业务平台（端口 8888）
java -jar im-platform/target/im-platform.jar

# 启动消息推送服务（WebSocket 端口 8878）
java -jar im-server/target/im-server.jar

# 在 IDE 中直接运行
# im-platform: com.lwf.implatform.IMPlatformApp
# im-server:   com.lwf.imserver.IMServerApp
```

### Web 前端（Vue 2）

```bash
cd im-web
npm install
npm run serve    # 开发（localhost:8080）
npm run build    # 生产构建
npm run lint     # lint
```

### UniApp 移动端

使用 HBuilderX 导入 `im-uniapp` 目录后运行。打包前修改 `im-uniapp/.env.js` 中的 `ENV` 变量（`DEV`/`PROD`）以及对应的后端地址。

### 基础设施

```bash
# 启动 MySQL / Redis / MinIO / coturn（TURN 服务器）
docker-compose up -d
```

## 架构设计

### 模块职责

| 模块 | 职责 |
|---|---|
| `im-platform` | 业务 HTTP 服务：用户、好友、群组、消息存储、文件上传、WebRTC 信令 |
| `im-server` | Netty WebSocket/TCP 实时推送服务，无业务逻辑 |
| `im-client` | Spring Boot Starter，供 im-platform 调用 im-server |
| `im-common` | 共享模型、枚举、Redis MQ 抽象 |
| `im-web` | Vue 2 Web 客户端 |
| `im-uniapp` | UniApp 客户端（App/H5/微信小程序） |

### 消息推送链路（分布式核心）

```
前端 → im-platform (HTTP) → im-client
                                ↓
                    Redis List (im:message:private/group/system)
                                ↓
                    im-server 轮询消费（RedisMQPullTask）
                                ↓
                    Netty Channel → 目标前端 WebSocket
                                ↓
                    发送结果写入 Redis (im:result:*)
                                ↓
                    im-platform 的 MessageListener 消费结果（回执）
```

- Redis Key `im:user:server_id` 记录每个用户连接的 im-server 节点 ID，实现跨节点路由
- 每个 im-server 节点注册唯一 ID（`im:max_server_id` 自增），拥有独立消息队列 `im:unread:${serverId}`
- im-client 的 `IMSender` 根据接收者所在节点将消息 LPUSH 到对应队列

### im-server Netty 层

- `IMChannelHandler`：统一 ChannelHandler，根据 `IMCmdType`（0=登录, 1=心跳, 2=强制下线, 3=私聊, 4=群聊, 5=系统消息）分发到对应 `Processor`
- `UserChannelCtxMap`：维护 `userId → ChannelHandlerContext` 的本地映射
- 同时支持 WebSocket（8878）和 TCP（8879，默认关闭）两种传输，对应 `ws/` 和 `tcp/` 子包

### im-platform 关键模式

- **Session**：JWT 解析后存 `UserSession`（含 userId、terminal 类型）至 ThreadLocal（`SessionContext`）
- **消息发送**：Controller → Service → `IMClient.sendPrivateMessage/sendGroupMessage` → Redis
- **消息回执**：`PrivateMessageListener` / `GroupMessageListener` 实现 `MessageListener` 接口，通过 `MessageListenerMulticaster` 注册，消费 `im:result:*` 队列更新消息状态
- **敏感词过滤**：文本消息发送前调用 `SensitiveFilterUtil.filter()`

### 配置要点

- **JWT secret 必须保持一致**：`im-platform` 和 `im-server` 的 `jwt.accessToken.secret` 必须相同，im-server 用它验证 WebSocket 握手时的 token
- 配置文件分 `dev`/`prod` 环境，通过 `spring.profiles.active` 切换
- im-uniapp 前端地址在 `im-uniapp/.env.js` 中配置，H5 模式走 `/api` 代理

### 默认端口

| 服务 | 端口 |
|---|---|
| im-platform HTTP | 8888 |
| im-server WebSocket | 8878 |
| MySQL | 3306 |
| Redis | 6379 |
| MinIO | 9000 |
| coturn TURN | 3478 |

## 数据库

初始化脚本位于 `db/` 目录：
- `im-platform.sql` — 主表结构
- `敏感词库初始化.sql` — 可选敏感词数据

## API 文档

启动 im-platform 后访问 Swagger（Knife4j）：`http://localhost:8888/doc.html`

## 规划文档

`PLAN.md` 中记录了微服务拆分方案（8 个服务目标），当前仍为单体形态，拆分尚未实施。
