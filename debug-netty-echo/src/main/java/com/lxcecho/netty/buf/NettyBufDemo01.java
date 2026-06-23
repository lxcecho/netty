package com.lxcecho.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 10.10.2021
 */
@Slf4j
public class NettyBufDemo01 {

    public static void main(String[] args) {
        // 创建一个 ByteBuf
        // 1.创建对象，该对象包含一个数组 arr，是一个 byte[10]
        // 2.在 Netty 的 Buffer 中，不需要使用 flip 进行反转，底层维护了 readIndex 和 writeIndex
        // 3.通过 readIndex 和 writeIndex 以及 capacity，将 buffer 分成三个区域
        // 0---readIndex 已经读取的区域
        // readIndex---writeIndex 可读的区域
        // writeIndex---capacity 可写的区域
        ByteBuf buffer = Unpooled.buffer(10);

        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        log.info("capacity= {}", buffer.capacity()); // 10

        // 输出
        for (int i = 0; i < buffer.capacity(); i++) {
//            log.info("{}", buffer.getByte(i)); // 不会造成 readIndex 的变化，因为这是靠索引读取的
            log.info("{}", buffer.readByte());
        }
    }

}
