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
npm install
npm run serve
```
访问 http://localhost:8080

4.启动uniapp-h5
将im-uniapp目录导入HBuilderX,点击菜单"运行"->"开发环境-h5"
访问 http://localhost:5173

#### 接入消息推送
对消息推送模块进行了剥离和封装

#### 界面截图
私聊：
![输入图片说明](%E6%88%AA%E5%9B%BE/web/%E7%A7%81%E8%81%8A.jpg)

群聊：
![输入图片说明](%E6%88%AA%E5%9B%BE/web/%E7%BE%A4%E8%81%8A.jpg)

群通话：
![输入图片说明](%E6%88%AA%E5%9B%BE/web/%E5%A4%9A%E4%BA%BA%E9%80%9A%E8%AF%9D.jpg)

好友列表：
![输入图片说明](%E6%88%AA%E5%9B%BE/web/%E5%A5%BD%E5%8F%8B.jpg)

群列表：
![输入图片说明](%E6%88%AA%E5%9B%BE/web/%E7%BE%A4%E5%88%97%E8%A1%A8.jpg)

移动端APP:
![输入图片说明](%E6%88%AA%E5%9B%BE/app/1.png)  
  
