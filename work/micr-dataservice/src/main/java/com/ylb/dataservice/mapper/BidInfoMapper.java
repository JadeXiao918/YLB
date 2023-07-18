package com.ylb.dataservice.mapper;

import com.ylb.api.model.BidInfo;
import com.ylb.api.pojo.BidInfoProduct;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidInfoMapper {
    //累计成交金额
    BigDecimal selectSumBidMoney();
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);
    //某个产品的投资记录
    List<BidInfoProduct> selectByProductId(@Param("productId") Integer productId, @Param("offset") int offset, @Param("rows") Integer rows);
    //某个产品的投资记录
    List<BidInfo> selectByProId(@Param("productId") Integer productId);

    //查询用户投资记录
    List<BidInfo> selectByUid(@Param("uid") Integer uid, @Param("offset") int offset, @Param("rows") Integer rows);
}