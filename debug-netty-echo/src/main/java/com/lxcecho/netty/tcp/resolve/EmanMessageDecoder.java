package com.lxcecho.netty.tcp.resolve;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class EmanMessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyMessageDecoder decode 被调用");
        // 需要将得到二进制字节码==》MyMessageProtocol 数据包对象
        int len = in.readInt();

        byte[] content = new byte[len];

        in.readBytes(content);

        // 封装成 MyMessageProtocol 对象，放入 out，传递下一个 handler 业务处理
        EmanMessageProtocol messageProtocol = new EmanMessageProtocol();
        messageProtocol.setLen(len);
        messageProtocol.setContent(content);
        out.add(messageProtocol);
    }
}
