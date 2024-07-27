package com.yanxuexi.media.config;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.service.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-07-27 19:29
 **/
@Configuration
@EnableOpenApi
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
                .title("媒资文件管理接口文档")
                .description("研学习项目媒资文件管理接口文档")
                .version("1.0")
                .build();
    }
}
