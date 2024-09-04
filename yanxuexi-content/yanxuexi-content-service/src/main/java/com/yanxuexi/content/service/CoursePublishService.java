package com.yanxuexi.content.service;

import com.yanxuexi.content.model.dto.CoursePreviewDto;
import com.yanxuexi.content.model.po.CoursePublish;

import java.io.File;

/**
 * @author hyx
 * @version 1.0
 * @description 课程预览、发布接口
 * @date 2024-08-18 11:49
 **/
public interface CoursePublishService {
    /**
     * @param courseId 课程Id
     * @return 课程预览信息
     * @description: 获取课程预览信息
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @param compId   机构Id
     * @param courseId 课程Id
     * @description: 提交课程审核
     */
    void commitAudi(Long compId, Long courseId);

    /**
     * @param compId   机构Id
     * @param courseId 课程Id
     * @description: 课程发布
     */
    void coursePublish(Long compId, Long courseId);

    /**
     * @param courseId 课程Id
     * @return 课程静态化页面文件
     * @description: 课程静态化
     */
    File generateCourseHtml(Long courseId);

    /**
     * @param courseId 课程Id
     * @param file     课程静态化页面文件
     * @description: 上传课程静态化页面文件
     */
    void uploadCourseHtml(Long courseId, File file);

    /**
     * @param courseId 课程ID
     * @return 课程发布信息
     * @description: 根据课程ID查询课程发布信息
     */
    CoursePublish getCoursePublish(Long courseId);
}
