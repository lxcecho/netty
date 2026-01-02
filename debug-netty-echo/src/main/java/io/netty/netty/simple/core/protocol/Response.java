package io.netty.netty.simple.core.protocol;

import lombok.Data;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
@Data
public class Response {
    /* 请求包头  */
    public static final int CrcCode = 0xabef0101;

    /* 请求模块  */
    private short module;

    /* 命令号  */
    private short cmd;

    /* 状态码  */
    private byte messageType;

    /* 数据部分  */
    private byte[] data;

    /**
     * 获取数据长度
     *
     * @return
     */
    public int getDataLength(){
        if(data == null){
            return 0;
        }
        return data.length;
    }
}
