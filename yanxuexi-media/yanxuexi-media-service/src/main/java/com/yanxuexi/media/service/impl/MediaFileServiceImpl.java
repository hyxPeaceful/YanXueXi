package com.yanxuexi.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.media.mapper.MediaFilesMapper;
import com.yanxuexi.media.model.dto.QueryMediaParamsDto;
import com.yanxuexi.media.model.dto.UploadFileParamsDto;
import com.yanxuexi.media.model.dto.UploadFileResultDto;
import com.yanxuexi.media.model.po.MediaFiles;
import com.yanxuexi.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Date;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理服务实现
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    // 循环依赖在 spring boot 2.7版本已不推荐使用，运行时会报错，使用懒加载解决循环依赖问题
    // 注入自身，保证引用的是代理对象
    @Autowired
    @Lazy
    MediaFileService mediaFileServiceProxy;

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket.files}")
    String filesBucket;

    @Value("${minio.bucket.videofiles}")
    String videoFilesBucket;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    // 根据扩展名获取 mimeType 文件类型
    private String getMimeType(String extension) {
        extension = Optional.ofNullable(extension).orElse("");
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        // 字节流，通用mimeType
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 上传文件到 MinIo
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      文件类型
     * @param bucket        桶
     * @param objectName    对象名
     * @return 是否上传成功
     */
    private boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs objectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            // 上传文件
            minioClient.uploadObject(objectArgs);
            log.debug("上传文件到minio成功, bucket:{}, objectName:{}", bucket, objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            log.error("上传文件到minio出错, bucket:{}, objectName:{}, 错误原因:{}", bucket, objectName, e.getMessage(), e);
        }
        return false;
    }

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date()) + "/";
    }

    //获取文件的md5
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fileInputStream);
        } catch (Exception e) {
            log.error("文件{}Md5值获取失败", file.getName(), e);
            return null;
        }
    }

    /**
     * 将文件信息添加到文件表中
     *
     * @param companyId           机构 Id
     * @param fileMd5             文件Md5值
     * @param uploadFileParamsDto 上传文件参数
     * @param bucket              桶
     * @param objectName          对象名
     * @return 文件信息
     */
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        // 根据文件Md5值，也即文件信息表Id值，查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        // 若为空，则新增文件信息
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");
            int insert = mediaFilesMapper.insert(mediaFiles);
            int i = 1 / 0;
            if (insert <= 0) {
                log.error("文件信息保存到数据库失败, {}", mediaFiles);
                return null;
            }
            log.debug("文件信息保存到数据库成功,{}", mediaFiles);
        }
        return mediaFiles;
    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        // 上传文件到MinIo
        // 文件名
        String fileName = uploadFileParamsDto.getFilename();
        // 文件扩展名
        String extension = fileName.substring(fileName.lastIndexOf("."));
        // 根据扩展名得到文件类型
        String mimeType = getMimeType(extension);
        // 文件默认存储目录
        String defaultFolderPath = getDefaultFolderPath();
        // 文件Md5值
        File file = new File(localFilePath);
        String fileMd5 = getFileMd5(file);
        // 文件对象名（包含目录）
        String objectName = defaultFolderPath + fileMd5 + extension;
        // 上传文件
        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, filesBucket, objectName);
        if (!result) {
            YanXueXiException.cast("上传文件到文件系统失败");
        }
        // 保存文件信息到数据库，使用代理对象调用，确保事务生效
        MediaFiles mediaFiles = mediaFileServiceProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, filesBucket, objectName);
        if (mediaFiles == null) {
            YanXueXiException.cast("文件信息保存失败");
        }
        // 准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }
}
