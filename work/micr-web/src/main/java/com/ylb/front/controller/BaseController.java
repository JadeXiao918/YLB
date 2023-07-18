package com.ylb.front.controller;

import com.ylb.api.service.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

public class BaseController {
    //声明公共的方法，属性等
    @DubboReference(interfaceClass = PlatBaseInfoService.class,version = "1.0")
    protected PlatBaseInfoService platBaseInfoService;
    @DubboReference(interfaceClass = ProductService.class,version = "1.0")
    protected ProductService productService;
    @Resource
    protected StringRedisTemplate stringRedisTemplate;
    @DubboReference(interfaceClass = InverstService.class,version = "1.0")
    protected InverstService inverstService;
    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    protected UserService userService;
    @DubboReference(interfaceClass = RechargeService.class,version = "1.0")
    protected RechargeService rechargeService;
    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    protected IncomeService incomeService;
}
