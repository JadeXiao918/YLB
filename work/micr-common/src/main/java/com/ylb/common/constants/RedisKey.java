package com.ylb.common.constants;

public class RedisKey {
    //投资排行榜
    public static final String KEY_INVEST_RANK = "INVEST:RANK";
    //注册时，短信验证码 SMS:REG:手机号
    public static final String KEY_KEY_CODE_REG = "SMS:REG";
    //登录时，短信验证码 SMS:LOGIN:手机号
    public static final String KEY_KEY_CODE_LOGIN = "SMS:LOGIN";
    //redis自增
    public static final String KEY_RECHARGE_ORDERID = "RECHARGE:ORDERID:SEQ";
    //order
    public static final String KEY_ORDERID_SET = "RECHARGE:ORDERID:SET";
}
