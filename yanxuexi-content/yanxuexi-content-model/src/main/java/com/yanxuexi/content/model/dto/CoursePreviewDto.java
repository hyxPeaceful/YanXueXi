package com.yanxuexi.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程预览数据模型
 * @date 2024-08-18 11:44
 **/
@Data
public class CoursePreviewDto {
    // 课程基本信息 营销信息
    private CourseBaseInfoDto courseBase;

    //课程计划信息
    List<TeachplanDto> teachplans;

    //课程师资信息...

}
