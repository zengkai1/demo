package com.example.demo.config.springmvc;

import com.example.demo.util.HttpHelper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;

/**
 * <p>
 *  重写HttpServletRequestWrapper类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/15 10:48
 */
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public MyHttpServletRequestWrapper(HttpServletRequest request) throws IOException{
        super(request);
        body = HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }
}
