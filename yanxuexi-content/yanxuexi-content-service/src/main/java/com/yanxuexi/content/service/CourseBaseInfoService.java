package com.yanxuexi.content.service;

import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
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
    public PageResult<CourseBase> queryCourseBaseList (PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
}
