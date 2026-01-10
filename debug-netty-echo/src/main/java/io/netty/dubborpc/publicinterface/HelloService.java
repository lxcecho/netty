package io.netty.dubborpc.publicinterface;

/**
 * 这个是接口，是服务提供方和 服务消费方都需要
 *
 * @author lxcecho@gmail.com
 * @since 2026/1/10
 */
public interface HelloService {

    String hello(String mes);

}
