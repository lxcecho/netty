package com.lxcecho.serialize.proto;

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

        /**
         *  序列化机制：
         *  1. java 序列化 比如一个 int 类型(4个字节长度)
         *  // int a = 2   &  int a = 110000000
         *  java 的序列化无论真是的 int 类型数值大小实际占用多少个字节，在内存中都是以 4 个长度 (32 位)
         *
         *  protobuf 序列化机制：
         *  是按照实际的数据大小去动态伸缩的, 因此很多时候我们的 int 数据并没有实际占用到4个字节
         *  所以 protobuf 序列化后一般都会比 int 类型(java 序列化机制)的占用长度要小很多！
         */
        for (byte bt : bytes.toByteArray()) {
            log.info("{}", bt);
        }

        UserProto.User userRever = UserProto.User.parseFrom(bytes);
        log.info("{}", userRever);

    }

}
