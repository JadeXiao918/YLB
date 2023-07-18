package com.ylb.api.service;

import com.ylb.api.model.IncomeRecord;

import java.util.List;

public interface IncomeService {
    //收益计划
    void generateIncomePlan();
    //收益返还
    void generateIncomeBack();
    //查询某个用户的收益记录
    List<IncomeRecord> queryByUid(Integer uid, Integer pageNo, Integer pageSize);
}
