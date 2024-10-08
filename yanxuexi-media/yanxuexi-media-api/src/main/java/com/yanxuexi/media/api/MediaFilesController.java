package com.yanxuexi.media.api;

import com.yanxuexi.media.model.dto.QueryMediaParamsDto;
import com.yanxuexi.media.model.dto.UploadFileParamsDto;
import com.yanxuexi.media.model.dto.UploadFileResultDto;
import com.yanxuexi.media.model.po.MediaFiles;
import com.yanxuexi.media.service.MediaFileService;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.time.LocalDateTime;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId, pageParams, queryMediaParamsDto);
    }

    @ApiOperation("上传文件接口")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto uploadFile(@RequestPart("filedata") MultipartFile multipartFile,
                                          @RequestParam(value= "objectName",required=false) String objectName) throws IOException {
        // 机构Id 暂时固定
        Long companyId = 1232141425L;

        // 上传文件基本信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        // 文件名称
        uploadFileParamsDto.setFilename(multipartFile.getOriginalFilename());
        // 文件大小
        uploadFileParamsDto.setFileSize(multipartFile.getSize());
        // 文件类型
        uploadFileParamsDto.setFileType("001001");

        // 获取文件Md5值，保证多线程同时创建临时文件时不会出现名称冲突
        InputStream inputStream = multipartFile.getInputStream();
        String md5Hex = DigestUtils.md5Hex(inputStream);
        // 创建一个临时文件
        File tempFile = File.createTempFile("minio" + md5Hex, ".temp");
        multipartFile.transferTo(tempFile);
        // 临时文件路径
        String localFilePath = tempFile.getAbsolutePath();

        return mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath, objectName);
    }
}
