package io.aio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 23:15 27-10-2022
 */
@Slf4j
public class AIOServer {

    private final int port;

    public AIOServer(int port) {
        this.port = port;
        listen();
    }

    private void listen() {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
            final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(threadGroup);
            serverSocketChannel.bind(new InetSocketAddress(port));
            log.info("服务端已启动，监听端口：{}", port);

            serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

                /**
                 * 处理接收到消息以后的逻辑，将接收到的信息再输出到客户端
                 *
                 * @param result
                 *          The result of the I/O operation.
                 * @param attachment
                 *          The object attached to the I/O operation when it was initiated.
                 */
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    log.info("IO 操作成功，开始获取数据");
                    try {
                        buffer.clear();
                        result.read(buffer).get();
                        buffer.flip();
                        result.write(buffer);
                        buffer.flip();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error(e.getMessage());
                    } finally {
                        try {
                            result.close();
                            serverSocketChannel.accept(null, this);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                    log.info("操作完成");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    log.info("IO操作失败：{}", exc.getMessage());
                }
            });

            try {
                TimeUnit.MILLISECONDS.sleep(Integer.MAX_VALUE);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 开启一个监听端口
        new AIOServer(8090);
    }

}
