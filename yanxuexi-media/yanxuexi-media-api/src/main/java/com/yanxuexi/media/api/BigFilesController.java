package com.yanxuexi.media.api;

import com.yanxuexi.base.model.RestResponse;
import com.yanxuexi.media.model.dto.UploadFileParamsDto;
import com.yanxuexi.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author hyx
 * @version 1.0
 * @description 大文件上传接口
 * @date 2024-08-03 17:33
 **/
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {
    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(
            @RequestParam("fileMd5") String fileMd5) {
        return mediaFileService.checkFile(fileMd5);
    }

    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunk") int chunkIndex) {
        return mediaFileService.checkChunk(fileMd5, chunkIndex);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunk") int chunkIndex) throws IOException {
        // 创建一个临时文件（除了创建临时文件有没有更好的方法？直接使用文件流？）
        File tempFile = File.createTempFile("minio" + fileMd5, ".temp");
        file.transferTo(tempFile);
        // 临时文件路径
        String localFilePath = tempFile.getAbsolutePath();

        return mediaFileService.uploadFileChunk(fileMd5, chunkIndex, localFilePath);
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergeChunks(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkTotal") int chunkTotal) {
        // 机构Id
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setTags("视频文件");
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType("001002");
        return mediaFileService.mergeChunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);
    }
}
