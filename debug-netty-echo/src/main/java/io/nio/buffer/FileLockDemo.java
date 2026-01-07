package io.nio.buffer;

import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 文件共享锁和排他锁
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 22:30 13-11-2022
 */
@Slf4j
public class FileLockDemo {

    public static void main(String[] args) throws Exception {
        RandomAccessFile file = new RandomAccessFile("lxcecho.txt", "rw");
        FileChannel channel = file.getChannel();

        FileLock lock = channel.lock(3, 6, true);
        log.info("valid: {} lockType: {}", lock.isValid(), lock.isShared());

        lock.release();
        file.close();
    }

}
