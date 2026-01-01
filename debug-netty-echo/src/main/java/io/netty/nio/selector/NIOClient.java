package io.netty.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2021/2/21
 */
@Slf4j
public class NIOClient {
    public static void main(String[] args) throws Exception {
        // 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        // 提供服务器的ip和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
        // 连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                log.info("因为链接需要事件，客户端不会阻塞，可以做其他工作！");
            }
        }

        // 如果连接成功，就发送数据
        String str = "你好，Netty.";
        // Wraps a byte array into a buffer.
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
        // 发送数据，将 buffer 数据写入 channel
        socketChannel.write(byteBuffer);
        System.in.read();
    }
}
