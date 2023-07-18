package com.ylb.api.service;

import com.ylb.api.model.ProductInfo;
import com.ylb.api.pojo.MultiProduct;

import java.util.List;

public interface ProductService {
    //根据产品类型，查询产品，支持分页
    List<ProductInfo> queryByTypeLimit(Integer pType, Integer pageNo, Integer pageSize);
    //某个产品类型的记录总数
    Integer queryRecordNumsByType(Integer pType);
    //首页的多个产品数据
    MultiProduct queryIndexPageProducts();
    //根据产品id，查询产品信息
    ProductInfo queryById(Integer id);
}
