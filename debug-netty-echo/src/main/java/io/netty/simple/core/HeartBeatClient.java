package io.netty.simple.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.simple.core.codec.NettyMessageDecoder;
import io.netty.simple.core.codec.NettyMessageEncoder;
import io.netty.simple.core.protocol.Header;
import io.netty.simple.core.protocol.MessageType;
import io.netty.simple.core.protocol.NettyConstant;
import io.netty.simple.core.protocol.NettyMessage;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
public class HeartBeatClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws Exception {

        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
                            ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                            ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
                            ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
                            //ch.pipeline().addLast("HeartBeatHandler", new SeverHander());
                        }
                    });
            ChannelFuture future = b.connect(
                    new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP,
                            NettyConstant.LOCAL_PORT)).sync();
            log.info("Client Start.. ");
            future.channel().closeFuture().sync();
        } finally {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        try {
                            connect(NettyConstant.PORT, NettyConstant.REMOTE_IP); // 发起重连操作
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new HeartBeatClient().connect(NettyConstant.PORT, NettyConstant.REMOTE_IP);
    }

    static class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(buildLoginReq());
        }

        private NettyMessage buildLoginReq() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.LOGIN_REQ.value());
            message.setHeader(header);
            return message;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            NettyMessage message = (NettyMessage) msg;
            if (message.getHeader() != null
                    && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

                byte loginResult = (byte) message.getBody();
                if (loginResult != (byte) 0) {
                    ctx.close();
                } else {
                    log.info("Login is ok : {}", message);
                    ctx.fireChannelRead(msg);
                }

            } else {
                ctx.fireChannelRead(msg);
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.fireExceptionCaught(cause);
        }

    }


    static class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

        private volatile ScheduledFuture<?> heartBeat;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            NettyMessage message = (NettyMessage) msg;

            if (message.getHeader() != null
                    && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

                this.heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);

            } else if (message.getHeader() != null
                    && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {

                log.info("Client receive com.bfxy.netty.server heart beat message : ---> {}", message);

            } else {
                ctx.fireChannelRead(msg);
            }
        }

        private class HeartBeatTask implements Runnable {
            private final ChannelHandlerContext ctx;

            public HeartBeatTask(final ChannelHandlerContext ctx) {
                this.ctx = ctx;
            }

            @Override
            public void run() {
                NettyMessage heatBeat = buildHeatBeat();
                log.info("Client send heart beat message to com.bfxy.netty.server : ---> {}", heatBeat);
                ctx.writeAndFlush(heatBeat);
            }

            private NettyMessage buildHeatBeat() {
                NettyMessage message = new NettyMessage();
                Header header = new Header();
                header.setType(MessageType.HEARTBEAT_REQ.value());
                message.setHeader(header);
                return message;
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            log.info("感知到 Server 端关闭。取消心跳任务");

            cause.printStackTrace();
            if (heartBeat != null) {
                heartBeat.cancel(true);
                heartBeat = null;
            }
            ctx.fireExceptionCaught(cause);
        }
    }
}
