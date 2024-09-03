package com.yanxuexi.content.api;

import com.yanxuexi.base.exception.ValidationGroups;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.base.utils.StringUtil;
import com.yanxuexi.content.model.dto.AddCourseDto;
import com.yanxuexi.content.model.dto.CourseBaseInfoDto;
import com.yanxuexi.content.model.dto.EditCourseDto;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import com.yanxuexi.content.service.CourseBaseInfoService;
import com.yanxuexi.content.service.CourseCategoryService;
import com.yanxuexi.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程基本信息查询接口
 * @date 2024-06-15 15:47
 **/
@Api(value = "课程内容管理接口", tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation(value = "课程基本信息分页查询")
    @PostMapping("/course/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams) {
        SecurityUtil.XcUser xcUser = SecurityUtil.getUser();
        String companyId = xcUser.getCompanyId();
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(Long.parseLong(companyId), pageParams, queryCourseParams);
        return pageResult;
    }

    @ApiOperation(value = "新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated({ValidationGroups.Insert.class}) AddCourseDto addCourseDto) {
        // 机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }

    @ApiOperation(value = "根据课程Id查询课程基本信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseById(@PathVariable Long courseId) {
        // 获取当前用户身份信息（存于ThreadLocal）
        SecurityUtil.XcUser xcUser = SecurityUtil.getUser();
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation(value = "修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated({ValidationGroups.Update.class})EditCourseDto editCourseDto) {
        // 机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourse(@PathVariable Long courseId) {
        Long companyId = 1232141425L;
        courseBaseInfoService.delectCourse(companyId, courseId);
    }
}
