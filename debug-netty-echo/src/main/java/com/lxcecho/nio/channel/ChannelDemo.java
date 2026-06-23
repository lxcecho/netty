package com.lxcecho.nio.channel;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 2021/2/20
 */
@Slf4j
public class ChannelDemo {
    public static void main(String[] args) {

//        writeFile01();

//        writeFile02();

//        writeFile03();

        writeFile04();
    }

    /**
     * 使用 FileChannel(通道) 和 方法  transferFrom ，完成文件的拷贝。
     */
    private static void writeFile04() {
        // 创建相关流
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel01 = null;
        FileChannel fileChannel02 = null;

        try {
            fileInputStream = new FileInputStream("D:\\4-1-1.jpg");
            fileOutputStream = new FileOutputStream("D:\\4-1-2.jpg");

            // 获取各个流对应的 FileChannel
            fileChannel01 = fileInputStream.getChannel();
            fileChannel02 = fileOutputStream.getChannel();

            // 使用 transferFrom 完成拷贝
//            fileChannel02.transferFrom(fileChannel01, 0, fileChannel01.size());
            // 使用 transferTo
            fileChannel01.transferTo(0, fileChannel01.size(), fileChannel02);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭相应的通道和流
                if (fileChannel01 != null) {
                    fileChannel01.close();
                }
                if (fileChannel02 != null) {
                    fileChannel02.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用 FileChannel(通道) 和 方法  read , write，完成文件的拷贝。
     */
    private static void writeFile03() {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream("D:\\file01.txt");
            FileChannel fileChannel01 = fileInputStream.getChannel();

            fileOutputStream = new FileOutputStream("2.txt");
            FileChannel fileChannel02 = fileOutputStream.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(512);

            while (true) {// 循环读取
                // 重要的操作
                /*public final Buffer clear() {
                    position = 0;
                    limit = capacity;
                    mark = -1;
                    return this;
                }*/

                byteBuffer.clear();// 清空 buffer，重置/复位
                // 把数据从 fileChannel01 读到 buffer 缓冲区
                int read = fileChannel01.read(byteBuffer);
                log.info("read = {}", read);
                if (read == -1) {// 表示读完
                    break;
                }

                // 将 buffer 中的数据写入到 fileChannel02
                byteBuffer.flip();// 反转此缓冲区（我理解的是 读写模式切换）
                fileChannel02.write(byteBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭相关的流
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用 ByteBuffer(缓冲) 和 FileChannel(通道)， 将 file01.txt 中的数据读入到程序，并显示在控制台屏幕。
     */
    private static void writeFile02() {
        FileInputStream fileInputStream = null;

        try {
            // 创建文件的输入流
            File file = new File("D:\\file01.txt");
            fileInputStream = new FileInputStream(file);

            // 通过 FileInputStream 获取对应的 FileChannel --> 实际类型 FileChannelImpl
            FileChannel fileChannel = fileInputStream.getChannel();

            // 创建缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

            // 将通道的数据读入到 Buffer
            fileChannel.read(byteBuffer);

            // 将 ByteBuffer 的字节数据转成 String
            log.info("{}", new String(byteBuffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 使用 ByteBuffer(缓冲) 和 FileChannel(通道)， 将 "hello,Netty" 写入到 file01.txt 中。
     */
    private static void writeFile01() {
        String str = "Hello,Netty";
        FileOutputStream fileOutputStream = null;

        try {
            // 创建一个输出流-->Channel
            fileOutputStream = new FileOutputStream("D:\\file01.txt");

            // 通过 FileOutPutStream 获取对应的 FileChannel
            // 这个 FileChannel 真实类型是 FileChannelImpl
            FileChannel fileChannel = fileOutputStream.getChannel();

            // 创建一个缓冲区 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            // 将 str 放入 ByteBuffer
            byteBuffer.put(str.getBytes());

            // 对 ByteBuffer 进行 flip
            byteBuffer.flip();

            // 将 ByteBuffer 数据写入到 FileChannel
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
