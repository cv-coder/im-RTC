# GitHub Actions 自动部署配置说明

## 配置步骤

### 1. 在 GitHub 仓库中配置 Secrets

进入仓库的 `Settings` → `Secrets and variables` → `Actions`，添加以下 secrets：

| Secret 名称 | 说明 | 示例值 |
|------------|------|--------|
| `SSH_PRIVATE_KEY` | 服务器 SSH 私钥 | -----BEGIN OPENSSH PRIVATE KEY----- ... |
| `SERVER_USER` | 服务器登录用户名 | root 或 ubuntu |
| `SERVER_IP` | 服务器 IP 地址 | 192.168.1.100 |

### 2. 生成 SSH 密钥对（如果还没有）

在本地执行：

```bash
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions_key
```

- 将 **私钥** (`github_actions_key`) 的内容复制到 `SSH_PRIVATE_KEY`
- 将 **公钥** (`github_actions_key.pub`) 的内容添加到服务器的 `~/.ssh/authorized_keys`

### 3. 服务器准备

#### 安装依赖

确保服务器已安装：

```bash
# 安装 JDK 17
sudo apt update
sudo apt install openjdk-17-jdk -y

# 验证安装
java -version
```

#### 配置生产环境配置文件

在服务器上创建并编辑配置文件：

```bash
# im-platform 配置
sudo vim /opt/box-im/config/application-platform.yml

# im-server 配置
sudo vim /opt/box-im/config/application-server.yml
```

**重要配置项：**
- 数据库连接信息
- Redis 连接信息
- JWT secret（两个服务必须一致）
- MinIO 配置（如果使用）

### 4. 配置防火墙

开放必要的端口：

```bash
# im-platform HTTP 端口（默认 8888）
sudo ufw allow 8888/tcp

# im-server WebSocket 端口（默认 8878）
sudo ufw allow 8878/tcp

# 或者使用 firewalld
sudo firewall-cmd --permanent --add-port=8888/tcp
sudo firewall-cmd --permanent --add-port=8878/tcp
sudo firewall-cmd --reload
```

### 5. 触发部署

当你推送代码到 `master` 分支时，GitHub Actions 会自动：

1. 检出代码
2. 使用 Maven 构建项目
3. 将 JAR 包部署到服务器
4. 重启服务

也可以在 GitHub 仓库的 `Actions` 标签页手动触发。

## 部署路径说明

服务器上的目录结构：

```
/opt/box-im/
├── im-platform/
│   └── im-platform.jar          # 业务平台服务
├── im-server/
│   └── im-server.jar            # 消息推送服务
├── config/
│   ├── application-platform.yml # 平台配置文件
│   └── application-server.yml   # 推送服务配置文件
└── logs/
    ├── im-platform.log          # 平台日志
    └── im-server.log            # 推送服务日志
```

## 查看服务状态

登录服务器后：

```bash
# 查看进程
ps aux | grep 'im-.*.jar'

# 查看日志
tail -f /opt/box-im/logs/im-platform.log
tail -f /opt/box-im/logs/im-server.log

# 停止服务
pkill -f 'im-platform.jar'
pkill -f 'im-server.jar'

# 手动启动
nohup java -jar /opt/box-im/im-platform/im-platform.jar \
  --spring.config.location=/opt/box-im/config/application-platform.yml \
  > /opt/box-im/logs/im-platform.log 2>&1 &

nohup java -jar /opt/box-im/im-server/im-server.jar \
  --spring.config.location=/opt/box-im/config/application-server.yml \
  > /opt/box-im/logs/im-server.log 2>&1 &
```

## 进阶配置

### 使用 systemd 管理服务（推荐）

创建服务文件：

```bash
# im-platform 服务
sudo vim /etc/systemd/system/im-platform.service
```

内容：

```ini
[Unit]
Description=Box-IM Platform Service
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/box-im/im-platform
ExecStart=/usr/bin/java -jar /opt/box-im/im-platform/im-platform.jar --spring.config.location=/opt/box-im/config/application-platform.yml
Restart=on-failure
RestartSec=10
StandardOutput=append:/opt/box-im/logs/im-platform.log
StandardError=append:/opt/box-im/logs/im-platform.log

[Install]
WantedBy=multi-user.target
```

同样创建 `im-server.service`，然后：

```bash
# 重载 systemd
sudo systemctl daemon-reload

# 启用开机自启
sudo systemctl enable im-platform
sudo systemctl enable im-server

# 启动服务
sudo systemctl start im-platform
sudo systemctl start im-server

# 查看状态
sudo systemctl status im-platform
sudo systemctl status im-server
```

修改工作流中的重启命令：

```yaml
# 重启服务
- name: Restart services
  run: |
    ssh -o StrictHostKeyChecking=no ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_IP }} << 'EOF'
      sudo systemctl restart im-platform
      sleep 3
      sudo systemctl restart im-server
    EOF
```

## 故障排查

### 常见问题

1. **SSH 连接失败**
   - 检查 `SSH_PRIVATE_KEY` 是否正确配置
   - 确认公钥已添加到服务器 `authorized_keys`
   - 检查服务器 SSH 端口是否开放

2. **服务启动失败**
   - 查看日志文件：`tail -f /opt/box-im/logs/*.log`
   - 检查配置文件是否正确
   - 确认数据库/Redis 连接正常

3. **端口占用**
   - 检查端口占用：`netstat -tulnp | grep 8888`
   - 更换端口或停止冲突进程

4. **内存不足**
   - 限制 JVM 内存：`java -Xmx512m -jar ...`
   - 考虑升级服务器配置
