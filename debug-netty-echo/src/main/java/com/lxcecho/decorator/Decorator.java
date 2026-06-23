package com.lxcecho.decorator;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 03.04.2022
 */
public class Decorator implements Component {

    private Component component;

    public Decorator(Component component) {
        this.component = component;
    }

    @Override
    public void doSomething() {
        component.doSomething();
    }
}
