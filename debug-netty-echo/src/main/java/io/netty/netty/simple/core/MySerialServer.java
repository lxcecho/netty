package io.netty.netty.simple.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.netty.simple.core.codec.RequestDecoder;
import io.netty.netty.simple.core.codec.ResponseEncoder;
import io.netty.netty.simple.core.protocol.MessageType;
import io.netty.netty.simple.core.protocol.Request;
import io.netty.netty.simple.core.protocol.Response;
import io.netty.netty.simple.core.transfer.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
public class MySerialServer {
    public static void main(String[] args) throws Exception {
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws IOException {
                        ch.pipeline().addLast("requestDecoder", new RequestDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast("responseEncoder", new ResponseEncoder());
                        ch.pipeline().addLast("serverHandler", new MySerialServerHandler());
                    }
                });
        // 绑定端口，同步等待成功
        ChannelFuture cf = b.bind(8765).sync();
        log.info("Server start.. ");
        cf.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    static class MySerialServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            Request message = (Request) msg;

            if (message.getModule() == 1) {
                if (message.getCmd() == 1) {
                    User requestUser = new User();
                    requestUser.readFromBytes(message.getData());
                    log.info("====服务器端====userId: {}, userName: {}", requestUser.getUserId(), requestUser.getUserName());

                    // 回写数据
                    User responseUser = new User();
                    responseUser.setUserId("1002");
                    responseUser.setUserName("李四");
                    responseUser.setAge(29);
                    List<String> favorite = new ArrayList<String>();
                    favorite.add("足球");
                    favorite.add("篮球");
                    requestUser.setFavorite(favorite);

                    Response response = new Response();
                    response.setModule((short) 1);
                    response.setCmd((short) 1);
                    response.setMessageType(MessageType.SUCCESS.value());
                    response.setData(responseUser.getBytes());
                    ctx.writeAndFlush(response);
                } else if (message.getCmd() == 2) {

                }
            } else if (message.getModule() == 2) {

            }
        }
    }
}
