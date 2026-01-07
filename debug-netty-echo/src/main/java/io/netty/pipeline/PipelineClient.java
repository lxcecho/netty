package io.netty.pipeline;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 22:54 30-10-2022
 */
@Slf4j
public class PipelineClient {
    public void connect(String host, int port) throws Exception {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientIntHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new PipelineClient().connect("localhost", 8090);
    }

    static class ClientIntHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("ClientIntHandler.channelRead");
            ByteBuf result = (ByteBuf) msg;
            byte[] content = new byte[result.readableBytes()];
            result.readBytes(content);
            result.release();
            ctx.close();
            log.info("Server said: {}", new String(content));
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("ClientIntHandler.channelActive");
            String msg = "Are u OK?";
            ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
            encoded.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
            ctx.write(encoded);
            ctx.flush();
        }
    }
}
