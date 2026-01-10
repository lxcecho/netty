package io.netty.dubborpc.provider;

/**
 * ServerBootstrap 会启动一个服务提供者，就是 NettyServer
 *
 * @author lxcecho@gmail.com
 * @since 2026/1/10
 */
public class ServerBootstrap {
    public static void main(String[] args) {
        // 代码代填..
        NettyServer.startServer("127.0.0.1", 7000);
    }
}
