package com.yanxuexi.media.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yanxuexi.base.utils.Mp4VideoUtil;
import com.yanxuexi.media.model.po.MediaProcess;
import com.yanxuexi.media.service.MediaFileService;
import com.yanxuexi.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * VideoJob 视频处理任务
 */
@Slf4j
@Component
public class VideoJob {
    @Autowired
    MediaProcessService mediaProcessService;

    @Autowired
    MediaFileService mediaFileService;

    // ffmpeg视频处理工具地址
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 分片广播视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() {
        // 分片参数
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();
        // 查询任务
        // 获取CPU核心数，通过CPU核心数确定一次处理任务数量，因为任务并行处理，且视频转码为CPU密集型任务，所以取得等于CPU核心数的任务执行效率最高
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        List<MediaProcess> mediaProcessList = mediaProcessService.getMediaProcessList(shardTotal, shardIndex, availableProcessors);
        // 取出的任务数量
        int size = mediaProcessList.size();
        log.debug("取出的视频转码任务数量: {}", size);
        if (size == 0) {
            return;
        }
        // 使用计算器，等待所有任务处理完成，方法返回
        CountDownLatch countDownLatch = new CountDownLatch(size);
        // 创建线程池，处理任务
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(() -> {
                try {
                    // 开启任务
                    // 任务Id
                    Long taskId = mediaProcess.getId();
                    // 源文件Md5值
                    String fileMd5 = mediaProcess.getFileId();
                    boolean isStartTask = mediaProcessService.startTask(taskId);
                    if (!isStartTask) {
                        log.error("开启任务失败，任务Id: {}", taskId);
                        return;
                    }
                    // 下载视频到本地
                    // 桶
                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    File downLoadFile = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (downLoadFile == null) {
                        log.error("视频下载失败, 任务Id: {}, bucket: {}, objectName: {}", taskId, bucket, objectName);
                        // 保存任务处理失败的结果
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileMd5, null, "视频下载失败");
                        return;
                    }
                    // 执行视频转码
                    // 源视频路径
                    String videoPath = downLoadFile.getAbsolutePath();
                    // 转换后视频名称
                    String translateVideoName = fileMd5 + ".mp4";
                    // 转换后视频路径
                    File translateFile = null;
                    try {
                        translateFile = File.createTempFile("temp", ".mp4");
                    } catch (IOException e) {
                        log.error("创建临时文件失败, {}", e.getMessage());
                        // 保存任务处理失败的结果
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileMd5, null, "创建临时文件失败");
                        return;
                    }
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegpath, videoPath, translateVideoName, translateFile.getAbsolutePath());
                    // 开始视频转换
                    String result = mp4VideoUtil.generateMp4();
                    if (!result.equals("success")) {
                        // 处理失败
                        log.error("视频转码失败, 原因: {}, bucket: {}, objectName: {}", result, bucket, objectName);
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileMd5, null, "视频转码失败");
                        return;
                    }
                    // 上传转码后视频到Minio
                    String newObjectName = getFilePath(fileMd5, ".mp4");
                    boolean isUpload = mediaFileService.addMediaFilesToMinIO(videoPath, "video/mp4", bucket, newObjectName);
                    if (!isUpload) {
                        // 转码后视频上传失败
                        log.error("视频转码后上传失败, taskId: {}", taskId);
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileMd5, null, "视频转码后上传失败");
                        return;
                    }
                    // 保存任务处理结果
                    mediaProcessService.saveProcessFinishStatus(taskId, "2", fileMd5, newObjectName, null);
                } finally {
                    // 线程处理完任务，计数器值减一，这里为了保证任务处理出现异常时也能正常将计数器值减一，将此操作放到finally代码块执行
                    countDownLatch.countDown();
                }
            });
        });
        try {
            countDownLatch.await(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("发生中断异常", e);
        }
    }

    /**
     * @description: 获取文件url
     * @param fileMd5 文件MD5值
     * @param fileExt 文件后缀
     * @return 文件url
     */
    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }
}
