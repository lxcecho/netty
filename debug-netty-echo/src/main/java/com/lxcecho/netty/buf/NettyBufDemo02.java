package com.lxcecho.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 10.10.2021
 */
@Slf4j
public class NettyBufDemo02 {
    public static void main(String[] args) {
        // Charset utf8 = Charset.forName("utf-8");
        Charset utf8 = CharsetUtil.UTF_8;
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello, world!", utf8);

        if (byteBuf.hasArray()) {
            byte[] content = byteBuf.array();

            // 将 content 转成字符
            log.info("{}", new String(content, utf8));

            log.info("byteBuf={}", byteBuf);

            log.info("{}", byteBuf.arrayOffset()); // 0
            log.info("{}", byteBuf.readerIndex()); // 0
            log.info("{}", byteBuf.writerIndex()); // 12
            log.info("{}", byteBuf.capacity()); // 36

            log.info("{}", byteBuf.getByte(0)); // 104
//            log.info("{}", byteBuf.readByte());

            // 可读字节数
            int len = byteBuf.readableBytes(); //12
            log.info("len={}", len);

            // 使用 for 取出各个字节
            for (int i = 0; i < len; i++) {
                log.info("{}", (char) byteBuf.getByte(i));
            }

            // 按照某个范围读取
            log.info("{}", byteBuf.getCharSequence(0, 4, utf8));
            log.info("{}", byteBuf.getCharSequence(4,6, utf8));

        }
    }
}
