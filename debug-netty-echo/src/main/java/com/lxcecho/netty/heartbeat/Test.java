package com.lxcecho.netty.heartbeat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {

        log.info("{}", System.nanoTime()); // 纳秒 10 亿分之 1
        Thread.sleep(1000);
        log.info("{}", System.nanoTime());

    }
}
