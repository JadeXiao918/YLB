package com.ylb.front.service;

public interface SmsService {
    //发送短信
    boolean sendSms(String phone);

    boolean checkSmsCode(String phone,String code);
}
