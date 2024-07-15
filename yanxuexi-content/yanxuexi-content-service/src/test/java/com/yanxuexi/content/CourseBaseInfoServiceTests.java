package com.yanxuexi.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import com.yanxuexi.content.service.impl.CourseBaseInfoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-06-17 17:04
 **/
@SpringBootTest()
public class CourseBaseInfoServiceTests {
    @Autowired
    CourseBaseInfoServiceImpl courseBaseInfoService;
    @Test
    public void testCourseBaseInfoService() {
        // 详细进行分页查询的单元测试
        // 查询条件
        QueryCourseParamsDto queryCourseParams = new QueryCourseParamsDto();
        queryCourseParams.setCourseName("java");
        // 分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(2L);
        // 查询并返回
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParams);
        System.out.println(pageResult);
    }
}
