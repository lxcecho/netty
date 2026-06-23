package com.lxcecho.netty.tcp.resolve;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EmanClientHandler extends SimpleChannelInboundHandler<EmanMessageProtocol> {
    private int count;

    /**
     * 通道建立成功后，发送 10 条消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            String mes = "今天天气冷，吃火锅" + "【第" + (i + 1) + "条】";
            byte[] content = mes.getBytes(StandardCharsets.UTF_8);
            int len = content.length;

            // 创建协议包对象
            EmanMessageProtocol messageProtocol = new EmanMessageProtocol();
            messageProtocol.setLen(len);
            messageProtocol.setContent(content);

            ctx.writeAndFlush(messageProtocol);
            System.out.println("客户端发送第 " + (i + 1) + " 条消息：" + mes);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EmanMessageProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("客户端收到服务端回复：长度：" + len + "，内容：" + new String(content, StandardCharsets.UTF_8) + "，接收消息数量：" + (++this.count));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("客户端异常消息：" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
