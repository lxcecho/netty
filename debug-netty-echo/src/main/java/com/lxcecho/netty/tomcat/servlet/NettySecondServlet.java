package com.lxcecho.netty.tomcat.servlet;

import com.lxcecho.netty.tomcat.http.NettyRequest;
import com.lxcecho.netty.tomcat.http.NettyResponse;
import com.lxcecho.netty.tomcat.http.NettyServlet;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 10:28 29-10-2022
 */
public class NettySecondServlet extends NettyServlet {
    @Override
    public void doGet(NettyRequest request, NettyResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(NettyRequest request, NettyResponse response) throws Exception {
        response.write("This is First Serlvet");
    }
}
