package com.yanxuexi.content.feignclient;

import com.yanxuexi.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hyx
 * @version 1.0
 * @description 搜索服务远程调用客户端
 * @date 2024-08-25 11:34
 **/

@FeignClient(value = "search", fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {
    @PostMapping(value = "/search/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex);
}