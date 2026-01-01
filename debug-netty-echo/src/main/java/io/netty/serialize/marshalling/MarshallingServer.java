package io.netty.serialize.marshalling;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 2026/1/1
 */
@Slf4j
public class MarshallingServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bGroup = new NioEventLoopGroup(1);
        EventLoopGroup wGroup = new NioEventLoopGroup();

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bGroup, wGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        sc.pipeline().addLast(new MarshallingServerHandler());
                    }
                });

        ChannelFuture cf = sb.bind(8765).sync();

        cf.channel().closeFuture().sync();
        bGroup.shutdownGracefully();
        wGroup.shutdownGracefully();
    }

    static class MarshallingServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 接受 request 请求 并进行业务处理
            RequestData rd = (RequestData) msg;
            System.err.println("id: " + rd.getId() + ", name: " + rd.getName() + ", requestMessage: " + rd.getRequestMessage());

            byte[] attachment = GzipUtils.ungzip(rd.getAttachment());

            String path = System.getProperty("user.dir")
                    + File.separatorChar + "receive" + File.separatorChar + "001.jpg";

            FileOutputStream fos = new FileOutputStream(path);
            fos.write(attachment);
            fos.close();

            //	回送相应数据
            ResponseData responseData = new ResponseData();
            responseData.setId("response " + rd.getId());
            responseData.setId("response " + rd.getName());
            responseData.setResponseMessage("响应信息");

            ctx.writeAndFlush(responseData);
        }
    }
}
