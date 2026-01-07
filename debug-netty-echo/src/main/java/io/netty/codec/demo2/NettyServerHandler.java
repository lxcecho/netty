package io.netty.codec.demo2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 说明
 * 1. 我们自定义一个Handler 需要继续netty 规定好的某个HandlerAdapter(规范)
 * 2. 这时我们自定义一个Handler , 才能称为一个handler
 */
@Slf4j
//public class NettyServerHandler extends ChannelInboundHandlerAdapter {
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {

    /**
     * 读取数据实际(这里我们可以读取客户端发送的消息)
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道 pipeline , 通道 channel, 地址
     * 2. Object msg: 就是客户端发送的数据 默认Object
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {
        // 根据dataType 来显示不同的信息
        MyDataInfo.MyMessage.DataType dataType = msg.getDataType();
        if (dataType == MyDataInfo.MyMessage.DataType.StudentType) {

            MyDataInfo.Student student = msg.getStudent();
            log.info("学生id={} 学生名字={}", student.getId(), student.getName());

        } else if (dataType == MyDataInfo.MyMessage.DataType.WorkerType) {
            MyDataInfo.Worker worker = msg.getWorker();
            log.info("工人的名字={} 年龄={}", worker.getName(), worker.getAge());
        } else {
            log.error("传输的类型不正确");
        }
    }


    /**
     * 读取数据实际(这里我们可以读取客户端发送的消息)
     * 1. ChannelHandlerContext ctx:上下文对象, 含有 管道pipeline , 通道channel, 地址
     * 2. Object msg: 就是客户端发送的数据 默认Object
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 读取从客户端发送的StudentPojo.Student

        StudentPOJO.Student student = (StudentPOJO.Student) msg;

        log.info("客户端发送的数据 id={} 名字=", student.getId(), student.getName());
    }*/

    /**
     * 数据读取完毕，读取完客户端数据后回复客户端
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        // writeAndFlush 是 write + flush
        // 将数据写入到缓存，并刷新
        // 一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵1", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常, 一般是需要关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常时关闭 ctx，ctx 是相关信息的汇总，关闭它其它的也就关闭了。
        ctx.close();
    }
}
