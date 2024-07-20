package com.yanxuexi.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.mapper.TeachplanMapper;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.dto.TeachplanDto;
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
 * @description 课程计划Mapper测试
 * @date 2024-06-17 17:04
 **/
@SpringBootTest()
public class TeachplanMapperTests {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Test
    public void testTeachplanMapper() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }
}
