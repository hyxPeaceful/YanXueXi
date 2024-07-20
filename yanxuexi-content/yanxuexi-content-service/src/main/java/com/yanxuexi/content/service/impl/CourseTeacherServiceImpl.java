package com.yanxuexi.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.content.mapper.CourseTeacherMapper;
import com.yanxuexi.content.model.dto.EditCourseTeacherDto;
import com.yanxuexi.content.model.po.CourseTeacher;
import com.yanxuexi.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程教师管理服务实现
 * @date 2024-07-20 22:01
 **/
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> queryCourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    /**
     * 新增或修改课程教师信息
     * @param editCourseTeacherDto 新增或修改课程教师信息传递参数
     */
    @Override
    public CourseTeacher editCourseTeacher(EditCourseTeacherDto editCourseTeacherDto) {
        Long id = editCourseTeacherDto.getId();
        // 判断新增还是修改
        if (id == null) {
            CourseTeacher courseTeacherNew = new CourseTeacher();
            BeanUtils.copyProperties(editCourseTeacherDto, courseTeacherNew);
            courseTeacherNew.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacherNew);
            if (insert <= 0) {
                YanXueXiException.cast("课程教师信息修改失败");
            }
            return courseTeacherMapper.selectById(courseTeacherNew.getId());
        }
        else {
            CourseTeacher courseTeacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(editCourseTeacherDto, courseTeacher);
            int update = courseTeacherMapper.updateById(courseTeacher);
            if (update <= 0) {
                YanXueXiException.cast("课程教师信息修改失败");
            }
            return courseTeacherMapper.selectById(courseTeacher.getId());
        }
    }

    /**
     * 课程教师删除
     * @param courseId 课程 Id
     * @param courseTeacherId 课程教师 Id
     */
    @Override
    public void deleteCourseTeacher(Long courseId, Long courseTeacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, courseTeacherId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete <= 0) {
            YanXueXiException.cast("课程教师删除失败");
        }
    }
}
