package com.example.demo.config.jwt;

import com.example.demo.constants.StatusCode;
import com.example.demo.util.Result;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 *  自定义系统登出拦截器
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/30 16:58
 */
public class SystemLogoutFilter extends LogoutFilter {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogoutFilter.class);

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        try {
            subject.logout();
        } catch (Exception ex) {
            logger.error("退出登录错误",ex);
        }
        //不执行后续的过滤器
        return true;
    }
/*
    private void writeResult(ServletResponse response){
        //响应Json结果
        PrintWriter out = null;
        try {
            out = response.getWriter();
            Result result = new Result(StatusCode.SUCCESS.getCode(),"退出登陆");
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            logger.error("返回Response信息出现IOException异常:" + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }*/

}
