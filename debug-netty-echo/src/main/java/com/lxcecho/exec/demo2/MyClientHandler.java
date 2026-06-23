package com.lxcecho.exec.demo2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 27.03.2022
 */
@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("{} client output: {}", ctx.channel().remoteAddress(), msg);
        ctx.channel().writeAndFlush("from client: " + LocalDateTime.now());
    }

    /**
     * 导火索
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("来自客户端的问候!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
