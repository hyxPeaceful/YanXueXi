package com.yanxuexi.content.service.impl;

import com.yanxuexi.content.model.dto.CourseBaseInfoDto;
import com.yanxuexi.content.model.dto.CoursePreviewDto;
import com.yanxuexi.content.model.dto.TeachplanDto;
import com.yanxuexi.content.service.CourseBaseInfoService;
import com.yanxuexi.content.service.CoursePublishService;
import com.yanxuexi.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程预览、发布接口实现
 * @date 2024-08-18 11:50
 **/
@Slf4j
@Component
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    /**
     * @description: 获取课程预览信息
     * @param courseId 课程Id
     * @return 课程预览信息
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        // 查询课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }
}
