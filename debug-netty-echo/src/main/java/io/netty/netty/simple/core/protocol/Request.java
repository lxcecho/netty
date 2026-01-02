package io.netty.netty.simple.core.protocol;

import lombok.Data;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Data
public class Request {
    /* 请求包头  */
    public static final int CrcCode = 0xabef0101;

    /* 请求模块  */
    private short module;

    /* 请求命令  */
    private short cmd;

    /* 数据内容  */
    private byte[] data;

    /**
     * 获取数据长度
     *
     * @return
     */
    public int getDataLength() {
        if (this.data == null) {
            return 0;
        }
        return this.data.length;
    }
}
