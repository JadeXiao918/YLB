package com.ylb.dataservice.service;

import com.ylb.api.model.RechargeRecord;
import com.ylb.api.service.RechargeService;
import com.ylb.common.constants.YLBConstant;
import com.ylb.common.util.CommonUtil;
import com.ylb.dataservice.mapper.FinanceAccountMapper;
import com.ylb.dataservice.mapper.RechargeRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {
    @Resource
    private RechargeRecordMapper rechargeRecordMapper;
    @Resource
    private FinanceAccountMapper financeAccountMapper;
    //根据userId查询充值记录
    @Override
    public List<RechargeRecord> queryByUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<RechargeRecord> records = new ArrayList<>();
        if (uid != null && uid > 0){
            pageNo = CommonUtil.defaultPageNo(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            records = rechargeRecordMapper.selectByUid(uid,offset,pageSize);
        }
        return records;
    }

    @Override
    public int addRechargeRecord(RechargeRecord record) {
        return rechargeRecordMapper.insertSelective(record);
    }
    //处理后续充值
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int handleKQNotify(String orderId, String payAmount, String payResult) {
        //查询订单
        RechargeRecord record = rechargeRecordMapper.selectByRechargeNo(orderId);
        int result = 0;
        int rows = 0;
        if (record != null){
            if (record.getRechargeStatus() == YLBConstant.RECHARGE_STATUS_PROCESSING){
                //判断金额是否一致
                String fen = record.getRechargeMoney().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
                if (fen.equals(payAmount)){
                    //金额一致
                    if ("10".equals(payResult)){
                        //成功
                        rows = financeAccountMapper.updateAvailableMoneyByRecharge(record.getUid(),record.getRechargeMoney());
                        if (rows < 1){
                            throw new RuntimeException("充值更新资金账户失败");
                        }
                        //更新充值记录状态
                        rows = rechargeRecordMapper.updateStatus(record.getId(),YLBConstant.RECHARGE_STATUS_SUCCESS);
                        if (rows < 1){
                            throw new RuntimeException("充值更新充值记录状态失败");
                        }
                        result = 1;//成功
                    }else {
                        //充值失败
                        //更新充值记录状态
                        rows = rechargeRecordMapper.updateStatus(record.getId(),YLBConstant.RECHARGE_STATUS_FAIL);
                        if ( rows < 1){
                            throw new RuntimeException("充值更新充值记录状态失败");
                        }
                        result = 2;//充值结果失败
                    }
                }else {
                    result = 4;//金额不一致
                }
            }else {
                result = 3;//订单已经处理过了
            }
        }
        return result;
    }
}
