package io.netty.netty.simple.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.netty.simple.core.protocol.Request;

/**
 * 请求编码器
 * <p>
 * 数据包格式
 * +——----——+——-----——+——----——+——----——+——-----——+
 * |  包头       |  模块号      |  命令号    |  长度       |   数据      |
 * +——----——+——-----——+——----——+——----——+——-----——+
 * 包头4字节
 * 模块号2字节short
 * 命令号2字节short
 * 长度4字节(描述数据部分字节长度)
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
public class RequestEncoder extends MessageToByteEncoder<Request> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Request request, ByteBuf out) throws Exception {
        // 包头
        out.writeInt(Request.CrcCode);
        // module
        out.writeShort(request.getModule());
        // cmd
        out.writeShort(request.getCmd());
        // 长度
        out.writeInt(request.getDataLength());
        // data
        if (request.getData() != null) {
            out.writeBytes(request.getData());
        }
    }
}
