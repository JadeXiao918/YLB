package com.ylb.front.view.income;

import com.ylb.api.model.IncomeRecord;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;

public class IncomeResultView {
    private Integer id;
    private String result = "未知";
    private String incomeDate;
    private BigDecimal incomeMoney;

    public IncomeResultView(IncomeRecord record) {
        this.id = record.getId();
        this.incomeMoney = record.getIncomeMoney();
        if (record.getIncomeDate() != null){
            incomeDate = DateFormatUtils.format(record.getIncomeDate(),"yyyy-MM-dd");
        }
        switch (record.getIncomeStatus()){
            case 0:
                result = "未返还";
                break;
            case 1:
                result = "已返还";
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

    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
    }

    public BigDecimal getIncomeMoney() {
        return incomeMoney;
    }

    public void setIncomeMoney(BigDecimal incomeMoney) {
        this.incomeMoney = incomeMoney;
    }
}
