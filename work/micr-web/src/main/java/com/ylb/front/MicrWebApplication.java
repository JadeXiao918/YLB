package com.ylb.front;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.ylb.common.util.JwtUtil;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@EnableSwaggerBootstrapUI
@EnableSwagger2
@EnableDubbo
@SpringBootApplication

public class MicrWebApplication {
    @Value("${jwt.secret}")
    private String secretKey;
    //创建JwtUtil
    @Bean
    public JwtUtil jwtUtil(){
        JwtUtil jwtUtil = new JwtUtil(secretKey);
        return jwtUtil;
    }
    public static void main(String[] args) {
        SpringApplication.run(MicrWebApplication.class, args);
    }

}
