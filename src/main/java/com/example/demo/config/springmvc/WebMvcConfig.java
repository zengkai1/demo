package com.example.demo.config.springmvc;

import com.example.demo.interceptor.CustomInterceptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * <p>
 *  WebMVC配置
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/10 11:39
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CustomInterceptor customInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration registration = registry.addInterceptor(customInterceptor);
        //拦截以下路径
        registration.addPathPatterns("/**");
        //对List中的路径不进行拦截
        List<String> pathPatterns = Lists.newArrayList();
        pathPatterns.add("/index");
        pathPatterns.add("/mongo/qryMap");
        pathPatterns.add("/swagger-ui.html/**");
        pathPatterns.add("/swagger-resources/**");
        pathPatterns.add("/v2/**");
        pathPatterns.add("/webjars/**");
        pathPatterns.add("/login");
        pathPatterns.add("/logout");
        pathPatterns.add("/doc.html");
        registration.excludePathPatterns(pathPatterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
