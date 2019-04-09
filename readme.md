# Chat-netty Project

### 设计初衷: 

参阅《精通网络编程详解》孙卫琴老师的书籍, 了解并使用了JDK原生的io, nio以及aio类库的基本API来开发socket服务端和客户端.

参阅《Netty in action》和闪电侠老师推出的博客, 受益良多.

由于ByteBuffer 编解码的开发复杂性(拆包/粘包, Netty内置提供一系列的拆包器, 比如固定长度的拆包器,行拆包器,分隔符拆包器,基于长度域拆包器)以及对业务逻辑的责任链设计), 基于Netty的开发难度就小了不少.

在熟悉使用的前提下, 再深入理解源码解析.

chat-netty 可以说是个很不错的入门作品.

### 功能说明:

1.  客户端登录/注册/注销, 其登录状态为(在线, 离线, 隐身)

2.  客户端单聊, 这里考虑到离线信息需要漫游, 保存在非关系型数据库(MONGODB/PGSQL)

3.  客户端群聊

4.  异地登录通知, 好友申请通知, 离线信息(单聊/群聊)通知

5.  添加/删除 - 好友/群聊

6.  考虑到分布式IM的扩展, 需要引入负载均衡做心跳侦测和redis作为外存储用户id与对应内网服务端的映射关系.

    这里应该是socket长连接, nginx不能考虑作为使用. 该扩展功能, 之后考虑..

