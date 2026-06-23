package com.lxcecho.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 22:46 30-10-2022
 */
@Slf4j
public class OutboundHandlerB extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("OutboundHandler B");
        ctx.write(msg, promise);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
                ctx.channel().write("Hello");
            }
        }, 3, TimeUnit.SECONDS);
    }
}
