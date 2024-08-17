package com.yanxuexi.media.service;

import com.yanxuexi.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 媒资任务处理服务
 * @date 2024-08-11 16:22
 **/
public interface MediaProcessService {
    /**
     * 获取待处理任务列表
     *
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count      任务数
     * @return 待处理任务列表
     */
    List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count);

    /**
     * 开启一个任务
     *
     * @param id 任务 Id
     * @return 是否开启成功
     */
    boolean startTask(long id);

    /**
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     * @description 保存任务结果
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}
