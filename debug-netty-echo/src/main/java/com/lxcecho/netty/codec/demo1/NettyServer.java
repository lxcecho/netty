package com.lxcecho.netty.codec.demo1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
    public static void main(String[] args) throws Exception {

        // 创建 BossGroup 和 WorkerGroup
        // 说明
        // 1. 创建两个线程组 bossGroup 和 workerGroup
        // 2. bossGroup 只是处理连接请求，真正的和客户端业务处理，会交给 workerGroup 完成
        // 3. 两个都是无限循环
        // 4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数
        // 默认实际 cpu核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 8*2


        // 连接数优化参考 https://blog.csdn.net/qq_33458621/article/details/98775406
        try {
            // 创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 使用链式编程来进行设置
            bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用 NioServerSocketChannel 作为服务器的通道实现
                    // socket参数参考 https://www.jianshu.com/p/0bff7c020af2
                    // Socket参数，服务端接受连接的队列长度(accept)，如果队列已满，客户端连接将被拒绝。默认值，Windows为200，其他为128。
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                    // 地址可以复用,进程重启端口被占用问题
                    .option(ChannelOption.SO_REUSEADDR, true)
                    // 客户端创建连接时绑定基本属性
                    .childAttr(AttributeKey.newInstance("source"), "appliance")
                    // 连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    // 立即发送数据(不缓存提高响应速度)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    // Netty参数，ByteBuf的分配器，默认值为ByteBufAllocator.DEFAULT，4.0版本为UnpooledByteBufAllocator，
                    // 4.1版本为PooledByteBufAllocator。该值也可以使用系统参数io.netty.allocator.type配置，使用字符串值："unpooled"，"pooled"。
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                    .handler(null) // 该 handler 对应 bossGroup , childHandler 对应 workerGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道初始化对象(匿名对象)
                        // 给 pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 在 pipeline 加入 ProtoBufDecoder
                            // 指定对哪种对象进行解码
                            pipeline.addLast("decoder", new ProtobufDecoder(StudentPOJO.Student.getDefaultInstance()));
                            pipeline.addLast(new NettyServerHandler());
                        }
                    }); // 给我们的 workerGroup 的 EventLoop 对应的管道设置处理器

            log.info(".....服务器 is ready...");

            // 绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
            // 启动服务器(并绑定端口)
            ChannelFuture cf = bootstrap.bind(6668).sync();

            // 给 cf 注册监听器，监控我们关心的事件
            cf.addListener((ChannelFutureListener) future -> {
                if (cf.isSuccess()) {
                    log.info("监听端口 6668 成功");
                } else {
                    log.info("监听端口 6668 失败");
                }
            });

            // 对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            // 优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}