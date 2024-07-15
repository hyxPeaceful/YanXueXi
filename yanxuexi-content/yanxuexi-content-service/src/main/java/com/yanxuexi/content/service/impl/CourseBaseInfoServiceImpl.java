package com.yanxuexi.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import com.yanxuexi.content.service.CourseBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hyx
 * @version 1.0
 * @description 课程基本信息查询服务
 * @date 2024-07-14 15:09
 **/
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 拼接查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据课程名称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        // 根据审核状态查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        // 根据发布状态查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        
        // 查询并返回
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(new Page<>(pageParams.getPageNo(), pageParams.getPageSize()), queryWrapper);
        PageResult<CourseBase> pageResult = new PageResult<>(courseBasePage.getRecords(), courseBasePage.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
        return pageResult;
    }
}
