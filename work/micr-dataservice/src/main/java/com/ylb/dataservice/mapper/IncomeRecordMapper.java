package com.ylb.dataservice.mapper;

import com.ylb.api.model.IncomeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);
    //到期的收益记录
    List<IncomeRecord> selectExpiredIncome(@Param("expiredDate") Date expiredDate);

    List<IncomeRecord> selectByUid(@Param("uid") Integer uid, @Param("offset") Integer offset, @Param("rows") Integer rows);
}