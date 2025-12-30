package io.netty.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 23:30 20-10-2022
 */
@Slf4j
public class SocketClientDemo {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 8080);
            Thread.sleep(10000);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("我是客户端，发送了一个消息\n");
            bufferedWriter.flush();
            // 接收服务端返回结果
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 输入流
            String serverLine = bufferedReader.readLine(); // 读取服务端返回的数据（被阻塞了）
            log.info("服务端返回的数据: {}", serverLine);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
