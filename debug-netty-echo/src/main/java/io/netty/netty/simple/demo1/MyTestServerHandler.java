package io.netty.netty.simple.demo1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 23:08 09-11-2022
 */
@Slf4j
public class MyTestServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("{} received: {}", ctx.channel().remoteAddress(), msg);
        ctx.channel().writeAndFlush("From server: " + UUID.randomUUID());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
