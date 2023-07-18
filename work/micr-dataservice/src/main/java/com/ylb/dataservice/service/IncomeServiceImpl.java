package com.ylb.dataservice.service;

import com.ylb.api.model.BidInfo;
import com.ylb.api.model.IncomeRecord;
import com.ylb.api.model.ProductInfo;
import com.ylb.api.service.IncomeService;
import com.ylb.common.constants.YLBConstant;
import com.ylb.common.util.CommonUtil;
import com.ylb.dataservice.mapper.BidInfoMapper;
import com.ylb.dataservice.mapper.FinanceAccountMapper;
import com.ylb.dataservice.mapper.IncomeRecordMapper;
import com.ylb.dataservice.mapper.ProductInfoMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageReader;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = IncomeService.class,version = "1.0")
public class IncomeServiceImpl implements IncomeService {
    @Resource
    private ProductInfoMapper productInfoMapper;
    @Resource
    private BidInfoMapper bidInfoMapper;
    @Resource
    private IncomeRecordMapper incomeRecordMapper;
    @Resource
    private FinanceAccountMapper financeAccountMapper;
    //收益计划
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateIncomePlan() {
        //获取要处理的理财产品记录
        Date currentDate = new Date();
        Date beginTime = DateUtils.truncate(DateUtils.addDays(currentDate,-1), Calendar.DATE);;
        Date endTime = DateUtils.truncate(currentDate,Calendar.DATE);
        List<ProductInfo> productInfoList = productInfoMapper.selectFullTimeProducts(beginTime,endTime);
        BigDecimal income = null;
        BigDecimal dayRate = null;
        BigDecimal cycle = null;//周期
        Date incomeDate = null;//到期时间
        int rows = 0;
        for (ProductInfo product:productInfoList){
            //日利率
            dayRate = product.getRate().divide(new BigDecimal("360"),10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("100"),10,RoundingMode.HALF_UP);
            if (product.getProductType() == YLBConstant.PRODUCT_TYPE_XINSHOUBAO){//天为单位
                cycle = new BigDecimal(product.getCycle());
                incomeDate = DateUtils.addDays(product.getProductFullTime(),(1+product.getCycle()));
            }else {
                cycle = new BigDecimal(product.getCycle() * 30);
                incomeDate = DateUtils.addDays(product.getProductFullTime(),(1+product.getCycle() * 30));
            }

            List<BidInfo> bidList = bidInfoMapper.selectByProId(product.getId());
            //计算每笔投资的利息和到期时间
            for (BidInfo bid : bidList){
                //利息 = 本金 * 周期 * 利率
                income = bid.getBidMoney().multiply(cycle).multiply(dayRate);
                //创建收益记录
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bid.getId());
                incomeRecord.setBidMoney(bid.getBidMoney());
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeStatus(YLBConstant.INCOME_STATUS_PLAN);
                incomeRecord.setProdId(product.getId());
                incomeRecord.setIncomeMoney(income);
                incomeRecord.setUid(bid.getUid());
                incomeRecordMapper.insertSelective(incomeRecord);
            }
            //更新产品状态
            rows = productInfoMapper.updateStatus(product.getId(),YLBConstant.PRODUCT_STATUS_PLAN);
            if (rows < 1){
                throw new RuntimeException("生成收益计划，更新产品状态为2失败");
            }
        }
    }
    //收益返还
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void generateIncomeBack() {
        //获取要处理的到期的收益记录
        Date curDate = new Date();
        Date expiredDate = DateUtils.truncate(DateUtils.addDays(curDate,-1),Calendar.DATE);
        System.out.println("expiredDate = " + expiredDate);
        //到期的收益记录
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectExpiredIncome(expiredDate);
        int rows = 0;
        //把每个收益进行返还，本金+利息
        for (IncomeRecord ir : incomeRecordList){
            rows = financeAccountMapper.updateAvailableMoneyByIncomeBack(ir.getUid(),ir.getBidMoney(),ir.getIncomeMoney());
            if (rows < 1){
                throw new RuntimeException("收益返还，更新账户资金失败");
            }
            //更新收益记录的状态为1
            ir.setIncomeStatus(YLBConstant.INCOME_STATUS_BACK);
            rows = incomeRecordMapper.updateByPrimaryKey(ir);
            if (rows < 1){
                throw new RuntimeException("收益返还，更新收益记录的状态失败");
            }
        }
    }

    @Override
    public List<IncomeRecord> queryByUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<IncomeRecord> records = new ArrayList<>();
        if (uid != null && uid > 0){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            records = incomeRecordMapper.selectByUid(uid,pageNo,pageSize);

        }
        return records;
    }
}
