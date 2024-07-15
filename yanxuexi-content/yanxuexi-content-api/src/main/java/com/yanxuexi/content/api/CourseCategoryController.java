package com.yanxuexi.content.api;

import com.yanxuexi.content.model.dto.CourseCategoryTreeDto;
import com.yanxuexi.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程分类相关接口
 * @date 2024-07-14 21:30
 **/
@Slf4j
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;
    @GetMapping("course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return courseCategoryService.queryTreeNodes("1");
    }
}
