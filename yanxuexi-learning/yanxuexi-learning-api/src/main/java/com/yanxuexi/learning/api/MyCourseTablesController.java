package com.yanxuexi.learning.api;

import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.learning.model.dto.MyCourseTableParams;
import com.yanxuexi.learning.model.dto.XcChooseCourseDto;
import com.yanxuexi.learning.model.dto.XcCourseTablesDto;
import com.yanxuexi.learning.model.po.XcCourseTables;
import com.yanxuexi.learning.service.MyCourseTablesService;
import com.yanxuexi.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的课程表接口
 * @date 2022/10/25 9:40
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {
    @Autowired
    MyCourseTablesService myCourseTablesService;
    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser xcUser = SecurityUtil.getUser();
        String userId = xcUser.getId();
        XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(userId, courseId);
        return xcChooseCourseDto;
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser xcUser = SecurityUtil.getUser();
        String userId = xcUser.getId();
        XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
        return learningStatus;
    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        return null;
    }

}
