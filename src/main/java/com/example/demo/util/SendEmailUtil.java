package com.example.demo.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *    发送邮件工具类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2021年4月20日
 */
@Slf4j
@UtilityClass
public class SendEmailUtil {

    private RedisTemplate redisTemplate = ContextUtil.getContext().getBean(RedisTemplate.class);

    /**
     * 向邮箱发送验证码邮件
     * @param randomNum ： 验证码
     * @param email ： 手机号
     */
    private void sendLoginCode4Email(String randomNum, String email, Integer expireTime) {
        //标题
        String subject = "登录验证码";
        //内容
        String context =  "【个人测试】您的验证码发送成功，验证码为["+randomNum+ "]，邮件来自zengkaiの测试，请在"+expireTime+"分钟内进行操作，超时验证码即失效，若非本人登录，请忽略本信息。";
        String send = MailUtil.send(CollUtil.newArrayList(email), subject, context, false);
        log.info("发送邮件结果：{},验证码：{}", send, randomNum);
    }

    /**
     * 随机生成六位随机数验证码
     * @param codeKey 验证码key
     * @param email 邮箱
     * @param expireTime 验证码过期时间/分钟
     * @return : 六位数字验证码
     */
    public String getRandomNum(String codeKey , String email,Integer expireTime){
        //默认超时时间为5分钟
        if (Objects.isNull(expireTime)){
            expireTime = 5;
        }
        RedisTemplate redisTemplate = ContextUtil.getContext().getBean(RedisTemplate.class);
        //如果已经有验证码，则删除当前验证码
        redisTemplate.delete(codeKey);
        //产生(0,999999]之间的随机数
        Integer randNum = (int)(Math.random()* (999999)+1);
        //进行六位数补全
        String randomCode = String.format("%06d",randNum);
        //发送邮件
        SendEmailUtil.sendLoginCode4Email(randomCode, email, expireTime);
        //将验证码存在缓存里进行比对
        redisTemplate.opsForValue().set(codeKey,randomCode,expireTime, TimeUnit.MINUTES);
        return randomCode;
    }

    /**
     * 验证码是否通过校验
     * @param code 验证码
     * @param codeKey codekey
     * @param enable 是否启用
     * @return boolean 是否通过校验
     */
    public boolean checkVerificationCode(String code, String codeKey, boolean enable){
        //如果未开启验证码校验
        if (!enable){
            return true;
        }
        //获取redis里的验证码
        String codeCache = (String)redisTemplate.opsForValue().get(codeKey);
        //如果查询不到或不匹配
        if (StrUtil.isBlank(codeCache) || !codeCache.equals(code)){
            return false;
        }
        //删除验证码缓存
        redisTemplate.delete(codeKey);
        return true;
    }

}
