package com.lxcecho.netty.simple.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.jboss.marshalling.Marshaller;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
@Sharable
public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws Exception {
        try {
            // 起始数据位置
            int lengthPos = out.writerIndex();
            log.info("----序列化之前的 lengthPos: {}", lengthPos);
            out.writeBytes(LENGTH_PLACEHOLDER);
            log.info("----预留四个字节，用于更新数据长度！执行序列化操作。。----");
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            log.info("----序列化完毕后的 out.writerIndex(): {}", out.writerIndex());
            log.info("最后更新数据长度公式为：总长度-初始化长度-4个预留字节长度 = 数据长度，进行set设置值");
            // 写数据为：总长度-起始位置-4个字节为数据长度
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);

        } finally {
            marshaller.close();
        }
    }
}
