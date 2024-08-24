package com.yanxuexi.content.service.jobHandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yanxuexi.messagesdk.model.po.MqMessage;
import com.yanxuexi.messagesdk.service.MessageProcessAbstract;
import com.yanxuexi.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hyx
 * @version 1.0
 * @description 课程发布任务类
 * @date 2024-08-24 20:32
 **/
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {

    // 任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
        // 分片参数
        // 分片序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }
    /**
     * @description: 任务处理方法
     * @param mqMessage 执行任务内容
     * @return 是否处理成功
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Long.parseLong(businessKey1);
        // 课程静态化，上传静态化的课程到MinIo

        // 发布课程信息缓存到Redis

        // 添加发布课程索引到ElasticSearch
        return true;
    }

    /**
     * @description: 课程静态化
     * @param mqMessage 课程处理消息
     * @param courseId 课程Id
     */
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        // 任务幂等性判断
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne > 0) {
            log.debug("课程静态化已完成");
            return;
        }
        // 课程静态化

        // 课程静态化任务状态更新
    }

    /**
     * @description: 保存课程索引
     * @param mqMessage 课程处理消息
     * @param courseId 课程Id
     */
    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        // 任务幂等性判断
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo > 0) {
            log.debug("课程索引已成功保存到ElasticSearch");
        }
        // 保存课程索引

        // 课程索引任务状态更新

    }

    /**
     * @description: 保存发布课程信息到Redis
     * @param mqMessage 课程处理消息
     * @param courseId 课程Id
     */
    private void saveCourseCache(MqMessage mqMessage, long courseId) {
        // 任务幂等性判断
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageThree = mqMessageService.getStageThree(id);
        if (stageThree > 0) {
            log.debug("发布的课程信息已成功缓存到Redis");
        }
        // 缓存课程信息

        // 课程缓存任务状态更新
    }
}
