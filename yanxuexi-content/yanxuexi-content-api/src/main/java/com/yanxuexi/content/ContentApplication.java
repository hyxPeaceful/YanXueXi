package com.yanxuexi.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author hyx
 * @version 1.0
 * @description 课程内容微服务启动类
 * @date 2024-06-15 16:19
 **/
@EnableOpenApi
@SpringBootApplication(scanBasePackages = {"com.yanxuexi.content", "com.yanxuexi.messagesdk", "com.yanxuexi.base"})
@EnableFeignClients(basePackages={"com.yanxuexi.content.feignclient"})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
