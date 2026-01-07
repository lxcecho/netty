package io.netty.tomcat.servlet;

import io.netty.tomcat.http.NettyRequest;
import io.netty.tomcat.http.NettyResponse;
import io.netty.tomcat.http.NettyServlet;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 10:28 29-10-2022
 */
public class NettyFirstServlet extends NettyServlet {

    @Override
    public void doGet(NettyRequest request, NettyResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(NettyRequest request, NettyResponse response) throws Exception {
        response.write("This is First Serlvet");
    }

}
