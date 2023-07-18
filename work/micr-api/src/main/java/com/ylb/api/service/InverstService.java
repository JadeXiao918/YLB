package com.ylb.api.service;

import com.ylb.api.model.BidInfo;
import com.ylb.api.model.RechargeRecord;
import com.ylb.api.pojo.BidInfoProduct;

import java.math.BigDecimal;
import java.util.List;

public interface InverstService {
    //查询某个产品的投资记录
    List<BidInfoProduct> queryBidListByProductId(Integer productId,
                                                 Integer pageNo,
                                                 Integer pageSize);
    //投资理财产品
    int investProduct(Integer uid, Integer productId, BigDecimal money);
    //查询某个用户的投资记录
    List<BidInfo> queryByUid(Integer uid, Integer pageNo, Integer pageSize);
}
