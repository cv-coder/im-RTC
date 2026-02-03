#### 项目结构
| 模块          | 功能                               |
|-------------|----------------------------------|
| im-platform | 业务平台服务，负责处理来自用户的业务请求(http)       |
| im-server   | 消息推送服务，不依赖业务,负责将消息推送给用户(ws)      |
| im-client   | 消息推送sdk, 其他服务可集成此sdk与im-server通信 |
| im-common   | 公共包,后端服务均依赖此包                    |
| im-web      | web页面                            |
| im-uniapp   | uniapp页面,可打包成app、h5、微信小程序        |

#### 消息推送方案
当消息的发送者和接收者连的不是同一个server时，消息是无法直接推送的，设计出了能够支持跨节点推送的方案：
![输入图片说明](%E6%88%AA%E5%9B%BE/%E6%B6%88%E6%81%AF%E6%8E%A8%E9%80%81%E9%9B%86%E7%BE%A4%E5%8C%96.jpg)

- 利用了redis的list数据实现消息推送，其中key为im:unread:${serverid},每个key的数据可以看做一个queue,每个im-server根据自身的id只消费属于自己的queue
- redis记录了每个用户的websocket连接的是哪个im-server,当用户发送消息时，im-platform将根据所连接的im-server的id,决定将消息推向哪个queue


#### 本地启动
1.安装运行环境
- 安装node:v18.19.0
- 安装jdk:17
- 安装maven:3.9.6
- 安装mysql:8.0,账号密码分别为root/root,创建名为im_platform的数据库，运行db/im_platfrom.sql脚本
- 安装redis:6.2
- 安装minio:RELEASE.2024-xx,使用默认账号、密码、端口

2.启动后端服务
```
mvn clean package
java -jar ./im-platform/target/im-platform.jar
java -jar ./im-server/target/im-server.jar
```

3.启动前端web
```
cd im-web
## **项目概览**

这是一个即时通讯（IM）参考实现，包含业务平台、消息推送服务、客户端 SDK 以及前端示例：

- **im-platform**：业务平台服务，处理 HTTP 接口与业务逻辑（登录、关系、消息入库等）。
- **im-server**：消息推送服务（基于 WebSocket/TCP），负责将消息下发到在线用户。推送与平台解耦，可以水平扩容。
- **im-client**：Java 客户端/SDK（可集成到其他服务用于与 im-server 通信）。
- **im-common**：公共库，后端模块的公共代码与工具。
- **im-web**：Web 前端示例。
- **im-uniapp**：移动端 uniapp 示例，可构建为 App/H5/小程序。

更多模块目录与源码请查看仓库对应路径。

## **设计要点**

- 消息队列使用 Redis list 实现跨节点消息分发：每个 im-server 维护并消费属于自身的 Redis queue（例如 `im:unread:${serverId}`）。
- im-platform 记录用户与 im-server 的绑定，当发送消息时将消息推入目标 im-server 对应的队列，由目标节点拉取并下发。
- JWT 用于鉴权，`im-platform` 与 `im-server` 使用一致的 secret（见配置）。

## **环境与依赖**

- JDK 17
- Maven 3.6+（或 3.9.x）
- MySQL 8.0（示例使用数据库 schema 文件在仓库 `db` 目录）
- Redis 6.x
- Node.js v18+（用于前端构建）
- 可选：MinIO（用于文件/头像存储，参考 `im-platform` 的 MinIO 配置）

数据库脚本：请查看 [db/im-platform.sql](db/im-platform.sql#L1)

## **配置（关键文件）**

- `im-platform` 配置：[im-platform/src/main/resources/application.yml](im-platform/src/main/resources/application.yml#L1)（默认 HTTP 端口 `8888`）。
- `im-server` 配置：[im-server/src/main/resources/application.yml](im-server/src/main/resources/application.yml#L1)（默认 WebSocket 端口 `8878`，TCP 可选）。

注意：`im-platform` 与 `im-server` 的 JWT `accessToken.secret` 必须保持一致，以保证 token 在两者间可验证。

## **快速启动（本地开发）**

1. 准备数据库与配置

- 启动 MySQL，并执行 `db/im-platform.sql` 创建所需表与初始数据。

2. 后端构建并运行

```bash
mvn -T 1C -f pom.xml clean package -DskipTests
# 在 IDE 中直接运行主类也可：im-platform/src/main/java/.../IMPlatformApp.java
# 或使用 jar 运行（jar 路径以实际构建产物为准）
java -jar im-platform/target/im-platform.jar
java -jar im-server/target/im-server.jar
```

或使用 IDE 分别运行：

- 运行 `com.bx.implatform.IMPlatformApp`（`im-platform`）
- 运行 `com.bx.imserver.IMServerApp`（`im-server`）

3. 前端运行

- Web 示例：

```bash
cd im-web
npm install
npm run serve
# 默认访问 http://localhost:8080
```

- UniApp（H5）：将 `im-uniapp` 导入 HBuilderX，选择 H5 运行或使用本地 vite（若已配置）。默认 dev 地址示例：`http://localhost:5173`。

4. 使用 Docker Compose（可选）

仓库根目录提供 `docker-compose.yml`，可以用于启动依赖服务（MySQL/Redis/MinIO）和镜像（若已构建）。

```bash
docker-compose up -d
```

## **常见操作与说明**

- 日志位置：各模块遵循 Spring Boot 日志配置（`logback.xml`）。
- 静态资源与截图位于仓库 `截图/` 目录，用于文档展示。
- 若需要切换环境（dev/test/prod），请修改 `spring.profiles.active` 或使用 `--spring.profiles.active=prod` 启动参数，配置文件位于对应模块的 `src/main/resources` 下。

## **贡献与许可**

- 欢迎提交 Issue 与 PR。请遵循仓库代码风格与模块边界。
- 项目采用仓库根目录的 LICENSE（请查看 LICENSE 文件）。

----


