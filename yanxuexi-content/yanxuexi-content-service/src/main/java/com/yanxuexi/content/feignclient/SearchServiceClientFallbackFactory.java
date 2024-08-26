package com.yanxuexi.content.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hyx
 * @version 1.0
 * @description 搜索服务远程调用降级处理工厂
 * @date 2024-08-25 14:55
 **/
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable cause) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引发生熔断，索引信息：{}， 熔断异常：{}", courseIndex, cause.toString(), cause);
                return false;
            }
        };
    }
}
