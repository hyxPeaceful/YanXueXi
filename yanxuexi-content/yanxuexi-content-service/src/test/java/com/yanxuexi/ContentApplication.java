package com.yanxuexi;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author hyx
 * @version 1.0
 * @description 课程内容微服务启动类
 * @date 2024-06-15 16:19
 **/
@SpringBootApplication
@EnableFeignClients(basePackages={"com.yanxuexi.content.feignclient"})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
