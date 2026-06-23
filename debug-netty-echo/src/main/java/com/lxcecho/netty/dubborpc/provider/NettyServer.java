package com.lxcecho.netty.dubborpc.provider;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import com.lxcecho.netty.dubborpc.customer.ClientBootstrap;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho@gmail.com
 * @since 2026/1/10
 */
@Slf4j
public class NettyServer {

    public static void startServer(String hostName, int port) {
        startServer0(hostName, port);
    }

    /**
     * 编写一个方法，完成对 NettyServer 的初始化和启动
     *
     * @param hostname
     * @param port
     */
    private static void startServer0(String hostname, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      protected void initChannel(SocketChannel ch) throws Exception {
                                          ChannelPipeline pipeline = ch.pipeline();
                                          pipeline.addLast(new StringDecoder());
                                          pipeline.addLast(new StringEncoder());
                                          pipeline.addLast(new NettyServerHandler()); // 业务处理器

                                      }
                                  }

                    );

            ChannelFuture channelFuture = serverBootstrap.bind(hostname, port).sync();
            log.info("服务提供方开始提供服务~~");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    /**
     * 服务器这边 handler 比较简单
     */
    @Slf4j
    public static class NettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 获取客户端发送的消息，并调用服务
            log.info("msg={}", msg);
            // 客户端在调用服务器的 api 时，我们需要定义一个协议
            // 比如我们要求 每次发消息是都必须以某个字符串开头 "HelloService#hello#你好"
            if (msg.toString().startsWith(ClientBootstrap.providerName)) {
                String result = new HelloServiceImpl()
                        .hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
                ctx.writeAndFlush(result);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
