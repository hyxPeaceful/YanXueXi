package com.yanxuexi.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxuexi.base.model.PageParams;
import com.yanxuexi.base.model.PageResult;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.mapper.CourseCategoryMapper;
import com.yanxuexi.content.mapper.CourseMarketMapper;
import com.yanxuexi.content.model.dto.AddCourseDto;
import com.yanxuexi.content.model.dto.CourseBaseInfoDto;
import com.yanxuexi.content.model.dto.QueryCourseParamsDto;
import com.yanxuexi.content.model.po.CourseBase;
import com.yanxuexi.content.model.po.CourseCategory;
import com.yanxuexi.content.model.po.CourseMarket;
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
        PageResult<CourseBase> pageResult = new PageResult<>(courseBasePage.getRecords(), courseBasePage.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
        return pageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //合法性校验
        if (StringUtils.isBlank(addCourseDto.getName())) {
            throw new RuntimeException("课程名称为空");
        }

        if (StringUtils.isBlank(addCourseDto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(addCourseDto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(addCourseDto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }

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
            throw new RuntimeException("课程基本信息插入失败");
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
            throw new RuntimeException("课程营销信息保存失败");
        }

        // 查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    private int saveCourseMarket(CourseMarket courseMarketNew) {
        // 合法性校验
        if (StringUtils.isEmpty(courseMarketNew.getCharge())) {
            throw new RuntimeException("收费规则不能为空");
        }
        if (courseMarketNew.getCharge().equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0) {
                throw new RuntimeException("课程的价格不能为空且必须大于0");
            }
        }
        // 插入课程营销信息
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
     * @param courseId 课程Id
     * @return
     */
    private CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
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
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());

        return courseBaseInfoDto;
    }
}
