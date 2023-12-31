package com.ylb;


import com.ylb.task.TaskManager;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


// Generated by https://start.springboot.io
// 优质的 spring/boot/data/security/cloud 框架中文文档尽在 => https://springdoc.cn
@EnableDubbo
@EnableScheduling
@SpringBootApplication
public class MicrTaskApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MicrTaskApplication.class, args);
        TaskManager tm = (TaskManager) ctx.getBean("taskManager");
        //tm.invokeGenerateIncomePlan();
        //tm.invokeGenerateIncomeBack();
        tm.invokeKuaiQianQuery();
    }

}
