package com.ylb.task;

import com.ylb.api.service.IncomeService;
import com.ylb.common.util.HttpClientUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component("taskManager")
public class TaskManager {
    /*@Scheduled(cron = "01 09 12 * * ?")
    public void testCron() {
        System.out.println("执行了定时任务方法:" + new Date());
    }
    */



    //生成收益计划
    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;
    @Scheduled(cron = "0 0 1 * * ?")
    public void invokeGenerateIncomePlan(){
        incomeService.generateIncomePlan();
    }
    //生成收益返还
    @Scheduled(cron = "0 0 2 * * ?")
    public void invokeGenerateIncomeBack(){
        incomeService.generateIncomeBack();
    }



    //补单接口
    @Scheduled(cron = "0 0/20 * * * ?")
    public void invokeKuaiQianQuery(){
        try {
            String url = "http://localhost:9000/pay/kq/rece/query";
            HttpClientUtils.doGet(url);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
