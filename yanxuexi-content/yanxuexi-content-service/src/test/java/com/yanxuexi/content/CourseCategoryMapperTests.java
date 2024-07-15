package com.yanxuexi.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.mapper.CourseCategoryMapper;
import com.yanxuexi.content.model.dto.CourseCategoryTreeDto;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-06-17 17:04
 **/
@SpringBootTest()
public class CourseCategoryMapperTests {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testCourseBaseMapper() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
