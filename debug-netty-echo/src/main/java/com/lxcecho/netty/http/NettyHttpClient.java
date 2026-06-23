package com.lxcecho.netty.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/2
 */
public class NettyHttpClient {
    public static void main(String[] args) throws InterruptedException,
            URISyntaxException,
            UnsupportedEncodingException,
            HttpPostRequestEncoder.ErrorDataEncoderException {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 创建线程组，这个是 Netty 除去 BossGroup 和 workerGroup 以外，还可以对 handler 去使用异步线程池去执行
        DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                4,
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();

                            // 注意看这里，就是使用了上面的线程组去处理一个个加入到 pipeline 中的 handler
                            p.addLast(defaultEventExecutorGroup,
                                    // 接收 Http 请求，必须要使用下面三个对应的 codec、ObjectAggregator、ExpectContinueHandler 处理器
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(1024 * 1024),
                                    new HttpServerExpectContinueHandler(),
                                    new NettyHttpClientHandler());
                        }
                    });

            Channel channel = bootstrap.connect("127.0.0.1", 8888).sync().channel();

            // 在客户端与服务器端建立连接之后，我们可以发送 http get 请求 和 http post 请求
            //	http get
            sendHttpGet(channel);

            // http post
//            sendHttpPost(channel);

            channel.closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 发送 http get 请求
     *
     * @param channel
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    private static void sendHttpGet(Channel channel) throws URISyntaxException, InterruptedException {
        // 请求地址
        URI uri = new URI("http://127.0.0.1:8888");
        // 使用 DefaultFullHttpRequest 类包装请求信息，比如协议版本、协议类型、请求路径和请求内容
        String requestBody = "hello server";
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                uri.toASCIIString(),
                Unpooled.wrappedBuffer(requestBody.getBytes(StandardCharsets.UTF_8)));

        // 可以在 httpRequest.headers 中设置各种需要的信息。
        httpRequest.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
        httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
        httpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);

        // 使用 channel 发送 httpRequest 到 Server 端
        channel.writeAndFlush(httpRequest).sync();
    }

    /**
     * 发送 http post 请求
     *
     * @param channel
     * @throws HttpPostRequestEncoder.ErrorDataEncoderException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    private static void sendHttpPost(Channel channel) throws HttpPostRequestEncoder.ErrorDataEncoderException, URISyntaxException {

        // 请求地址
        URI uri = new URI("http://127.0.0.1:8888");

        // 这里我们也可以使用 HttpDataFactory 工厂类去创建请求，Disk if MIN_SIZE exceed
        HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
        // 预编译（组织）Http 请求对象 Prepare the HTTP request.
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                uri.toASCIIString());

        // 对请求内容进行编码，使用到 HttpDataFactory, Use the PostBody encoder
        HttpPostRequestEncoder bodyRequestEncoder =
                // 注意最后一个参数为 false 则代表 => not multipart
                new HttpPostRequestEncoder(factory, request, false);

        // 添加请求体内容
        bodyRequestEncoder.addBodyAttribute("param1", "lxcecho");
        bodyRequestEncoder.addBodyAttribute("param2", "GOGOGO");

        // 可以给 body 添加文件等
        // bodyRequestEncoder.addBodyFileUpload("myfile", file, "application/x-zip-compressed", false);
        // it is legal to add directly header or cookie into the request until finalize


        // 这部分是对请求头的设置 headers，比如我们可以设置常见的请求头信息
        request.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);

        // 发送请求，send request
        channel.writeAndFlush(request);
    }

}
