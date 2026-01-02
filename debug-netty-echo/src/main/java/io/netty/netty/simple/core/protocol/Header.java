package io.netty.netty.simple.core.protocol;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Data
@ToString
public class Header {
    private int crcCode = 0xabef0101;

    private int length; // 消息长度

    private long sessionID; // 会话 ID

    private byte type; // 消息类型

    private byte priority; // 消息优先级

    private Map<String, Object> attachment = new HashMap<String, Object>(); // 附件
}
