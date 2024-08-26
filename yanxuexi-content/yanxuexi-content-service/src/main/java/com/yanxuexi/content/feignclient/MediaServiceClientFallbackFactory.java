package com.yanxuexi.content.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hyx
 * @version 1.0
 * @description 微服务远程调用降级处理工厂
 * @date 2024-08-25 14:55
 **/
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable cause) {
        return new MediaServiceClient() {
            @Override
            public String uploadFile(MultipartFile multipartFile, String objectName) {
                log.error("调用媒资管理服务上传文件时发生熔断，异常信息：{}", cause.toString(), cause);
                return null;
            }
        };
    }
}
