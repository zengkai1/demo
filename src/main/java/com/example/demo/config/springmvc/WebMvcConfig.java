package com.example.demo.config.springmvc;

import com.example.demo.config.shiro.ExcludeUrlPropertiesConfig;
import com.example.demo.interceptor.AuthorizationInterceptor;
import com.example.demo.interceptor.CustomInterceptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;
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

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    @Resource
    private ExcludeUrlPropertiesConfig urlPropertiesConfig ;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration registration = registry.addInterceptor(customInterceptor);
        //拦截以下路径
        registration.addPathPatterns("/**");
        //对List中的路径不进行拦截
        List<String> pathPatterns = Lists.newArrayList();
        List<String> excluded = urlPropertiesConfig.getExcluded();
        pathPatterns.addAll(excluded);
        registration.excludePathPatterns(pathPatterns);

        InterceptorRegistration authRegistration = registry.addInterceptor(authorizationInterceptor);
        authRegistration.addPathPatterns("/**");
        authRegistration.excludePathPatterns(pathPatterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
