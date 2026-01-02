package io.netty.netty.simple.core.protocol;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Slf4j
public enum MessageType {
    /**
     * 业务请求成功
     */
    SUCCESS((byte) 0),

    /**
     * 业务相应失败
     */
    FAILURE((byte) 1),

    /**
     * 业务请求消息
     */
    SERVICE_REQ((byte) 2),

    /**
     * 业务相应消息
     */
    SERVICE_RESP((byte) 3),

    /**
     * 业务 ONE WAY 既是请求又是响应消息
     */
    ONE_WAY((byte) 4),

    /**
     * 握手请求消息
     */
    LOGIN_REQ((byte) 5),

    /**
     * 握手响应消息
     */
    LOGIN_RESP((byte) 6),

    /**
     * 心跳请求消息
     */
    HEARTBEAT_REQ((byte) 7),

    /**
     * 心跳响应消息
     */
    HEARTBEAT_RESP((byte) 8);

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static void main(String[] args) {
        log.info("suc={}, fail={}", MessageType.SUCCESS.value(), MessageType.FAILURE.value());
    }
}
