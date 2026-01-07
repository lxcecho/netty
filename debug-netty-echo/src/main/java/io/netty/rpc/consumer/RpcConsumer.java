package io.netty.rpc.consumer;

import io.netty.rpc.api.IRpcHelloService;
import io.netty.rpc.api.IRpcService;
import io.netty.rpc.consumer.proxy.RpcProxy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 16:26 29-10-2022
 */
@Slf4j
public class RpcConsumer {

    public static void main(String[] args) {
        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);
        log.info(rpcHello.hello("lxcecho"));

        IRpcService service = RpcProxy.create(IRpcService.class);

        log.info("add: {}", service.add(8, 2));
        log.info("subtract: {}", service.subtract(8, 2));
        log.info("multiply: {}", service.multiply(8, 2));
        log.info("divide: {}", service.divide(8, 2));
    }

}
