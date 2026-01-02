package io.netty.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 08.10.2021
 */
@Slf4j
public class NettyHttpServer {
    static final int PORT = 8888;

    public static void main(String[] args) {
        /**
         * 事件循环组，就是死循环
         */
        // 仅仅接收连接，转给 workerGroup，自己不做处理
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 真正处理
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(4, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerWorkerThread_" + threadIndex.incrementAndGet());
            }
        });

        try {
            // 很轻松的启动服务端代码
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // childHandler 子处理器，传入一个初始化器参数 TestServerInitializer（自定义）
            // TestServerInitializer 在 Channel 被注册时，就会创建调用
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    /*.handler(null) */ // 该 handler 对应 bossGroup，childHandler 对应 workerGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 向管道加入处理器
                            // 得到管道
                            ChannelPipeline pipeline = ch.pipeline();

                            // 加入一个 netty 提供的 httpServerCodec codec ==> [coder - decoder]
                            // HttpServerCodec 说明：
                            // 1 HttpServerCodec 是 Netty 提供的处理 Http 的编解码器
                            pipeline.addLast(
                                    defaultEventExecutorGroup,
                                    new HttpServerCodec(),
                                    new HttpObjectAggregator(1024 * 1024),
                                    new HttpServerExpectContinueHandler(),
                                    // 2 增加一个自定义的 handler
                                    new NettyHttpServerHandler());
                        }
                    });
            // 绑定一个端口并且同步，生成一个 ChannelFuture 对象
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            log.info("Netty http server listening on port {}", PORT);
            // 对关闭的监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 循环组优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
