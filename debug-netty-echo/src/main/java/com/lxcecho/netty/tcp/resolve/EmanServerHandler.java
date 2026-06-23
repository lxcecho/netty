package com.lxcecho.netty.tcp.resolve;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class EmanServerHandler extends SimpleChannelInboundHandler<EmanMessageProtocol> {

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EmanMessageProtocol msg) throws Exception {
        // 接收到数据，并处理
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("收到的信息如下：接收的数据包数量： " + (++this.count));
        System.out.println("长度：" + len + " 内容： " + new String(content, StandardCharsets.UTF_8));

        // 回复消息
        String res = UUID.randomUUID().toString();
        byte[] resContent = res.getBytes(StandardCharsets.UTF_8);
        int resLen = resContent.length;

        EmanMessageProtocol resMessageProtocol = new EmanMessageProtocol();
        resMessageProtocol.setLen(resLen);
        resMessageProtocol.setContent(resContent);

        ctx.writeAndFlush(resMessageProtocol);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("========== 有客户端连接成功！ ==========");
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("========== 客户端断开连接 ==========");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
