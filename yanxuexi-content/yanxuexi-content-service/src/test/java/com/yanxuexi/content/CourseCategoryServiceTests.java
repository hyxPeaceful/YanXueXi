package com.yanxuexi.content;

import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.model.dto.CourseCategoryTreeDto;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import com.yanxuexi.content.service.CourseCategoryService;
import com.yanxuexi.content.service.impl.CourseBaseInfoServiceImpl;
import com.yanxuexi.content.service.impl.CourseCategoryServiceImpl;
import org.junit.jupiter.api.Test;
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
public class CourseCategoryServiceTests {
    @Autowired
    CourseCategoryService courseCategoryService;
    @Test
    public void testCourseBaseInfoService() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
