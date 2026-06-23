package com.lxcecho.netty.codec.demo3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 23:06 10-11-2022
 */
@Slf4j
public class ProtoServerHandler extends SimpleChannelInboundHandler<DataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataInfo.MyMessage msg) throws Exception {
        DataInfo.MyMessage.DataType dataType = msg.getDataType();
        if (DataInfo.MyMessage.DataType.PersonType == dataType) {
            DataInfo.Person person = msg.getPerson();
            log.info("{}， {}， {}", person.getName(), person.getAddress(), person.getAge());
        } else if (DataInfo.MyMessage.DataType.DogType == dataType) {
            DataInfo.Dog dog = msg.getDog();
            log.info("{} {}", dog.getName(), dog.getAge());
        } else if (DataInfo.MyMessage.DataType.CatType == dataType) {
            DataInfo.Cat cat = msg.getCat();
            log.info("{} {}", cat.getName(), cat.getAge());
        }
    }
}
