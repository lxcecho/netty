package io.netty;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URL;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/1
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://repo.spring.io/milestone/io/projectreactor/netty/reactor-netty5-http/2.0.0-M3/reactor-netty5-http-2.0.0-M3.jar");
        try (InputStream in = url.openStream()) {
            log.info("✅ 成功连接！证书验证通过。");
        }
    }
}
