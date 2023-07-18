package com.ylb.front.controller;

import com.ylb.common.constants.RedisKey;
import com.ylb.common.enums.RCode;
import com.ylb.common.util.CommonUtil;
import com.ylb.front.service.SmsService;
import com.ylb.front.view.RespResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "短信业务")
@RestController
@RequestMapping("v1/sms")
public class SmsController extends BaseController{
    @Resource(name = "smsCodeRegisterImpl")
    private SmsService smsService;
    @Resource(name = "smsCodeLoginImpl")
    private SmsService loginSmsService;
    //发送注册验证码短信
    @GetMapping("/code/register")
    public RespResult sendCodeRegister(@RequestParam String phone){
        RespResult result = RespResult.fail();
        if (CommonUtil.checkPhone(phone)){
            //判断redis中是否有这个手机号的验证
            String key = RedisKey.KEY_KEY_CODE_REG + phone;
            if (stringRedisTemplate.hasKey(key)){
                result = RespResult.ok();
                result.setRCode(RCode.SMS_CODE_CAN_USE);
            }else {
                boolean isSuccess = smsService.sendSms(phone);
                if (isSuccess){
                    result = RespResult.ok();
                }
            }
        } else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }
    //发送登录验证码短信
    @GetMapping("/code/login")
    public RespResult sendCodeLogin(@RequestParam String phone){
        RespResult result = RespResult.fail();
        if (CommonUtil.checkPhone(phone)){
            //判断redis中是否有这个手机号的验证
            String key = RedisKey.KEY_KEY_CODE_LOGIN + phone;
            if (stringRedisTemplate.hasKey(key)){
                result = RespResult.ok();
                result.setRCode(RCode.SMS_CODE_CAN_USE);
            }else {
                boolean isSuccess = loginSmsService.sendSms(phone);
                if (isSuccess){
                    result = RespResult.ok();
                }
            }
        } else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }
}
