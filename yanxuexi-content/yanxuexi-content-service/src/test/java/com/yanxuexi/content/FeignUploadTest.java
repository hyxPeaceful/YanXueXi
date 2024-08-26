package com.yanxuexi.content;

import com.yanxuexi.content.config.MultipartSupportConfig;
import com.yanxuexi.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author hyx
 * @version 1.0
 * @description 测试feign远程调用媒资服务文件上传接口
 * @date 2024-08-25 11:40
 **/
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\Develop\\test\\fileUpload\\test.html"));
        mediaServiceClient.uploadFile(multipartFile,"course/" + "test.html");
    }
}
