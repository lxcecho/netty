package io.netty.netty.rpc.provider;

import io.netty.netty.rpc.api.IRpcService;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 16:28 29-10-2022
 */
public class RpcServiceImpl implements IRpcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int subtract(int a, int b) {
        return a - b;
    }

    @Override
    public int multiply(int a, int b) {
        return a * b;
    }

    @Override
    public int divide(int a, int b) {
        return a / b;
    }
}
