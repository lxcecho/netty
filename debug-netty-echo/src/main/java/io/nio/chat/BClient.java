package io.nio.chat;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 17.09.2021
 */
public class BClient {
    public static void main(String[] args) {
        try {
            new ChatClient().startClient("zake");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
