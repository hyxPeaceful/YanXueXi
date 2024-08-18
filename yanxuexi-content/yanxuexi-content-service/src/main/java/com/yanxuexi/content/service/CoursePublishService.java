package com.yanxuexi.content.service;

import com.yanxuexi.content.model.dto.CoursePreviewDto;

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
}
