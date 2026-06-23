package com.lxcecho.netty.codec.demo3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 23:06 10-11-2022
 */
@Slf4j
public class ProtoClientHandler extends SimpleChannelInboundHandler<DataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataInfo.MyMessage msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int n = (int) (1 + Math.random() * (3 - 1 + 1));
        log.info("n: {}", n);
        DataInfo.MyMessage myMessage = DataInfo.MyMessage.newBuilder().build();
        if (0 == n) {
            myMessage = DataInfo.MyMessage.newBuilder()
                    .setDataType(DataInfo.MyMessage.DataType.PersonType)
                    .setPerson(DataInfo.Person.newBuilder().setName("lxceco").setAge(18).setAddress("广西钦州").build())
                    .build();
        } else if (1 == n) {
            myMessage = DataInfo.MyMessage.newBuilder()
                    .setDataType(DataInfo.MyMessage.DataType.DogType)
                    .setDog(DataInfo.Dog.newBuilder().setName("Heman").setAge(18).build())
                    .build();
        } else if (2 == n) {
            myMessage = DataInfo.MyMessage.newBuilder()
                    .setDataType(DataInfo.MyMessage.DataType.CatType)
                    .setCat(DataInfo.Cat.newBuilder().setName("Eman").setAge(18).build())
                    .build();
        }
        ctx.channel().writeAndFlush(myMessage);
    }
}
