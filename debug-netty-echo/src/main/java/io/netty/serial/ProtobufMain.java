package io.netty.serial;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 15:40 23-10-2022
 */
@Slf4j
public class ProtobufMain {

    public static void main(String[] args) throws InvalidProtocolBufferException {

        UserProto.User user = UserProto.User.newBuilder().setName("lxcecho").setAge(599).build();
        ByteString bytes = user.toByteString();
        log.info("{}", bytes.size()); // 7

        for (byte bt : bytes.toByteArray()) {
            log.info("{}", bt);
        }

        UserProto.User userRever = UserProto.User.parseFrom(bytes);
        log.info("{}", userRever);

    }

}
