package com.lxcecho.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞 IO 模型
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 9:35 29-10-2022
 */
@Slf4j
public class BioServer {

    ServerSocket serverSocket;

    public BioServer(int port) {
        try {
            // 创建一个新的 ServerSocket， 用以监听指定端口上的连接请求
            serverSocket = new ServerSocket(port);
            log.info("BIO Server is completing on {}", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始监听
     *
     * @throws Exception
     */
    public void listen() throws Exception {
        // 循环监听
        while (true) {
            // 等待客户端连接，阻塞方法，直到一个连接建立
            // Socket 数据发送者在服务端的引用
            Socket socket = serverSocket.accept();
            log.info("port: {}", socket.getPort());

            // 对方法数据给我了，读 Input
            InputStream is = socket.getInputStream();

            // 网络客户端把数据发送到网卡，机器所得到的数据读到了 JVM 内中
            byte[] buff = new byte[1024];
            int len = is.read(buff);
            if (len > 0) {
                String msg = new String(buff, 0, len);
                log.info("receive: {}", msg);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new BioServer(8090).listen();
    }

}
