package io.netty.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 接收 Server 端回送的响应
 * 注意：
 * 在这里我们的 NettyHttpClientHandler 继承自 SimpleChannelInboundHandler，我们点击该类进入源码。
 * public abstract class SimpleChannelInboundHandler<I> extends ChannelInboundHandlerAdapter {
 * 发现 ChannelInboundHandlerAdapter 是 SimpleChannelInboundHandler 的父类。
 * 仔细阅读源码时，你会发现它们的区别就是一个对 buffer 对象没有回收释放资源处理，另一个帮做了回收释放资源处理。
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
public class NettyHttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject message) throws Exception {
        // 判断是否为 HttpResponse 对象
        if (message instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) message;
            // 打印服务器返回的 httpResponse
            log.error("res={}", response);
        }
        // 判断是否包含请求体内容处理
        if (message instanceof HttpContent) {
            HttpContent hc = (HttpContent) message;
            ByteBuf buf = hc.content();
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body = new String(req, StandardCharsets.UTF_8);
            // 打印服务器返回的 body
            log.error("Server Receive Content : {}", body);
        }

    }
}