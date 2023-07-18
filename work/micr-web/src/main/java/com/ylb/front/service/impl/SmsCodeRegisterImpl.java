package com.ylb.front.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ylb.common.constants.RedisKey;
import com.ylb.front.config.JdwxSmsConfig;
import com.ylb.front.service.SmsService;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service(value = "smsCodeRegisterImpl")
public class SmsCodeRegisterImpl implements SmsService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JdwxSmsConfig smsConfig;
    @Override
    public boolean sendSms(String phone) {
        boolean send = false;
        //设置短信内容
        String random = RandomStringUtils.randomNumeric(4);
        System.out.println("注册验证码的随机数 random="+random);
        //更新content中的%s
        String content = String.format(smsConfig.getContent(), random);
        //使用HttpClient发送get，请求给第三方
        CloseableHttpClient client = HttpClients.createDefault();
        String url = smsConfig.getUrl()+"?mobile=" + phone
                +"&content=" + content
                +"&appkey="+smsConfig.getAppkey();
        HttpGet get = new HttpGet(url);
        try{
            CloseableHttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //得到返回的数据
                //String text = EntityUtils.toString(response.getEntity());
                String text = "{\n" +
                        "       \"code\": \"10000\",\n" +
                        "       \"charge\": false,\n" +
                        "       \"remain\": 1305,\n" +
                        "       \"msg\": \"查询成功\",\n" +
                        "       \"result\": {\n" +
                        "           \"ReturnStatus\": \"Success\",\n" +
                        "           \"Message\": \"ok\",\n" +
                        "           \"RemainPoint\":420842,\n" +
                        "           \"TaskID\": 18424321,\n" +
                        "           \"SuccessCounts\": 1\n" +
                        "      }\n" +
                        "}";
                //解析json
                if (StringUtils.isNotBlank(text)){
                    //fastjson
                    JSONObject jsonObject = JSONObject.parseObject(text);
                    if ("10000".equals(jsonObject.getString("code"))){
                        //读取result中的key:ReturnStatus
                        if ("Success".equalsIgnoreCase(jsonObject.getJSONObject("result").getString("ReturnStatus"))){
                            //短信发送成功
                            send = true;
                            //把短信验证码，存到redis
                            String key = RedisKey.KEY_KEY_CODE_REG + phone;
                            stringRedisTemplate.boundValueOps(key).set(random,1, TimeUnit.MINUTES);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return send;
    }

    @Override
    public boolean checkSmsCode(String phone, String code) {
        String key = RedisKey.KEY_KEY_CODE_REG + phone;
        if (stringRedisTemplate.hasKey(key)){
            String querySmsCode = stringRedisTemplate.boundValueOps(key).get();
            if (code.equals(querySmsCode)){
                return true;
            }
        }
        return false;
    }
}
