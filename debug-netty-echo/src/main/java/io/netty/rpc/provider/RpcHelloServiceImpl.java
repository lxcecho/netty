package io.netty.rpc.provider;

import io.netty.rpc.api.IRpcHelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 16:28 29-10-2022
 */
@Slf4j
public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String msg) {
        log.info("收到客户端消息：{}", msg);
        if (msg != null) {
            return "你好，客户端，我已经收到你发的消息 [" + msg + "] 了";
        } else {
            return "Hello, " + msg + "!";
        }
    }
}
