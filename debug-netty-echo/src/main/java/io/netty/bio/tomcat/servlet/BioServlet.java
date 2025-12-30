package io.netty.bio.tomcat.servlet;

import io.netty.bio.tomcat.http.BioRequest;
import io.netty.bio.tomcat.http.BioResponse;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 9:47 29-10-2022
 */
public abstract class BioServlet {

    /**
     * 由 service() 方法决定是调用  doGet 还是 doPost 方法
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void service(BioRequest request, BioResponse response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    public abstract void doGet(BioRequest request, BioResponse response) throws Exception;

    public abstract void doPost(BioRequest request, BioResponse response) throws Exception;

}
