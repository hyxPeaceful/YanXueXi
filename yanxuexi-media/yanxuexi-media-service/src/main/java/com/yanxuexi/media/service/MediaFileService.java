package com.yanxuexi.media.service;

import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.media.model.dto.QueryMediaParamsDto;
import com.yanxuexi.media.model.dto.UploadFileParamsDto;
import com.yanxuexi.media.model.dto.UploadFileResultDto;
import com.yanxuexi.media.model.po.MediaFiles;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @param companyId           机构Id
     * @param uploadFileParamsDto 文件信息
     * @param localFilePath       文件本地路径
     * @return 文件信息
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * 保存文件信息到数据库
     * @param companyId     机构Id
     * @param fileMd5  文件Md5值
     * @param uploadFileParamsDto 上传文件参数
     * @param bucket 桶
     * @param objectName 对象名称
     * @return 文件信息
     */
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);
}
