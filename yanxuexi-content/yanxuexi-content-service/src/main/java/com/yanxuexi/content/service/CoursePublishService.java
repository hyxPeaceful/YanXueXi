package com.yanxuexi.content.service;

import com.yanxuexi.content.model.dto.CoursePreviewDto;

import java.io.File;

/**
 * @author hyx
 * @version 1.0
 * @description 课程预览、发布接口
 * @date 2024-08-18 11:49
 **/
public interface CoursePublishService {
    /**
     * @description: 获取课程预览信息
     * @param courseId 课程Id
     * @return 课程预览信息
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @description: 提交课程审核
     * @param compId 机构Id
     * @param courseId 课程Id
     */
    void commitAudi(Long compId, Long courseId);

    /**
     * @description: 课程发布
     * @param compId 机构Id
     * @param courseId 课程Id
     */
    void coursePublish(Long compId, Long courseId);

    /**
     * @description: 课程静态化
     * @param courseId 课程Id
     * @return 课程静态化页面文件
     */
    File generateCourseHtml(Long courseId);

    /**
     * @description: 上传课程静态化页面文件
     * @param courseId 课程Id
     * @param file 课程静态化页面文件
     */
    void uploadCourseHtml(Long courseId,File file);
}