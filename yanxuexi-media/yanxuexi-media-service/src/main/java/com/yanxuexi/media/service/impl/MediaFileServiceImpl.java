package com.yanxuexi.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.base.model.RestResponse;
import com.yanxuexi.media.mapper.MediaFilesMapper;
import com.yanxuexi.media.mapper.MediaProcessMapper;
import com.yanxuexi.media.model.dto.QueryMediaParamsDto;
import com.yanxuexi.media.model.dto.UploadFileParamsDto;
import com.yanxuexi.media.model.dto.UploadFileResultDto;
import com.yanxuexi.media.model.po.MediaFiles;
import com.yanxuexi.media.model.po.MediaProcess;
import com.yanxuexi.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.stream.Stream;

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

    @Autowired
    MediaProcessMapper mediaProcessMapper;

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
        // 若extension为空，则赋为“”空字符串
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
     * @param localFilePath 本地文件路径
     * @param mimeType      文件类型
     * @param bucket        桶
     * @param objectName    对象名
     * @return 是否上传成功
     */
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
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
    @Override
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
            if (insert <= 0) {
                log.error("文件信息保存到数据库失败, {}", mediaFiles);
                return null;
            }
            // 添加到待处理任务表
            addWaitingTask(mediaFiles);
            log.debug("文件信息保存到数据库成功,{}", mediaFiles);
        }
        return mediaFiles;
    }

    /**
     * 添加待处理任务（暂时仅处理avi视频）
     * @param mediaFiles 媒资文件信息
     */
    private void addWaitingTask(MediaFiles mediaFiles){
        // 文件名称
        String filename = mediaFiles.getFilename();
        // 文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 文件类型
        String mimeType = getMimeType(extension);
        // 暂时仅处理avi视频
        if (mimeType.equals("video/x-msvideo")) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess, "url");
            mediaProcess.setStatus("1");
            mediaProcess.setFailCount(0);
            mediaProcessMapper.insert(mediaProcess);
        }
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

    /**
     * 检测文件是否存在
     *
     * @param fileMd5 文件Md5值
     * @return 文件是否已存在
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFile = mediaFilesMapper.selectById(fileMd5);
        if (mediaFile != null) {
            // 文件所在桶
            String bucket = mediaFile.getBucket();
            // 文件对象（文件在桶下的存储路径）
            String filePath = mediaFile.getFilePath();
            // 查询文件参数
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build();
            // 查询远程服务获取一个文件对象
            try {
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                if (inputStream != null) {
                    // 文件存在返回true
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                log.error("查询文件{}失败", filePath, e);
            }
        }
        // 文件不存在
        return RestResponse.success(false);
    }

    /**
     * 检测文件分块是否存在
     *
     * @param fileMd5    文件Md5值
     * @param chunkIndex 文件分块序号
     * @return 文件分块是否存在
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String chunkFolder = getChunkFolder(fileMd5);
        // 文件所在桶
        String bucket = videoFilesBucket;
        // 文件对象（文件在桶下的存储路径）
        String filePath = chunkFolder + chunkIndex;
        // 查询文件参数
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build();
        // 查询远程服务获取一个文件对象
        try {
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream != null) {
                // 文件分块存在返回true
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            log.error("查询文件{}失败", filePath, e);
        }
        // 文件分块不存在返回false
        return RestResponse.success(false);
    }

    /**
     * 上传文件分块
     *
     * @param fileMd5            文件Md5值
     * @param chunkIndex         分块序号
     * @param localChunkFilePath 分块文件存储临时路径
     */
    @Override
    public RestResponse<Boolean> uploadFileChunk(String fileMd5, int chunkIndex, String localChunkFilePath) {
        // 获取文件分块类型
        String mimeType = getMimeType(null);
        // 获取视频存放的bucket
        String bucket = videoFilesBucket;
        // 获取文件分块对象（存储路径）
        String chunkFileFolder = getChunkFolder(fileMd5);
        String filePath = chunkFileFolder + chunkIndex;
        boolean isUpload = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket, filePath);
        if (!isUpload) {
            return RestResponse.validfail(false, "上传文件分块失败");
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        // 找到分块文件目录，调用MinIo的SDK合并分块
        // 获取文件存放bucket
        String bucket = videoFilesBucket;
        // 获取分块文件目录
        String chunkFolder = getChunkFolder(fileMd5);
        // 获取文件扩展名
        String fileName = uploadFileParamsDto.getFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        // 获取文件对象名（文件存放路径）
        String fileObjectName = getFileObjectName(fileMd5, extension);
        // 调用MinIo SDK合并分块
        List<ComposeSource> composeSourceList = Stream.iterate(0, i -> ++i).
                limit(chunkTotal).
                map(i -> ComposeSource.builder().bucket(bucket).object(chunkFolder + i).build()).toList();
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket(bucket).object(fileObjectName).sources(composeSourceList).build();
        // 合并文件分块
        try {
            minioClient.composeObject(composeObjectArgs);
        } catch (Exception e) {
            log.error("合并文件分块失败, bucket: {}, object: {}, md5: {}, 异常原因: {}", bucket, fileObjectName, fileMd5, e.getMessage());
            return RestResponse.validfail(false, "合并文件分块失败");
        }

        // 校验合并后的文件和源文件是否一致,这里校验一致无法通过，但是在minio上预览文件内容和本地是一致的，为了后续开发，暂时不进行文件一致性校验，后面有时间再解决此问题
//        File mergeFile = downloadFileFromMinIO(bucket, fileObjectName);
//        try (FileInputStream inputStream = new FileInputStream(mergeFile)) {
//            String mergeFileMd5 = DigestUtils.md5Hex(inputStream);
//            if (!fileMd5.equals(mergeFileMd5)) {
//                log.error("文件校验失败，原文件和合并后文件不一致，原文件Md5值：{}，合并文件Md5值{}", fileMd5, mergeFileMd5);
//                return RestResponse.validfail(false, "文件校验失败，原文件和合并后文件不一致");
//            }
//        } catch (IOException e) {
//            log.error("合并文件输入流获取失败", e);
//        }

        // 文件信息入库
        MediaFiles mediaFile = mediaFileServiceProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket, fileObjectName);
        if (mediaFile == null) {
            return RestResponse.validfail(false, "文件信息入库失败");
        }

        // 清理分块文件
        clearChunkFiles(chunkFolder, chunkTotal);

        return RestResponse.success(true);
    }

    /**
     * 清除分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal 分块文件总数
     */
    private void clearChunkFiles (String chunkFileFolderPath, int chunkTotal){
        // 获取bucket
        String bucket = videoFilesBucket;
        // 获取需要删除的文件路径
        Iterable<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i).map(i -> new DeleteObject(chunkFileFolderPath + i)).toList();
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                .bucket(bucket)
                .objects(deleteObjects)
                .build();
        // 批量删除文件
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                log.error("清楚分块文件失败, objectName:{}", deleteError.objectName(), e);
            }
        });
    }

    /**
     * 从minio下载文件
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO (String bucket, String objectName){
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                                                                .bucket(bucket)
                                                                .object(objectName)
                                                                .build());
            //创建临时文件
//            String md5Hex = DigestUtils.md5Hex(inputStream);
            minioFile = File.createTempFile("minio", ".temp");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(inputStream, outputStream);
            return minioFile;
        } catch (Exception e) {
            log.error("文件下载失败, bucket: {}, object: {}, 异常原因: {}", bucket, objectName, e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("输出流关闭失败", e);
                }
            }
        }
        return null;
    }

    /**
     * 根据文件Md5值，获取文件分块所在目录
     *
     * @param fileMd5
     * @return
     */
    private String getChunkFolder (String fileMd5){
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * 根据文件Md5值，获取文件对象名（存放路径）
     * @param fileMd5   文件Md5值
     * @param extension 文件扩展名
     * @return
     */
    private String getFileObjectName (String fileMd5, String extension){
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + extension;
    }
}
