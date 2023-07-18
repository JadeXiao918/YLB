package com.ylb.dataservice.service;

import com.ylb.api.model.BidInfo;
import com.ylb.api.model.FinanceAccount;
import com.ylb.api.model.ProductInfo;
import com.ylb.api.model.RechargeRecord;
import com.ylb.api.pojo.BidInfoProduct;
import com.ylb.api.pojo.UserAccountInfo;
import com.ylb.api.service.InverstService;
import com.ylb.common.constants.YLBConstant;
import com.ylb.common.util.CommonUtil;
import com.ylb.dataservice.mapper.BidInfoMapper;
import com.ylb.dataservice.mapper.FinanceAccountMapper;
import com.ylb.dataservice.mapper.ProductInfoMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@DubboService(interfaceClass = InverstService.class,version = "1.0")
public class InvestServiceImpl implements InverstService {
    @Resource
    private BidInfoMapper bidInfoMapper;
    @Resource
    private FinanceAccountMapper accountMapper;
    @Resource
    private ProductInfoMapper productInfoMapper;
    @Override
    public List<BidInfoProduct> queryBidListByProductId(Integer productId, Integer pageNo, Integer pageSize) {
        List<BidInfoProduct> bidList = new ArrayList<>();
        if(productId != null && productId > 0){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            bidList = bidInfoMapper.selectByProductId(productId,offset,pageSize);
        }
        return bidList;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int investProduct(Integer uid, Integer productId, BigDecimal money) {
        int result = 0;//参数不正确
        int rows = 0;
        //参数检查
        if ((uid != null && uid > 0) && (productId != null && productId > 0) && (money != null && money.intValue() % 100 ==0 && money.intValue() >= 100)){
            //查询账号金额
            FinanceAccount account = accountMapper.selectByUidForUpdate(uid);
            if (account != null){
                if (CommonUtil.ge(account.getAvailableMoney(),money)){
                    //资金满足购买的要求
                    //检查产品是否可以购买
                    ProductInfo productInfo = productInfoMapper.selectByPrimaryKey(productId);
                    if (productInfo != null && productInfo.getProductStatus() == YLBConstant.PRODUCT_STATUS_SELLING){
                        if (CommonUtil.ge(productInfo.getProductMoney(),money) && CommonUtil.ge(money,productInfo.getBidMinLimit())
                        && CommonUtil.ge(productInfo.getBidMaxLimit(),money)){
                            //可以购买 扣除账户资金
                            rows = accountMapper.updateAvailableMoneyByInvest(uid,money);
                            if (rows < 1){
                                throw new RuntimeException("投资更新账号资金失败");
                            }
                            //扣除产品剩余可投资金额
                            rows = productInfoMapper.updateLeftProductMoney(productId,money);
                            if (rows < 1){
                                throw  new RuntimeException("投资更新产品剩余金额失败");
                            }
                            //创建投资记录
                            BidInfo bidInfo = new BidInfo();
                            bidInfo.setBidMoney(money);
                            bidInfo.setBidStatus(YLBConstant.INVEST_STATUS_SUCC);
                            bidInfo.setBidTime(new Date());
                            bidInfo.setProdId(productId);
                            bidInfo.setUid(uid);
                            bidInfoMapper.insertSelective(bidInfo);
                            //判断产品是否卖完，更新产品是满标状态
                            ProductInfo dbProductInfo = productInfoMapper.selectByPrimaryKey(productId);
                            if (dbProductInfo.getLeftProductMoney().compareTo(new BigDecimal("0")) == 0){
                                rows = productInfoMapper.updateSelled(productId);
                                if (rows < 1){
                                    throw new RuntimeException("投资更新产品满标失败");
                                }
                            }
                            //投资成功
                            result = 1;
                        }
                    }else {
                        result = 4;//理财产品不存在
                    }
                }else {
                    result = 3;//资金不足
                }
            }else {
                result = 2;//资金账户不存在
            }
        }
        return result;
    }

    @Override
    public List<BidInfo> queryByUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<BidInfo> records = new ArrayList<>();
        if (uid != null & uid > 0){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize  =CommonUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            records = bidInfoMapper.selectByUid(uid,offset,pageSize);
        }
        return records;
    }
}
