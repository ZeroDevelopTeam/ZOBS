package com.zero.logic.controller;

import com.zero.logic.dao.UserDao;
import com.zero.logic.domain.User;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.ReadProperties;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 发送验证码类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/22
 */
@RestController
@RequestMapping("email")
public class SendVerifyCodeController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping(value = "/sendVerifyCode",method = RequestMethod.GET)
    @ApiOperation(value = "生成验证码",notes = "随机生成验证码发送到邮箱")
    public String sendVerifyCode(@RequestParam String userCode,@RequestParam String emailAddress){
        try {
            //获取验证码有效时间
            int CAPTCHA_EXPIRES_MINUTES=5;//验证码过期时间 默认五分钟，//如果配置文件有设置则使用配置文件的
            if ("".equals(ReadProperties.properties.getProperty("CAPTCHA_EXPIRES_MINUTES")));
            CAPTCHA_EXPIRES_MINUTES= Integer.parseInt(ReadProperties.properties.getProperty("CAPTCHA_EXPIRES_MINUTES"));
            //获取邮件模板配置信息
            Properties properties = ReadProperties.getPropes("/application.properties");
            String fromAddr = properties.getProperty("message.setFrom");//发件地址
            String verifyCode = String.valueOf((int)((Math.random()*9+1)*1000));//生成随机四位验证码
            SimpleMailMessage message = new SimpleMailMessage();
            //用户密码找回
            if ("".equals(emailAddress )|| emailAddress.length()<1){
                User oldUser = userDao.getUserByUserCode(userCode);
               // Timestamp outDate = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000);// 5分钟后过期
                //oldUser.setOutDate(outDate);
                //oldUser.setVerifyCode(verifyCode);

                //使用redis缓存以<key,value>形式存储 用户-验证码 信息
                redisTemplate.opsForValue().set(userCode, verifyCode,CAPTCHA_EXPIRES_MINUTES,TimeUnit.MINUTES);

                emailAddress = oldUser.getEmail();
                message.setSubject("主题：密码重置");
                message.setText("您的验证码是 "+verifyCode+"请勿泄露给他人");

                userDao.save(oldUser);

            }else {//用户注册校验邮箱
                //使用redis缓存以<key,value>形式存储 用户-验证码 信息
                redisTemplate.opsForValue().set(userCode, verifyCode,CAPTCHA_EXPIRES_MINUTES,TimeUnit.MINUTES);
                message.setSubject("主题：用户注册");
                message.setText("您的验证码是 "+verifyCode+"请勿泄露给他人");
            }

            Map map = new HashMap();
            map.put("verifyCode",verifyCode);
            message.setFrom(fromAddr);//发件箱
            message.setTo(emailAddress);//收件箱
            mailSender.send(message);
            String str = JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"验证码发送成功，注意查收邮件");
            return JsonUtil.makeJsonBeanByKey(str,map);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"发送失败");
        }
    }
}
