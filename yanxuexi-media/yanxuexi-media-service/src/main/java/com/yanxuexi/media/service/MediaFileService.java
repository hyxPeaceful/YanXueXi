package com.yanxuexi.media.service;

import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.base.model.RestResponse;
import com.yanxuexi.media.model.dto.QueryMediaParamsDto;
import com.yanxuexi.media.model.dto.UploadFileParamsDto;
import com.yanxuexi.media.model.dto.UploadFileResultDto;
import com.yanxuexi.media.model.po.MediaFiles;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 检测文件是否存在
     * @param fileMd5 文件Md5值
     * @return 文件是否已存在
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检测文件分块是否存在
     * @param fileMd5 文件Md5值
     * @param chunkIndex 文件分块序号
     * @return 文件分块是否存在
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传文件分块
     * @param fileMd5 文件Md5值
     * @param chunkIndex 分块序号
     * @param localChunkFilePath 分块文件存储临时路径
     */
    public RestResponse<Boolean> uploadFileChunk(String fileMd5, int chunkIndex, String localChunkFilePath);

    /**
     * @description 合并分块
     * @param companyId  机构id
     * @param fileMd5  文件md5
     * @param chunkTotal 分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.yanxuexi.base.model.RestResponse
     */
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);
}
