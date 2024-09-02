package com.yanxuexi.ucenter.feignClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Service;

/**
 * @author hyx
 * @version 1.0
 * @description 验证码微服务熔断降级处理方法
 * @date 2024-09-01 22:42
 **/
@Slf4j
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable cause) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.error("调用验证码微服务发什么熔断异常: {}", cause.getMessage());
                return null;
            }
        };
    }
}
