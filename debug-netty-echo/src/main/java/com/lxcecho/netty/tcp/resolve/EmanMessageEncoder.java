package com.lxcecho.netty.tcp.resolve;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EmanMessageEncoder extends MessageToByteEncoder<EmanMessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, EmanMessageProtocol msg, ByteBuf out) throws Exception {
        System.out.println("EmanMessageEncoder encode 被调用");
        // 先写长度（4字节）
        out.writeInt(msg.getLen());
        // 再写内容
        out.writeBytes(msg.getContent());
    }
}
