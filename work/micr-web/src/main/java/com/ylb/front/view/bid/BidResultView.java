package com.ylb.front.view.bid;

import com.ylb.api.model.BidInfo;
import com.ylb.api.model.RechargeRecord;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;

public class BidResultView {
    private Integer id;
    private String result = "未知";
    private String bidDate = "-";
    private BigDecimal bidMoney;
    public BidResultView(BidInfo record) {
        this.id = record.getId();
        this.bidMoney = record.getBidMoney();
        if (record.getBidTime() != null){
            bidDate = DateFormatUtils.format(record.getBidTime(),"yyyy-MM-dd");
        }
        switch (record.getBidStatus()){
            case 0:
                result = "投资失败";
                break;
            case 1:
                result = "投资成功";
                break;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBidDate() {
        return bidDate;
    }

    public void setBidDate(String bidDate) {
        this.bidDate = bidDate;
    }

    public BigDecimal getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(BigDecimal bidMoney) {
        this.bidMoney = bidMoney;
    }
}
