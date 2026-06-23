package com.lxcecho.nio.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 22:15 24-11-2022
 */
@Slf4j
public class NIOFileChannelDemo {

    public static void main(String[] args) throws Exception{

        // 本地文件写数据
//        writeFileByChannel();

        // 本地文件读取数据
//        readFileByChannel();

        // 使用而一个 Buffer 完成文件读取、写入
//        readWriteFileByBuffer();

        copyFileByTransferFrom();
    }

    private static void copyFileByTransferFrom() throws IOException {
        // 创建相关流
        FileInputStream fileInputStream = new FileInputStream("d:\\a.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\a2.jpg");

        // 获取各个流对应的 fileChannel
        FileChannel sourceCh = fileInputStream.getChannel();
        FileChannel destCh = fileOutputStream.getChannel();

        // 使用 transferForm 完成拷贝
        destCh.transferFrom(sourceCh, 0, sourceCh.size());

        // 关闭相关通道和流
        sourceCh.close();
        destCh.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    private static void readWriteFileByBuffer() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel fileChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true) { // 循环读取

            // 这里有一个重要的操作，一定不要忘了
            /*
             public final Buffer clear() {
                position = 0;
                limit = capacity;
                mark = -1;
                return this;
            }
             */
            byteBuffer.clear(); // 清空 buffer

            int read = fileChannel01.read(byteBuffer);
            log.info("read = {}", read);

            if (read == -1) { // 表示读完
                break;
            }

            // 将 buffer 中的数据写入到 fileChannel02 -- 2.txt
            byteBuffer.flip();

            fileChannel02.write(byteBuffer);
        }

        // 关闭相关的流
        fileInputStream.close();
        fileOutputStream.close();
    }

    private static void readFileByChannel() throws IOException {
        // 创建文件的输入流
        File file = new File("d:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        // 通过 fileInputStream 获取对应的 FileChannel -> 实际类型  FileChannelImpl
        FileChannel fileChannel = fileInputStream.getChannel();

        // 创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        // 将通道的数据读入到 Buffer
        fileChannel.read(byteBuffer);

        // 将 byteBuffer 的 字节数据 转成 String
        log.info("{}", new String(byteBuffer.array()));

        fileInputStream.close();
    }

    private static void writeFileByChannel() throws IOException {
        String str = "Hello, 韩顺平老师";
        // 创建一个输出流 -> channel
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");

        // 通过 fileOutputStream 获取 对应的 FileChannel
        // 这个 fileChannel 真实 类型是  FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        // 创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 将 str 放入 byteBuffer
        byteBuffer.put(str.getBytes());


        // 对 byteBuffer 进行 flip
        byteBuffer.flip();

        // 将 byteBuffer 数据写入到 fileChannel
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }

}
