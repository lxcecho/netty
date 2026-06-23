package com.lxcecho.netty.tcp.resolve;

import lombok.Data;

@Data
public class EmanMessageProtocol {
    /*关键*/
    private int len;

    private byte[] content;

}
