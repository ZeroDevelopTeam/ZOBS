package com.zero.logic.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token的生成，验证，销毁
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/12
 */
@Configuration
public class TokenUtil {
    private static int TOKEN_EXPIRES_MINUTES = 24; //默认为24*60（24小时）
    @Autowired
    private static RedisTemplate redisTemplate;
    @Autowired
    public void setRedis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        //泛型设置成String后必须更改对应的序列化方案
        redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
    }

    /**
     * 生成token并且以<userCode,token>形式保存到redis
     * @param userCode
     * @return
     */
    public static void createAndSaveToken(String userCode){

        Properties propes = ReadProperties.getPropes("/application.properties");
        if ("".equals(propes.getProperty("TOKEN_EXPIRES_MINUTES")));
         TOKEN_EXPIRES_MINUTES = Integer.parseInt(propes.getProperty("uploadFilePath"));
        //原token
        String token = UUID.randomUUID().toString();
        //将token用MD5加密并以<key,value>的形式缓存到redis
        redisTemplate.boundValueOps(userCode).set(MD5Util.getMd5(userCode,token),TOKEN_EXPIRES_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 根据userCode获取token
     * @param userCode
     * @return
     */
    public static String getToken(String userCode){
        System.out.println(redisTemplate.opsForValue().get("token")+">>>>>>>>>>>>>>>>>");
        String token = "";
        if (redisTemplate.opsForValue().get("token")==null ){
            System.out.println(redisTemplate.opsForValue().get("token"));
        }
        if (redisTemplate.opsForValue().get("token")!=null){
            System.out.println(redisTemplate.opsForValue().get("token").toString()+">>>>>>>>gggggg>>>>>>>>>");
            token = redisTemplate.opsForValue().get("token").toString();
        }
        return token;
    }
    /**
     * 校验token
     * @param userode
     * @param token
     * @return true/false
     */
    public static boolean checkToken(String userode,String token){
        token = MD5Util.getMd5(userode,token);
        if (token.equals(redisTemplate.boundValueOps(userode).get())){
            //校验成功延长token过期时间
            redisTemplate.boundValueOps(userode).expire(TOKEN_EXPIRES_MINUTES,TimeUnit.MINUTES);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 根据userCode删除redis里的token
     * @param userCode
     */
    public static void deleteToken(String userCode){
        redisTemplate.delete(userCode);
    }
}
