package com.yanxuexi.content.api;

import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程基本信息查询接口
 * @date 2024-06-15 15:47
 **/
@Api(value = "课程内容管理接口",tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {
    @ApiOperation(value = "课程信息查询接口")
    @PostMapping("course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams) {
        CourseBase courseBase = new CourseBase();
        courseBase.setName("测试名称");
        courseBase.setCreateDate(LocalDateTime.now());
        List<CourseBase> courseBases = new ArrayList();
        courseBases.add(courseBase);
        PageResult<CourseBase> pageResult = new PageResult<CourseBase>(courseBases,10,1,10);
        return pageResult;
    }
}
