package io.netty.dubborpc.customer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lxcecho@gmail.com
 * @since 2026/1/10
 */
@Slf4j
public class NettyClient {

    // 创建线程池
    private static ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler client;
    private int count = 0;

    /**
     * 编写方法使用代理模式，获取一个代理对象
     *
     * @param serivceClass
     * @param providerName
     * @return
     */
    public Object getBean(final Class<?> serivceClass, final String providerName) {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            log.info("(proxy, method, args) 进入.... {} 次", (++count));
            // {} 部分的代码，客户端每调用一次 hello, 就会进入到该代码
            if (client == null) {
                initClient();
            }
            // 设置要发给服务器端的信息
            // providerName 协议头 args[0] 就是客户端调用 api hello(???), 参数
            client.setPara(providerName + args[0]);
            return executor.submit(client).get();
        };
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serivceClass}, invocationHandler);
    }

    /**
     * 初始化客户端
     */
    private static void initClient() {
        client = new NettyClientHandler();
        // 创建 EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new StringEncoder());
                                pipeline.addLast(client);
                            }
                        }
                );

        try {
            bootstrap.connect("127.0.0.1", 7000).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

        /*上下文*/
        private ChannelHandlerContext context;

        /*返回的结果*/
        private String result;

        /*客户端调用方法时，传入的参数*/
        private String para;

        /**
         * 与服务器的连接创建后，就会被调用, 这个方法是第一个被调用(1)
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info(" channelActive 被调用  ");
            context = ctx; // 因为我们在其它方法会使用到 ctx
        }

        /**
         * 收到服务器的数据后，调用方法 (4)
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info(" channelRead 被调用  ");
            result = msg.toString();
            notify(); // 唤醒等待的线程
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }

        /**
         * 被代理对象调用, 发送数据给服务器，-> wait -> 等待被唤醒(channelRead) -> 返回结果 (3)-》5
         *
         * @return
         * @throws Exception
         */
        @Override
        public synchronized Object call() throws Exception {
            log.info(" call1 被调用  ");
            context.writeAndFlush(para);
            // 进行 wait
            wait(); // 等待 channelRead 方法获取到服务器的结果后，唤醒
            log.info(" call2 被调用  ");
            return result; // 服务方返回的结果
        }

        // (2)
        void setPara(String para) {
            log.info(" setPara  ");
            this.para = para;
        }
    }
}
