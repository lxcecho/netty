package com.lxcecho.nio.zerocopy;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 29.05.2021
 */
@Slf4j
public class NewIoClient {

    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",7001));
        String filename = "protoc-3.6.1-win32.zip";

        // 得到一个文件 channel
        FileChannel fileChannel = new FileInputStream(filename).getChannel();

        // 准备发送
        long startTime = System.currentTimeMillis();

        // 在 linux 下一个 transferTo 方法就可以完成传输
        // 在 windows 下，一次调用 transferTo 只能发送 8M，就需要分段传输文件，而且主要传输时的位置 ---> 思考？？？
        // transferTo 底层使用到 零拷贝
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        log.info("发送总字节数 {}，耗时 {}", transferCount, (System.currentTimeMillis() - startTime));

        // close
        fileChannel.close();
    }

}
