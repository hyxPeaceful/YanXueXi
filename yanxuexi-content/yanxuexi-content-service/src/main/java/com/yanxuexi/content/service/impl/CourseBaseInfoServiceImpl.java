package com.yanxuexi.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.*;
import com.yanxuexi.content.model.dto.AddCourseDto;
import com.yanxuexi.content.model.dto.CourseBaseInfoDto;
import com.yanxuexi.content.model.dto.EditCourseDto;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.*;
import com.yanxuexi.content.service.CourseBaseInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author hyx
 * @version 1.0
 * @description 课程基本信息查询服务
 * @date 2024-07-14 15:09
 **/
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    /**
     * @description:    课程分页查询
     * @param pageParams    分页查询参数
     * @param queryCourseParamsDto 查询条件
     * @return 查询结果
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 拼接查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据课程名称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        // 根据审核状态查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        // 根据发布状态查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        // 查询并返回
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(new Page<>(pageParams.getPageNo(), pageParams.getPageSize()), queryWrapper);
        return new PageResult<>(courseBasePage.getRecords(), courseBasePage.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * 添加课程基本信息
     *
     * @param companyId    教学机构 Id
     * @param addCourseDto 课程基本信息
     * @return 课程信息
     */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        // 向课程基本信息表插入课程基本信息
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBaseNew);
        //设置审核状态默认值：未审核
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态默认值：未发布
        courseBaseNew.setStatus("203001");
        //教学机构ID
        courseBaseNew.setCompanyId(companyId);
        //创建时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        // 插入数据库
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            YanXueXiException.cast("课程基本信息插入失败");
        }

        // 向课程营销表插入课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        // 拷贝输入的信息到实体类对象
        BeanUtils.copyProperties(addCourseDto, courseMarketNew);
        // 获取课程Id，在上面插入课程基本信息时，Mybatis plus 会自动回显主键
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);
        // 保存课程营销信息
        int update = saveCourseMarket(courseMarketNew);
        if (update <= 0) {
            YanXueXiException.cast("课程营销信息保存失败");
        }

        // 查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    /**
     * 保存课程销售信息
     *
     * @param courseMarketNew
     * @return int
     */
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        // 合法性校验
        if (StringUtils.isEmpty(courseMarketNew.getCharge())) {
            YanXueXiException.cast("收费规则不能为空");
        }
        if (courseMarketNew.getCharge().equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0) {
                YanXueXiException.cast("课程的价格不能为空且必须大于0");
            }
        }
        // 查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseMarketNew.getId());
        // 如果课程营销信息不存在则插入
        if (courseMarket == null) {
            return courseMarketMapper.insert(courseMarketNew);
        }
        // 如果课程营销信息存在则更新
        else {
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            return courseMarketMapper.updateById(courseMarket);
        }
    }

    /**
     * 根据课程id查询课程基本信息，包括基本信息和营销信息
     *
     * @param courseId 课程Id
     * @return CourseBaseInfoDto
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        // 查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        // 拼接课程基本信息和营销信息
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        // 查询课程分类信息
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        // 大分类名称
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        // 小分类名称
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        return courseBaseInfoDto;
    }

    /**
     * 修改课程
     *
     * @param companyId     教学机构 Id
     * @param editCourseDto 课程基本信息
     * @return 修改后课程基本信息
     */
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        // 业务逻辑校验
        // 查询课程是否存在
        CourseBase courseBase = courseBaseMapper.selectById(editCourseDto.getId());
        if (courseBase == null) {
            YanXueXiException.cast("修改的课程不存在");
        }
        // 机构只能修改自身所管理的课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            YanXueXiException.cast("机构只能修改自身所管理的课程");
        }

        // 封装课程基础信息
        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        // 更新课程基础信息
        int updateCourseBase = courseBaseMapper.updateById(courseBase);
        if (updateCourseBase <= 0) {
            YanXueXiException.cast("课程基础信息修改失败");
        }
        // 封装课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        // 保存课程营销信息
        int updateCourseMarket = saveCourseMarket(courseMarket);
        if (updateCourseMarket <= 0) {
            YanXueXiException.cast("课程营销信息修改失败");
        }
        // 查询修改后的课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(editCourseDto.getId());
        return courseBaseInfo;
    }

    /**
     * 删除课程
     *
     * @param companyId 机构 Id
     * @param courseId  课程 Id
     */
    @Override
    public void delectCourse(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBase.getCompanyId()))
            YanXueXiException.cast("只允许删除本机构的课程");
        // 删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(teacherLambdaQueryWrapper);
        // 删除课程计划
        LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(teachplanLambdaQueryWrapper);
        // 删除营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程基本信息
        courseBaseMapper.deleteById(courseId);
    }
}
