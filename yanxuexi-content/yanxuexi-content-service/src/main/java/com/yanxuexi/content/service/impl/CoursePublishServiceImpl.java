package com.yanxuexi.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.yanxuexi.content.config.MultipartSupportConfig;
import com.yanxuexi.content.feignclient.MediaServiceClient;
import com.yanxuexi.content.model.po.Teachplan;
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
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    MediaServiceClient mediaServiceClient;

    /**
     * @param courseId 课程Id
     * @return 课程预览信息
     * @description: 获取课程预览信息
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
     * @param compId   机构Id
     * @param courseId 课程Id
     * @description: 提交课程审核
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
        } else {
            // 更新
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本信息表的审核状态为已提交
        courseBaseInfo.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBaseInfo);
    }

    /**
     * @param compId   机构Id
     * @param courseId 课程Id
     * @description: 课程发布
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
        } else {
            // 修改
            int update = coursePublishMapper.updateById(coursePublish);
        }

        // 将课程发布消息写入消息表
        saveCoursePublishMessage(courseId);

        // 删除预发布表中的课程信息
        int delete = coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * @param courseId 课程Id
     * @return 课程静态化页面文件
     * @description: 课程静态化
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        //静态化文件
        File htmlFile = null;

        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            // todo:（这里不应该获取课程发布表中的课程信息吗，而不是重新去各个表中查，否则会出现最终发布的课程信息和审核通过的课程信息不一致）
            CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
            CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
            BeanUtils.copyProperties(coursePublish, courseBaseInfoDto);
            BeanUtils.copyProperties(JSON.parseObject(coursePublish.getMarket(), CourseMarket.class), courseBaseInfoDto);
            CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
            coursePreviewDto.setCourseBase(courseBaseInfoDto);
            coursePreviewDto.setTeachplans(JSON.parseObject(coursePublish.getTeachplan(), List.class));

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewDto);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course", ".html");
            log.debug("课程静态化，生成静态文件:{}", htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}", e.toString());
            YanXueXiException.cast("课程静态化异常");
        }
        return htmlFile;
    }

    /**
     * @param courseId 课程Id
     * @param file     课程静态化页面文件
     * @description: 上传课程静态化页面文件
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.uploadFile(multipartFile, "course/" + courseId + ".html");
        if (course == null) {
            YanXueXiException.cast("上传静态文件异常");
        }
    }

    /**
     * @param courseId 课程id
     * @description 保存消息表记录
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            YanXueXiException.cast("保存消息记录失败");
        }
    }

    /**
     * @description: 根据课程ID查询课程发布信息
     * @param courseId 课程ID
     * @return 课程发布信息
     */
    public CoursePublish getCoursePublish(Long courseId){
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish ;
    }
}
