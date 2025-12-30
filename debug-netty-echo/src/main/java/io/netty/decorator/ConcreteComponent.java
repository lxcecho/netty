package io.netty.decorator;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 03.04.2022
 */
@Slf4j
public class ConcreteComponent implements Component {
    @Override
    public void doSomething() {
        log.info("功能A");
    }
}
