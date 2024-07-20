package com.yanxuexi.content.api;

import com.yanxuexi.content.model.dto.EditCourseTeacherDto;
import com.yanxuexi.content.model.po.CourseTeacher;
import com.yanxuexi.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程教师管理相关接口
 * @date 2024-07-20 21:52
 **/
@Api(value = "课程教师管理相关接口", tags = "课程教师管理相关接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;
    @ApiOperation("课程教师信息查询")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacher(@PathVariable @Validated @NotNull(message = "课程 Id 不能为空") Long courseId) {
        return courseTeacherService.queryCourseTeacher(courseId);
    }

    @ApiOperation("课程教师信息新增或修改接口")
    @PostMapping("/courseTeacher")
    public void updateCourseTeacher(@RequestBody @Validated EditCourseTeacherDto editCourseTeacherDto) {
        courseTeacherService.editCourseTeacher(editCourseTeacherDto);
    }

    @ApiOperation("删除课程教师接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{courseTeacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long courseTeacherId) {
        courseTeacherService.deleteCourseTeacher(courseId, courseTeacherId);
    }
}
