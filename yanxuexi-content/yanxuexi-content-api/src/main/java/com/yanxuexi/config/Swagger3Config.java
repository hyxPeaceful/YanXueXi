package com.yanxuexi.config;


import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.service.Contact;


/**
 * @author hyx
 * @version 1.0
 * @description swagger3 配置类
 * @date 2024-06-16 14:04
 **/
@EnableOpenApi
@Configuration
public class Swagger3Config {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(builderApiInfo())
                .select()
                // 扫描所有带有 @ApiOperation 注解的类
                .apis( RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 扫描所有的 controller
                // .apis(RequestHandlerSelectors.basePackage("com.shenlanbao.product.library.management.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo builderApiInfo() {
        return new ApiInfoBuilder()
                .contact(
                        new Contact(
                                "hyx",
                                "https://www.yanxuexi.com",
                                "hyx_peaceful@163.com"
                        )
                )
                .title("课程内容接口文档")
                .description("研学习项目课程内容管理接口文档")
                .version("1.0")
                .build();
    }
}

