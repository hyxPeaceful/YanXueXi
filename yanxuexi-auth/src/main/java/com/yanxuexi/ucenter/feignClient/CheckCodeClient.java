package com.yanxuexi.ucenter.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-09-01 22:36
 **/
@FeignClient(value = "checkcode", fallbackFactory = CheckCodeClientFactory.class)
public interface CheckCodeClient {
    @PostMapping("checkcode/verify")
    Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);
}
