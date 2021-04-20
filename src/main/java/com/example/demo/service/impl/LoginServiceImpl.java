package com.example.demo.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.demo.co.LoginUser;
import com.example.demo.co.shiro.AppShiroUser;
import com.example.demo.co.shiro.UserContext;
import com.example.demo.config.jwt.JwtProperties;
import com.example.demo.config.shiro.CustomToken;
import com.example.demo.constants.StatusCode;
import com.example.demo.constants.interfaces.DemoConstants;
import com.example.demo.constants.interfaces.KeyPrefixConstants;
import com.example.demo.constants.interfaces.RegexConstants;
import com.example.demo.constants.interfaces.SecurityConstants;
import com.example.demo.exception.ZKCustomException;
import com.example.demo.form.user.SaveUserForm;
import com.example.demo.service.LoginService;
import com.example.demo.service.UserService;
import com.example.demo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *    登陆服务实现类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/11 17:06
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Result<UserContext> login(LoginUser user, HttpServletRequest request, HttpServletResponse response) {
        //用邮箱也可登录
        if (user.getUsername().matches(RegexConstants.EMAIL)){
            //根据邮箱获取用户名
            LoginUser loginUser = userService.qryUserByEmail(user.getUsername());
            if (Objects.nonNull(loginUser)){
                user.setUsername(loginUser.getUsername());
            }
        }
        //获取验证码的key
        String codeKey = KeyPrefixConstants.LOGIN_CODE + user.getUsername();
        //验证码校验
        boolean checkVerificationCode = SendEmailUtil.checkVerificationCode(user.getCode(), codeKey, jwtProperties.isVerificationCode());
        if (!checkVerificationCode){
            return Result.handleFailure("登录失败！验证码不正确或已失效！");
        }
        //账号密码校验
        Boolean checkPassWord = checkPassWord(user.getUsername(), user.getPassword());
        if (checkPassWord){
            //登录成功设置缓存
            loginSuccess(user.getUsername(), request, response);
        }else{
            return Result.handleFailure("登录失败，密码不正确！");
        }
        //登录次数校验
        limitLogin(user.getUsername());
        //返回数据
        return Result.ok().setMsg("登陆成功").setData(UserContext.getCurrentUser()).setDescription("UserContext 信息");
    }


    /**
     * 校验密码
     * @param username:用户名
     * @param password:密码
     * @return 校验结果
     */
    private Boolean checkPassWord(String username, String password) {
        //根据用户名查询用户登陆信息
        LoginUser loginUser = userService.qryUserByUsername(username);
        if (Objects.isNull(loginUser)){
            return Boolean.FALSE;
        }
        String passwordFromDB = loginUser.getPassword();
        //密码比对
        boolean verify = MD5SaltUtil.verify(password, passwordFromDB);
        return verify;
    }

    /**
     * 每日登陆限制，若超出限制则抛出异常
     * @param username ： 用户名
     * */
    private void limitLogin(String username){
        //keyPrefix：存入redis计数器key
        String keyPrefix = KeyPrefixConstants.LOGIN_COUNT+username;
        //limitPeriod：间隔多少秒进行访问
        int limitPeriod = DateTimeUtil.getSeconds();
        //获取存入redis的str
        String str = (String)redisTemplate.opsForValue().get(keyPrefix);
        //允许最大登陆的次数
        int limtTimeMax = DemoConstants.LIMIT_TIME_MAX;
        //redis存放时间与计数器map
        Map<String,Object> result = (Map<String,Object>) JSONUtil.parseObj(str);
        //获取当前时间
        String dateTime = DateUtil.offsetSecond(DateUtil.date(), limitPeriod).toString();
        //获取当前请求HttpServletRequest
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取机器ip
        String localip = RequestIpUtils.getIpAddr(request);
        //时间范围内第一次进入计数器重置
        Integer currentCount = 1;
        //查询当前计数
        Map map = new HashMap();
        map.put("openTime", dateTime);
        String currentTime = new DateTime().toString();
        map.put("currentTime",currentTime);
        map.put("limitPeriod",limitPeriod);
        map.put("ip",localip);
        if (StrUtil.isEmpty(str)){
            map.put("currentCount", currentCount);
            redisTemplate.opsForValue().set(keyPrefix, JSONUtil.toJsonStr(map),limitPeriod, TimeUnit.SECONDS);
        }else {
            //查询当前计数++
            currentCount = (Integer)result.get("currentCount");
            currentCount++;
            map.put("currentCount", currentCount);
            if (currentCount > limtTimeMax){
                //当前解锁倒计时
                long between = DateUtil.between(DateUtil.parse( result.get("openTime").toString()), DateUtil.parse(currentTime), DateUnit.SECOND);
                //转化为秒，一般倒计时以秒展现较为合适
                String formatBetween = DateUtil.formatBetween(between * 1000, BetweenFormatter.Level.SECOND);
                throw new ZKCustomException(StatusCode.FAILURE.getCode(),String.format("登入次数超出限制，上次登陆时间："+result.get("currentTime")+"，登陆ip："+result.get("ip")+"，距离解锁："+formatBetween));
            }else {
                redisTemplate.opsForValue().set(keyPrefix, JSONUtil.toJsonStr(map),limitPeriod, TimeUnit.SECONDS);
            }
        }
        AppShiroUser currentUser = UserContext.getCurrentUser();
        currentUser.setLastLoginTime(currentTime);
        currentUser.setTodayLoginCount(currentCount);
        //重置登录次数与登录时间
        UserContext userContext = new UserContext(currentUser);
        log.info("用户正常登陆, 当前已登入{}次",currentCount);
    }

    /**
     * 刷新token
     *
     * @return 刷新结果
     */
    @Override
    public Result<String> refreshToken() {
        try {
            String accessToken = UserContext.getCurrentUser().getAccessToken();
            //获取当前token的账号信息
            String account = JwtUtil.getClaim(accessToken, SecurityConstants.ACCOUNT);
            String refreshTokenCacheKey = SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + account;
            //判断Redis中RefreshToken是否存在
            String currentTimeMillsRedis  = (String)redisTemplate.opsForValue().get(refreshTokenCacheKey);
            if (StrUtil.isNotEmpty(currentTimeMillsRedis)){
                //相比如果一致，进行AccessToken刷新
                accessToken = accessToken.replace(SecurityConstants.TOKEN_PREFIX,"");;
                if (accessToken.equals(currentTimeMillsRedis)){
                    //设置RefreshToken中的时间戳为当前最新时间戳
                    String currentTime = String.valueOf(System.currentTimeMillis());
                    Integer refreshTokenExpireTime = jwtProperties.getTokenExpireTime();
                    //刷新AccessToken为当前最新时间戳
                    accessToken = JwtUtil.sign(account, currentTime);
                    redisTemplate.opsForValue().set(refreshTokenCacheKey,accessToken,refreshTokenExpireTime, TimeUnit.MINUTES);
                    return Result.ok().setMsg("token 刷新成功").setData(accessToken);
                }
            }
        }catch (Exception e){
            log.info("刷新token异常:{}",e.getMessage());
        }
        return Result.handleFailure("当前用户的token无效或已过期!");
    }

    /**
     * 根据用户名获取token信息
     *
     * @param username 用户名
     * @return token
     */
    @Override
    public Result<String> getTokenByAccount(String username) {
        String tokenKey = SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + username;
        String token = (String) redisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isBlank(token)){
            return Result.handleFailure(String.format("用户 %s token已失效!",username));
        }
        return Result.handleSuccess(token);
    }

    /**
     * 发送登录验证码(邮箱)
     *
     * @param username ：用户名
     * @return 验证码
     */
    @Override
    public Result<String> sendLoginCode(String username) {

        //根据用户名获取用户信息
        LoginUser loginUser = userService.qryUserByUsername(username);
        if (Objects.isNull(loginUser) || Objects.isNull(loginUser.getEmail())){
            return Result.handleSuccess("发送个空气 0.0 ");
        }
        //获取验证码的key
        String codeKey = KeyPrefixConstants.LOGIN_CODE + username;
        //获取验证码
        String randomNum = SendEmailUtil.getRandomNum(codeKey,loginUser.getEmail(),5);
        log.info("验证码：{}",randomNum);
        return Result.handleSuccess("验证码已发送！");
    }

    /**
     * 发送登录验证码(邮箱)
     *
     * @param email ：邮箱
     * @return 验证码
     */
    @Override
    public Result<String> sendLoginCodeByEmail(String email) {
        if (Objects.isNull(email)){
            return Result.handleSuccess("发送个空气 0.0 ");
        }
        //获取验证码的key
        String codeKey = KeyPrefixConstants.LOGIN_CODE + email;
        //获取验证码
        String randomNum = SendEmailUtil.getRandomNum(codeKey, email,5);
        log.info("验证码：{}",randomNum);
        return Result.handleSuccess("验证码已发送！");
    }

    /**
     * 注册
     *
     * @param userForm ：用户提交表单
     * @return 注册结果
     */
    @Override
    public Result<String> register(SaveUserForm userForm) {
        //查询用户名是否重复
        LoginUser user = userService.qryUserByUsername(userForm.getUsername());
        if (Objects.nonNull(user)){
            return Result.failure().setMsg("系统已存在该账号，请重新输入");
        }
        //查询手机号是否重复
        user = userService.qryUserByPhone(userForm.getPhone());
        if (Objects.nonNull(user)){
            return Result.failure().setMsg("系统已存在该手机号，请重新输入");
        }
        //查询邮箱是否重复
        user = userService.qryUserByEmail(userForm.getEmail());
        if (Objects.nonNull(user)){
            return Result.failure().setMsg("系统已存在该邮箱，请重新输入");
        }
        //获取验证码的key
        String codeKey = KeyPrefixConstants.LOGIN_CODE + userForm.getEmail();
        //校验邮箱验证码,邮箱可做用户名，必须校验
        boolean checkVerificationCode = SendEmailUtil.checkVerificationCode(userForm.getCode(), codeKey, true);
        if (!checkVerificationCode){
            return Result.failure().setMsg("邮箱验证码校验不通过！");
        }
        //密码加密
        userForm.setPassword(MD5SaltUtil.encrypt(userForm.getPassword()));
        //入库
        boolean saveUser = userService.saveUser(userForm);
        if (saveUser){
            return Result.ok().setMsg("注册成功");
        }
        return Result.failure().setMsg("注册失败");
    }


    /**
     * 登录后更新缓存，生成token，设置响应头部信息
     * @param account
     * @param response
     */
    private void loginSuccess(String account, HttpServletRequest request, HttpServletResponse response){
        //初始化token
        String token = null;
        //先查询数据库中的token信息
        String tokenKey = SecurityConstants.PREFIX_SHIRO_REFRESH_TOKEN + account;
        token = (String)redisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isEmpty(token) || !JwtUtil.verify(token)) {
            //替换为:
            //获取当前时间
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            //生成token
            token = JwtUtil.sign(account, currentTimeMillis);
            //写入header
            response.setHeader(SecurityConstants.REQUEST_AUTH_HEADER, SecurityConstants.TOKEN_PREFIX+token);
            response.setHeader(SecurityConstants.ACCESS_CONTROL_EXPOSE, SecurityConstants.REQUEST_AUTH_HEADER);
            //将token存入缓存
            redisTemplate.opsForValue().set(tokenKey,token,jwtProperties.getRefreshCheckTime(),TimeUnit.MINUTES);
        }
        //用户认证信息
        Subject subject = SecurityUtils.getSubject();
        subject.login(new CustomToken(token));
        AppShiroUser appShiroUser = new AppShiroUser(account,token, RequestIpUtils.getIpAddr(request));
        new UserContext(appShiroUser);
    }

}
