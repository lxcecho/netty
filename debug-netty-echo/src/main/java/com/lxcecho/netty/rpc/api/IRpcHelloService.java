package com.lxcecho.netty.rpc.api;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 16:24 29-10-2022
 */
public interface IRpcHelloService {
    /**
     * 确认服务可用
     *
     * @param msg
     * @return
     */
    String hello(String msg);
}
