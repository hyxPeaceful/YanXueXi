package com.yanxuexi.content;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-06-17 17:04
 **/
@SpringBootTest()
public class CourseBaseMapperTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Test
    public void testCourseBaseMapper() {
        CourseBase courseBase = courseBaseMapper.selectById(18);
        Assertions.assertNotNull(courseBase);

        // 详细进行分页查询的单元测试
        // 查询条件
        QueryCourseParamsDto queryCourseParams = new QueryCourseParamsDto();
        queryCourseParams.setCourseName("java");
        // 拼接查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParams.getCourseName()), CourseBase::getName, queryCourseParams.getCourseName());
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParams.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParams.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParams.getPublishStatus()), CourseBase::getStatus, queryCourseParams.getPublishStatus());
        // 分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(2L);
        // 查询并返回
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(new Page<>(pageParams.getPageNo(), pageParams.getPageSize()), queryWrapper);
        PageResult<CourseBase> pageResult = new PageResult<>(courseBasePage.getRecords(), courseBasePage.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
        System.out.println(pageResult);
    }
}
