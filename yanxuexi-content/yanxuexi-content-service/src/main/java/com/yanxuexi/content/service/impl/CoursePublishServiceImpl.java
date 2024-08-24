package com.yanxuexi.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.yanxuexi.messagesdk.model.po.MqMessage;
import com.yanxuexi.messagesdk.service.MqMessageService;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.content.mapper.CourseBaseMapper;
import com.yanxuexi.content.mapper.CourseMarketMapper;
import com.yanxuexi.content.mapper.CoursePublishMapper;
import com.yanxuexi.content.mapper.CoursePublishPreMapper;
import com.yanxuexi.content.model.dto.CourseBaseInfoDto;
import com.yanxuexi.content.model.dto.CoursePreviewDto;
import com.yanxuexi.content.model.dto.TeachplanDto;
import com.yanxuexi.content.model.po.CourseMarket;
import com.yanxuexi.content.model.po.CoursePublish;
import com.yanxuexi.content.model.po.CoursePublishPre;
import com.yanxuexi.content.service.CourseBaseInfoService;
import com.yanxuexi.content.service.CoursePublishService;
import com.yanxuexi.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程预览、发布接口实现
 * @date 2024-08-18 11:50
 **/
@Slf4j
@Component
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    /**
     * @description: 获取课程预览信息
     * @param courseId 课程Id
     * @return 课程预览信息
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        // 查询课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    /**
     * @description: 提交课程审核
     * @param compId 机构Id
     * @param courseId 课程Id
     */
    @Override
    @Transactional
    public void commitAudi(Long compId, Long courseId) {
        // 如果课程的审核状态为已提交则不允许提交
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            YanXueXiException.cast("课程不存在");
        }
        String auditStatus = courseBaseInfo.getAuditStatus();
        if (auditStatus.equals("202003")) {
            YanXueXiException.cast("课程已提交审核，请等待");
        }
        // 课程的图片、计划等信息没有填写也不允许提交
        if (courseBaseInfo.getPic() == null) {
            YanXueXiException.cast("请上传课程图片");
        }
        //todo：本机构只能提交审核本机构的课程

        // 查询课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.isEmpty()) {
            YanXueXiException.cast("请上传课程计划");
        }
        //查询课程基本信息、营销信息、计划信息插入预发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        // 基本信息
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        // 营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);
        // 计划信息
        String teachplanJson = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanJson);
        // 机构Id
        coursePublishPre.setCompanyId(compId);
        // 状态已提交
        coursePublishPre.setStatus("202003");
        // 提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        // 查询预发布表，若存在此课程则更新，否则插入
        CoursePublishPre publishPre = coursePublishPreMapper.selectById(courseId);
        if (publishPre == null) {
            // 插入
            coursePublishPreMapper.insert(coursePublishPre);
        }
        else {
            // 更新
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本信息表的审核状态为已提交
        courseBaseInfo.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBaseInfo);
    }

    /**
     * @description: 课程发布
     * @param compId 机构Id
     * @param courseId 课程Id
     */
    @Override
    @Transactional
    public void coursePublish(Long compId, Long courseId) {
        // 课程未提交审核不允许发布
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            YanXueXiException.cast("课程没有审核记录，请先提交审核，再发布");
        }
        // 课程未审核通过不允许发布
        if (!coursePublishPre.getStatus().equals("202004")) {
            YanXueXiException.cast("课程未审核通过，不允许发布");
        }

        //todo: 本机构只允许提交本机构的课程

        // 将数据（预发布表中课程信息）写入课程发布表
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        CoursePublish coursePublished = coursePublishMapper.selectById(courseId);
        if (coursePublished == null) {
            // 新增
            int insert = coursePublishMapper.insert(coursePublish);
        }
        else {
            // 修改
            int update = coursePublishMapper.updateById(coursePublish);
        }

        // 将课程发布消息写入消息表
        saveCoursePublishMessage(courseId);

        // 删除预发布表中的课程信息
        int delete = coursePublishPreMapper.deleteById(courseId);
    }
    /**
     * @description 保存消息表记录
     * @param courseId  课程id
     */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage == null){
            YanXueXiException.cast("保存消息记录失败");
        }
    }
}
