package com.yanxuexi.content.service;

import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.model.dto.AddCourseDto;
import com.yanxuexi.content.model.dto.CourseBaseInfoDto;
import com.yanxuexi.content.model.dto.EditCourseDto;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;

/**
 * @author hyx
 * @version 1.0
 * @description 课程信息管理
 * @date 2024-07-14 15:04
 **/
public interface CourseBaseInfoService {

    /**
     * 课程分页查询
     * @param pageParams 分页查询参数
     * @param queryCourseParamsDto 查询条件
     * @return 查询结果
     */
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 添加课程基本信息
     * @param companyId 教学机构 Id
     * @param addCourseDto 课程基本信息
     * @return 课程信息
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程Id查询课程信息
     * @param courseId 课程Id
     * @return CourseBaseInfoDto
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     * @param companyId 教学机构 Id
     * @param editCourseDto 课程基本信息
     * @return 修改后课程基本信息
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    /**
     * 删除课程
     * @param companyId 机构 Id
     * @param courseId 课程 Id
     */
    public void delectCourse(Long companyId, Long courseId);
}
