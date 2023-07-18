package com.ylb.dataservice.service;

import com.ylb.api.pojo.BaseInfo;
import com.ylb.api.service.PlatBaseInfoService;
import com.ylb.dataservice.mapper.BidInfoMapper;
import com.ylb.dataservice.mapper.ProductInfoMapper;
import com.ylb.dataservice.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;

@DubboService(interfaceClass = PlatBaseInfoService.class,version = "1.0")
public class PlatBaseInfoServiceImpl implements PlatBaseInfoService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ProductInfoMapper productInfoMapper;
    @Resource
    private BidInfoMapper bidInfoMapper;
    //平台基本信息
    @Override
    public BaseInfo queryPlatBaseInfo() {
        //获取注册人数
        int registerUser = userMapper.selectCountUser();
        //收益率平均值
        BigDecimal avgRate = productInfoMapper.selectAvgRate();
        //累计成交金额
        BigDecimal sumBidMoney = bidInfoMapper.selectSumBidMoney();
        BaseInfo baseInfo = new BaseInfo(avgRate,sumBidMoney,registerUser);
        return baseInfo;
    }
}
