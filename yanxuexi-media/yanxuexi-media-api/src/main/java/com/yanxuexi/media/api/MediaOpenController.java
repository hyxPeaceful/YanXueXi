package com.yanxuexi.media.api;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.base.model.RestResponse;
import com.yanxuexi.media.model.po.MediaFiles;
import com.yanxuexi.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyx
 * @version 1.0
 * @description 公开媒资信息查询接口
 * @date 2024-08-18 14:13
 **/
@Api(value = "媒资信息公开查询接口",tags = "媒资信息公开查询接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            YanXueXiException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
