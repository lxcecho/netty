package com.lxcecho.bio.tomcat.http;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 9:49 29-10-2022
 */
@Slf4j
public class BioRequest {

    private String method;

    private String url;

    public BioRequest(InputStream in) {
        try {
            // 拿到 HTTP 协议内容
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;
            if ((len = in.read(buff)) > 0) {
                content = new String(buff, 0, len);
            }
            // 报文格式：
//            GET /favicon.ico HTTP/1.1
//            Host: localhost:8090
//            Connection: keep-alive
//            sec-ch-ua: "Google Chrome";v="111", "Not(A:Brand";v="8", "Chromium";v="111"
//            sec-ch-ua-mobile: ?0
//            User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36
//            sec-ch-ua-platform: "Windows"
//            Accept: image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8
//            Sec-Fetch-Site: same-origin
//            Sec-Fetch-Mode: no-cors
//            Sec-Fetch-Dest: image
//            Referer: http://localhost:8090/secondServlet.do
//            Accept-Encoding: gzip, deflate, br
//            Accept-Language: en,zh-CN;q=0.9,zh;q=0.8


            String line = content.split("\\n")[0]; // 以换行分割，得到：GET /favicon.ico HTTP/1.1
            String[] arr = line.split("\\s"); // 按空格拆分，请求行会被拆成：[GET, /favicon.ico, HTTP/1.1]

            this.method = arr[0];
            // 先拿到路径：/secondServlet.do，再去掉 GET 请求里的查询参数，如：GET /test.do?name=lxcecho&age=18 HTTP/1.1
            this.url = arr[1].split("\\?")[0];
            log.info("content: {}\n", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

}
