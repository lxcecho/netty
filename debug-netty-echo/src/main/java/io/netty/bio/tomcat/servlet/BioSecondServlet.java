package io.netty.bio.tomcat.servlet;

import io.netty.bio.tomcat.http.BioRequest;
import io.netty.bio.tomcat.http.BioResponse;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 9:56 29-10-2022
 */
public class BioSecondServlet extends BioServlet {
    @Override
    public void doGet(BioRequest request, BioResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(BioRequest request, BioResponse response) throws Exception {
        response.write("This is Second Servlet");
    }
}
