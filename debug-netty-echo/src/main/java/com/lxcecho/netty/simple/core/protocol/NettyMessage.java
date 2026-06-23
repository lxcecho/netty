package com.lxcecho.netty.simple.core.protocol;

import lombok.Data;
import lombok.ToString;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Data
@ToString
public class NettyMessage {
    private Header header;

    private Object body;
}
