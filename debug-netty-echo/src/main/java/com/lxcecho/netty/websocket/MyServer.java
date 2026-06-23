package com.lxcecho.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Netty 通过 websocket 百年城实现服务器和客户端长连接
 * - 实现基于 websocket 的长连接的全双工交互；
 * - 改变 http 协议多次请求的约束，实现长连接了，服务器可以发送消息给浏览器；
 * - 客户端浏览器和服务器会相互感知，比如故武器关闭了，浏览器会感知，同样浏览器关闭了，服务器会感知；
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 12.12.2021
 */
public class MyServer {

    private final int port;

    public MyServer(int port) {
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 因为基于 http 协议，使用 http 的编码和解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 以 块 方式写，添加 ChunkedWriteHandler 处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /**
                             * 1.http 数据在传输过程中是分段，HttpObjectAggregator 就是可以将多个段聚合
                             * 2.这就是为什么，当浏览器发送大量数据时，就会发出多次 http 请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            /**
                             * 1.对应 websocket，它的数据是以 帧（frame) 形式传递
                             * 2.可以看到 WebSocketFrame 下面有六个子类
                             * 3.浏览器请求时 ws://localhost:7000/hello 表示请求的 uri
                             * 4.WebSocketServerProtocolHandler 核心功能是将 http 协议升级为 ws 协议，保持长连接
                             * 5.是通过一个 状态码 101
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
//                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello2"));

                            // 自定义的 handler，处理业务逻辑
                            pipeline.addLast(new MyTextWebSocketFrameHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new MyServer(7000).run();
    }

}
