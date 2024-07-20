package com.yanxuexi.content.service;

import com.yanxuexi.content.model.dto.*;
import com.yanxuexi.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程教师管理服务接口
 * @date 2024-07-20 14:52
 **/
public interface CourseTeacherService {
    /**
     * 查询课程教师信息
     * @param courseId 课程 Id
     * @return 课程教师信息
     */
   List<CourseTeacher> queryCourseTeacher(Long courseId);

    /**
     * 新增或修改课程教师信息
     * @param editCourseTeacherDto 新增或修改课程教师信息传递参数
     */
   CourseTeacher editCourseTeacher(EditCourseTeacherDto editCourseTeacherDto);

    /**
     * 删除课程教师
     * @param courseId 课程 Id
     * @param courseTeacherId 课程教师 Id
     */
   void deleteCourseTeacher(Long courseId, Long courseTeacherId);
}
