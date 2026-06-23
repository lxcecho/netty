package com.lxcecho.bio.tomcat.servlet;

import com.lxcecho.bio.tomcat.http.BioRequest;
import com.lxcecho.bio.tomcat.http.BioResponse;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 9:56 29-10-2022
 */
public class BioFirstServlet extends BioServlet {


    @Override
    public void doGet(BioRequest request, BioResponse response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(BioRequest request, BioResponse response) throws Exception {
        response.write("This is First Servlet");
    }
}
