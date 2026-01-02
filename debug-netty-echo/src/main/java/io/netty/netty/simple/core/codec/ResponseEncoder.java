package io.netty.netty.simple.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.netty.simple.core.protocol.Response;

/**
 * 响应编码器
 * <p>
 * 数据包格式
 * +——----——+——-----——+——----——+——----——+——-----——+——-----——+
 * | 包头          | 模块号        | 命令号      |信息类型    |  长度          |   数据       |
 * +——----——+——-----——+——----——+——----——+——-----——+——-----——+
 * 包头4字节
 * 模块号2字节short
 * 命令号2字节short
 * 长度4字节(描述数据部分字节长度)
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {

        // 包头
        out.writeInt(Response.CrcCode);
        // module
        out.writeShort(response.getModule());
        // cmd
        out.writeShort(response.getCmd());
        // 状态码
        out.writeByte(response.getMessageType());
        // 长度
        out.writeInt(response.getDataLength());
        // data
        if (response.getData() != null) {
            out.writeBytes(response.getData());
        }
    }
}