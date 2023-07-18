package com.ylb.front.settings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurationSettings {
    @Bean
    public Docket docket() {
        //创建Docket对象
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        //创建Api信息，接口文档的总体描述
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("盈利宝")
                .version("1.0")
                .description("前后端分离的项目，前端Vue，后端Spring Boot + Dubbo分布式项目")
                .contact(new Contact("盈利宝", "2831886254@qq.com", "2831886254@qq.com"))
                .license("Apache 2.0")
                .build();
        //设置使用ApiInfo
        docket = docket.apiInfo(apiInfo);
        //设置参与文档生成的包
        docket = docket.select().apis(RequestHandlerSelectors.basePackage("com.ylb.front.controller")).build();
        return docket;
    }
}
