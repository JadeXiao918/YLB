package com.ylb.api.service;

import com.ylb.api.pojo.BaseInfo;

public interface PlatBaseInfoService {
    /**
     * 计算利率，注册人数，累计成交金额
     */
    BaseInfo queryPlatBaseInfo();

}
