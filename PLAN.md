### Box-IM 微服务拆分服务清单（基于当前仓库）

#### Summary
- 当前后端已是“2服务形态”：`im-platform`（业务单体）+ `im-server`（实时网关）。
- 结合现有接口、数据表和消息链路，建议目标拆分为 **8 个服务**，先落地 5 个核心服务，再逐步细化。
- 依据代码位置：[im-platform controllers](D:/Users/lwf/Desktop/java/box-im-master/im-platform/src/main/java/com/lwf/implatform/controller)、[im-server netty processors](D:/Users/lwf/Desktop/java/box-im-master/im-server/src/main/java/com/lwf/imserver/netty/processor)、[db schema](D:/Users/lwf/Desktop/java/box-im-master/db/im-platform.sql)。

#### Key Changes
1. `im-gateway-realtime`（保留并强化现有 `im-server`）：WebSocket/TCP 连接、心跳、在线状态、消息下发、下发结果回传。
2. `im-bff-api`（新增）：对外统一 HTTP 入口，鉴权、路由、聚合，前端路径保持不变。
3. `im-user-auth-service`：注册/登录/刷新 token/资料维护（`im_user`）。
4. `im-social-service`：好友、群组、群成员关系（`im_friend`、`im_group`、`im_group_member`）。
5. `im-message-service`：私聊/群聊消息、撤回、历史、离线拉取、已读回执（`im_private_message`、`im_group_message`）。
6. `im-media-service`：文件图片上传、MinIO 管理、过期清理（`im_file_info`）。
7. `im-rtc-signaling-service`：单聊 RTC 信令与会话状态（后续承接群 RTC）。
8. `im-moderation-ops-service`：敏感词与封禁/解封事件处理（`im_sensitive_word` + ban/unban 队列）。

#### Public API / Interface Changes
- 对前端默认保持现有 REST 路径不变，由 `im-bff-api` 透传，降低改造成本。
- 新增内部服务接口能力：
1. `social`：`isFriend`、`isInGroup`、`listGroupMembers`
2. `realtime`：`isOnline`、`getOnlineTerminal`
3. `message`：`appendMessage`、`markRead`、`pullOffline`
- 新增领域事件约定（先可沿用 Redis 队列）：`friend.changed`、`group.member.changed`、`message.sent`、`user.banned`、`group.banned`。

#### Test Plan
1. 登录后 WS 建链与心跳续期正常，在线状态查询准确。
2. 私聊链路完整：加好友 -> 发消息 -> 回执更新 -> 历史查询一致。
3. 群聊链路完整：入群/踢人 -> 消息下发 -> 已读人数统计正确。
4. 文件链路完整：上传、去重复用、过期清理正常。
5. 风控链路完整：敏感词过滤生效，用户/群封禁实时通知到在线端。

#### Assumptions
- 第一阶段默认“先逻辑拆分、后物理拆库”：可先共库分表，稳定后再一服务一库。
- 第一阶段默认继续使用 Redis 作为消息队列与状态存储，先不引入 Kafka/RabbitMQ。
- 前端兼容优先，先不改协议。
- 群 RTC 作为二阶段能力（前端已有 `/webrtc/group/*` 调用，后端尚未完整落地）。
