package com.lxcecho.serialize;

import com.lxcecho.serialize.hessian.HessianSerializer;
import com.lxcecho.serialize.java.ISerializer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 15:04 23-10-2022
 */
@Slf4j
public class SerializerMain {

    public static void main(String[] args) {
        /*ISerializable serializable = new JavaSerializer();
        User user = new User();
        user.setName("lxcecho");
        user.setAge(18);

        byte[] bytes = serializable.serialize(user);
        log.info("{}", bytes);

        for (byte aByte : bytes) {
            log.info("{}", aByte+" ");
        }

        User userDeserialize = serializable.deserialize(bytes);
        log.info("{}", userDeserialize);*/

        User user = new User();
        user.setName("lxcecho");
        user.setAge(18);
        ISerializer serializer = new HessianSerializer();
        byte[] bytes = serializer.serialize(user);
        User userRever = serializer.deserialize(bytes);
        log.info("len={}, content={}, userRever={}", bytes.length, new String(bytes), userRever);

    }
}
