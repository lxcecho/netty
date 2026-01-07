package io.netty.simple.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.simple.core.codec.RequestEncoder;
import io.netty.simple.core.codec.ResponseDecoder;
import io.netty.simple.core.protocol.Request;
import io.netty.simple.core.protocol.Response;
import io.netty.simple.core.transfer.User;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
public class MySerialClient {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("responseDecoder", new ResponseDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast("requestEncoder", new RequestEncoder());
                        ch.pipeline().addLast("clientHandler", new MySerialClientHandler());
                    }
                });
        // 发起异步连接操作
        ChannelFuture future = b.connect(new InetSocketAddress("127.0.0.1", 8765)).sync();

        Channel channel = future.channel();

        log.info(" Client start.. ");

        for (int i = 0; i < 1000; i++) {
            Request request = new Request();
            request.setModule((short) 1);
            request.setCmd((short) 1);

            User requestUser = new User();
            requestUser.setUserId("1001");
            requestUser.setUserName("张三");
            requestUser.setAge(20);
            List<String> favorite = new ArrayList<String>();
            favorite.add("足球");
            favorite.add("篮球");
            requestUser.setFavorite(favorite);

            request.setData(requestUser.getBytes());
            // 发送请求
            channel.writeAndFlush(request);
            // TimeUnit.NANOSECONDS.sleep(1);
        }


        future.channel().closeFuture().sync();
    }

    static class MySerialClientHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                Response message = (Response) msg;
                if (message.getModule() == 1) {
                    if (message.getCmd() == 1) {
                        User responseUser = new User();
                        responseUser.readFromBytes(message.getData());
                        log.info("=====客户端=====userId: {}, userName: {}", responseUser.getUserId(), responseUser.getUserName());
                    } else if (message.getCmd() == 2) {
                        //TODO
                    }
                } else if (message.getModule() == 2) {
                    //TODO
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }

}
