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
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
