package io.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

/**
 * 说明：
 * 1. SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
 * 2. HttpObject 客户端和服务端相互通讯的数据被封装成 HttpObject
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 08.10.2021
 */
@Slf4j
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    /*@Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        // 判断 msg 是不是 httpRequest 请求
        if(httpObject instanceof HttpRequest){
            log.info("msg类型：{}", httpObject.getClass());
            log.info("客户端地址：{}", channelHandlerContext.channel().remoteAddress());
            
            // 回复信息给浏览器【http 协议】
            ByteBuf content = Unpooled.copiedBuffer("你好 我是服务器", CharsetUtil.UTF_8);

            // 构造一个 HTTP 的响应，即 httpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 将构建好的 response 返回
            channelHandlerContext.writeAndFlush(response);
        }
    }*/

    /**
     * 4 读取客户端数据
     *
     * @param channelHandlerContext
     * @param httpObject
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        /*log.info("对应的 channel = {}, \npipeline = {}, \n通过 pipeline 获取 channel = {}",
                channelHandlerContext.channel(),
                channelHandlerContext.pipeline(),
                channelHandlerContext.pipeline().channel());

        log.info("当前 ctx 的 handler = {}", channelHandlerContext.handler());

        log.info("httpObject 类型：{}", httpObject.getClass());*/

        // 判断 msg 是不是 httpRequest 请求
        if (httpObject instanceof HttpRequest) {
            // pipeline 和 浏览器的关系
            /*log.info("ctx 类型：{}", channelHandlerContext.getClass());
            log.info("pipeline hashCode：{}, \nTestHttpServerHandler hash: {}",
                    channelHandlerContext.pipeline().hashCode(), this.hashCode());
            log.info("客户端地址：{}", channelHandlerContext.channel().remoteAddress());*/

            HttpRequest request = (HttpRequest) httpObject;

            // 获取到 uri，过滤掉指定资源
            URI uri = new URI(request.uri());
            if ("/favicon.ico".equals(uri.getPath())) { // 图标请求不做响应
                log.info("请求了 favicon.ico，不做响应");
                return;
            }

            HttpMethod method = request.method();
            HttpHeaders headers = request.headers();

            //	判断请求类型 get/post 做不同的逻辑处理
            if (HttpMethod.GET.equals(method)) {
                log.error("doGet ..");
            } else if (HttpMethod.POST.equals(method)) {
                log.error("doPost ..");
                //	post 请求得到 fullRequest 可以进行解析处理
                FullHttpRequest fullRequest = (FullHttpRequest) httpObject;
                String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
                contentTypeConverter(contentType);
            }

            sendResponse(channelHandlerContext, request);
        }
    }

    /**
     * 写出响应信息
     *
     * @param channelHandlerContext
     * @param request
     */
    private static void sendResponse(ChannelHandlerContext channelHandlerContext, HttpRequest request) {
        // 回复信息给浏览器，【http 协议】
        String responseBody = "Hello, I'm Server.";
        ByteBuf context = Unpooled.copiedBuffer(responseBody, CharsetUtil.UTF_8);
        // 构建一个 http 的响应，即 httpResponse
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                context);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        // 如果 keepAlive 是 true， 则保持练级金额，否则关闭连接
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (!keepAlive) {
            // 将都建好 response 返回
            channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            channelHandlerContext.writeAndFlush(response);
        }
    }

    /**
     * 根据不同的 contentType 可以做不同的数据转换
     *
     * @param contentType
     */
    private void contentTypeConverter(String contentType) {
        if (StringUtils.isNoneBlank(contentType)) {
            // 可以根据不同类型的 contentType 做相应处理
            if (HttpHeaderValues.APPLICATION_JSON.toString().equals(contentType)) {
                log.error("contentType is application/json");
            } else if (HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString().equals(contentType)) {
                log.error("contentType is application/x-www-form-urlencoded");
            }
            // else if (...)
        } else {
            log.warn("NettyHttpServerHandler#channelRead0: message contentType is null, contentType: {}", contentType);
        }
    }

    /**
     * 3 活动状态——上线
     * 当一个新的连接已经被建立时，ChannelHandler 的 channelActive() 回调方法将会被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
        super.channelActive(ctx);
    }

    /**
     * 2 注册
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
        super.channelRegistered(ctx);
    }

    /**
     * 5 非活跃状态——下线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive");
        super.channelInactive(ctx);
    }

    /**
     * 6 注销
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelUnregistered");
        super.channelUnregistered(ctx);
    }

    /**
     * 1 添加
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded");
        super.handlerAdded(ctx);
    }

    /**
     * 7 移除
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved");
        super.handlerRemoved(ctx);
    }
}
