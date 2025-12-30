package io.netty.nio.chat;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 17.09.2021
 */
public class AClient {
    public static void main(String[] args) {
        try {
            new ChatClient().startClient("lxcecho");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
