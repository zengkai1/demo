package com.example.demo.config.shiro;

import com.example.demo.config.jwt.JwtFilter;
import com.example.demo.config.jwt.SystemLogoutFilter;
import com.google.common.collect.Lists;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AllSuccessfulStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

/**
 * <p>
 *  shiro配置
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:16
 */
@Configuration
public class ShiroConfig {

    /**
     * @desc 过滤器注册
     * @param filter
     * @return
     */
    @Bean
    public FilterRegistrationBean<SystemLogoutFilter> shiroCheckSessionRegistration(SystemLogoutFilter filter) {
        FilterRegistrationBean<SystemLogoutFilter> registration = new FilterRegistrationBean<SystemLogoutFilter>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public static DefaultAdvisorAutoProxyCreator getLifecycleBeanPostProcessor(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        //强制使用cglib
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 权限管理，配置主要是Realm的管理认证
     * @param loginRealm
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(LoginRealm loginRealm,CustomRealm customRealm){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
       List<Realm> realmList = Lists.newArrayList();
       // realmList.add(loginRealm);
        realmList.add(customRealm);
        securityManager.setRealms(realmList);
        //关闭shiro自带的session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
    }


    /**
     * Filter工厂，设置对应的过滤条件和跳转条件
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> map = new LinkedHashMap<>();
        //放行Swagger2页面，需要放行这些
        map.put("/swagger-ui.html/**","anon");
        map.put("/swagger/**","anon");
        map.put("/map/**", "anon");
        map.put("/swagger-resources/**","anon");
        map.put("/v2/**","anon");
        map.put("/static/**", "anon");
        map.put("/webjars/**", "anon");
        map.put("/doc.html", "anon");
        //解除用户登陆限制
        map.put("/unfreezeByUsername/**","anon");
        //忽略列表，“anon”：url可以匿名进行访问
        map.put("/mongo/**","anon");
        map.put("/register","anon");
        map.put("/login","anon");
        map.put("/getTokenByAccount/**","anon");
        //对所有用户认证，“authc”：url必须经过认证通过才可以访问
        map.put("/logout", "logout");
        map.put("/**", "jwt");
        //登录
        //shiroFilterFactoryBean.setLoginUrl("/login");
        //首页
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 添加jwt过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt",jwtFilter());
        filterMap.put("logout",logoutFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }

    @Bean
    public SystemLogoutFilter logoutFilter(){
        return new SystemLogoutFilter();
    }


    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }


    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public Authenticator authenticator( ) {
        MyModularRealmAuthenticator authenticator = new MyModularRealmAuthenticator();
        authenticator.setRealms(Arrays.asList(/*new LoginRealm(),*/ new CustomRealm()));
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }
}
