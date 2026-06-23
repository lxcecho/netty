package com.lxcecho.netty.simple.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import com.lxcecho.netty.simple.core.codec.NettyMessageDecoder;
import com.lxcecho.netty.simple.core.codec.NettyMessageEncoder;
import com.lxcecho.netty.simple.core.protocol.Header;
import com.lxcecho.netty.simple.core.protocol.MessageType;
import com.lxcecho.netty.simple.core.protocol.NettyConstant;
import com.lxcecho.netty.simple.core.protocol.NettyMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
public class HeartBeatServer {
    public void bind() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws IOException {
                        ch.pipeline().addLast(
                                new NettyMessageDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast(new NettyMessageEncoder());
                        ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        ch.pipeline().addLast(new LoginAuthRespHandler());
                        ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRespHandler());
                    }
                });
        ChannelFuture cf = b.bind(NettyConstant.REMOTE_IP, NettyConstant.PORT).sync();
        log.info("Netty com.bfxy.netty.server start ok : {}", (NettyConstant.REMOTE_IP + " : " + NettyConstant.PORT));
        cf.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        new HeartBeatServer().bind();
    }

    static class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

        private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
        private String[] whiteList = {"127.0.0.1", "192.168.1.200"};

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            NettyMessage message = (NettyMessage) msg;

            if (message.getHeader() != null
                    && message.getHeader().getType() == MessageType.LOGIN_REQ
                    .value()) {
                String nodeIndex = ctx.channel().remoteAddress().toString();
                NettyMessage loginResp = null;
                if (nodeCheck.containsKey(nodeIndex)) {
                    loginResp = buildResponse((byte) -1);
                } else {
                    InetSocketAddress address = (InetSocketAddress) ctx.channel()
                            .remoteAddress();
                    String ip = address.getAddress().getHostAddress();
                    boolean isOK = false;
                    for (String WIP : whiteList) {
                        if (WIP.equals(ip)) {
                            isOK = true;
                            break;
                        }
                    }
                    loginResp = isOK ? buildResponse((byte) 0)
                            : buildResponse((byte) -1);
                    if (isOK)
                        nodeCheck.put(nodeIndex, true);
                }
                log.info("The login response is : {} body [{}]", loginResp, loginResp.getBody());
                ctx.writeAndFlush(loginResp);
            } else {
                ctx.fireChannelRead(msg);
            }
        }

        private NettyMessage buildResponse(byte result) {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.LOGIN_RESP.value());
            message.setHeader(header);
            message.setBody(result);
            return message;
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            cause.printStackTrace();
            nodeCheck.remove(ctx.channel().remoteAddress().toString()); // 删除缓存
            ctx.close();
            ctx.fireExceptionCaught(cause);
        }
    }

    static class HeartBeatRespHandler extends ChannelDuplexHandler {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            NettyMessage message = (NettyMessage) msg;
            if (message.getHeader() != null
                    && message.getHeader().getType() == MessageType.HEARTBEAT_REQ
                    .value()) {
                log.info("Receive com.bfxy.netty.client heart beat message : ---> {}", message);
                NettyMessage heartBeat = buildHeatBeat();
                log.info("Send heart beat response message to com.bfxy.netty.client : ---> {}", heartBeat);
                ctx.writeAndFlush(heartBeat);
            } else
                ctx.fireChannelRead(msg);
        }

        private NettyMessage buildHeatBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_RESP.value());
            message.setHeader(header);
            return message;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("channelActive..");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.info("channelInactive..");
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            log.info("userEventTriggered..");
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            log.info("感知到 Client 端关闭, 删除监听");

            cause.printStackTrace();
            ctx.fireExceptionCaught(cause);
        }

    }
}
