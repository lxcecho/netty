package com.lxcecho.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池实现服务端
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 2021/2/19
 */
@Slf4j
public class BIOServer2 {

    public static void main(String[] args) throws Exception {
        // 线程池机制
        // 1 创建一个线程池
        // 2 如果有客户端连接，就创建一个线程与之通讯（单独写一个方法）
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 创建 ServerSocket
        ServerSocket serverSocket = new ServerSocket(8090);
        log.info("服务器启动了...");
        while (true) {
            log.info("线程信息 id = {}，名字 name = {}", Thread.currentThread().getId(), Thread.currentThread().getName());
            // 监听，等待客户端链接
            log.info("等待连接...."); // 连接成功之后 阻塞在这里
            final Socket socket = serverSocket.accept();
            log.info("连接到了一个客户端...");

            // 就创建一个线程，与之通讯（单独写一个方法）
            executorService.execute(() -> { // 重写
                // 可以和客户端通讯
                handler(socket);
            });
        }
    }

    /**
     * 编写一个 handler 方法，和客户端通讯
     *
     * @param socket
     */
    private static void handler(Socket socket) {
        try {
            log.info("线程信息 id = {}，名字 name = {}", Thread.currentThread().getId(), Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            // 通过 socket 获取数据流
            InputStream inputStream = socket.getInputStream();

            // 循环的读取客户端发送的数据
            while (true) {
                log.info("线程信息 id = {}，名字 name = {}", Thread.currentThread().getId(), Thread.currentThread().getName());
                log.info("read..."); // 完成通讯之后，阻塞在这里
                int read = inputStream.read(bytes);
                if (read != -1) {
                    // 输出到客户端发送的数据
                    log.info(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("关闭和 client 的连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
