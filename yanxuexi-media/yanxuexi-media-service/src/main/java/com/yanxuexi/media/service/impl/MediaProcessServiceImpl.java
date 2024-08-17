package com.yanxuexi.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yanxuexi.media.mapper.MediaFilesMapper;
import com.yanxuexi.media.mapper.MediaProcessHistoryMapper;
import com.yanxuexi.media.mapper.MediaProcessMapper;
import com.yanxuexi.media.model.po.MediaFiles;
import com.yanxuexi.media.model.po.MediaProcess;
import com.yanxuexi.media.model.po.MediaProcessHistory;
import com.yanxuexi.media.service.MediaFileService;
import com.yanxuexi.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 媒资任务处理服务实现
 * @date 2024-08-11 16:24
 **/
@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    /**
     * 获取待处理任务列表
     *
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count      任务数
     * @return 待处理任务列表
     */
    @Override
    public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    /**
     * 开启一个任务实现
     * @param id 任务 Id
     * @return 是否开启成功
     */
    @Override
    public boolean startTask(long id) {
        int i = mediaProcessMapper.startTask(id);
        return i > 0;
    }

    /**
     * @description: 保存任务处理结果
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     */
    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 查询任务信息
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            log.error("任务处理结果保存失败，任务不存在");
            return;
        }
        // 任务处理失败，更新任务表
        if (status.equals("3")) {
            LambdaUpdateWrapper<MediaProcess> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(MediaProcess::getId, taskId)
                    .set(MediaProcess::getStatus, status)
                    .set(MediaProcess::getFailCount, mediaProcess.getFailCount() + 1)
                    .set(MediaProcess::getErrormsg, errorMsg);
            mediaProcessMapper.update(null, updateWrapper);
            return;
        }

        // 任务处理成功
        // 更新文件信息表中url
        LambdaUpdateWrapper<MediaFiles> updateMediaFilesWrapper = new LambdaUpdateWrapper<>();
        updateMediaFilesWrapper.eq(MediaFiles::getFileId, fileId)
                .set(MediaFiles::getUrl, url);
        mediaFilesMapper.update(null, updateMediaFilesWrapper);
        // 更新任务表
        LambdaUpdateWrapper<MediaProcess> updateMediaProcessWrapper = new LambdaUpdateWrapper<>();
        updateMediaProcessWrapper.eq(MediaProcess::getId, taskId)
                .set(MediaProcess::getStatus, status)
                .set(MediaProcess::getUrl, url)
                .set(MediaProcess::getFinishDate, LocalDateTime.now());
        mediaProcessMapper.update(null, updateMediaProcessWrapper);
        // 添加处理完成的任务信息到任务历史信息表
        MediaProcess newMediaProcess = mediaProcessMapper.selectById(taskId);
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(newMediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        // 删除任务表中处理完成的任务信息
        mediaProcessMapper.deleteById(taskId);
    }

}


