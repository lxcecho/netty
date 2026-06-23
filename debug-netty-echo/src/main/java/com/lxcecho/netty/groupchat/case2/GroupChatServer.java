package com.lxcecho.netty.groupchat.case2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 11.12.2021
 */
@Slf4j
public class GroupChatServer {

    /**
     * 监听端口
     */
    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    /**
     * 编写 run 方法，处理客户端的请求
     */
    public void run() {
        // 创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 8 个 NioEventLoop

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // get pipeline
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // Add a decoder to pipeline
                            pipeline.addLast("decoder", new StringDecoder());
                            // Add a encoder to pipeline
                            pipeline.addLast("encoder", new StringEncoder());
                            // Add custom business handler
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });

            log.info("Netty 服务器启动");

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GroupChatServer(7000).run();
    }

}
