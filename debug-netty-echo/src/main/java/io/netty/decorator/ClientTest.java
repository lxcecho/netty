package io.netty.decorator;

import org.junit.Test;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 03.04.2022
 */
public class ClientTest {


    @Test
    public void testDecorator() {
        Component component = new ConcreteDecorator1(new ConcreteDecorator1(new ConcreteComponent()));
        component.doSomething();
    }

}
