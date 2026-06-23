package com.lxcecho.serialize.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * <a href="https://jbossmarshalling.jboss.org/docs">...</a>
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/1
 */
public class MarshallingCodeCFactory {
    /**
     * 创建Jboss Marshalling解码器MarshallingDecoder
     *
     * @return MarshallingDecoder
     */
    public static MarshallingDecoder buildMarshallingDecoder() {
        // 首先通过 Marshalling 工具类的方法获取 Marshalling 实例对象 参数 serial 标识创建的是 java 序列化工厂对象。
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        // 创建了 MarshallingConfiguration 对象，配置了版本号为 5
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        // 根据 marshallerFactory 和 configuration 创建 provider
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        // 构建 Netty 的 MarshallingDecoder 对象，俩个参数分别为 provider 和单个消息序列化后的最大长度
        return new MarshallingDecoder(provider, 1024 * 1024);
    }

    /**
     * 创建 JbossMarshalling 编码器 MarshallingEncoder
     *
     * @return MarshallingEncoder
     */
    public static MarshallingEncoder buildMarshallingEncoder() {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        // 构建 Netty 的 MarshallingEncoder 对象，MarshallingEncoder 用于实现序列化接口的 POJO 对象序列化为二进制数组
        return new MarshallingEncoder(provider);
    }
}
