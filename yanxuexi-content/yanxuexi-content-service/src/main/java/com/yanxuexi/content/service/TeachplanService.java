package com.yanxuexi.content.service;

import com.yanxuexi.content.model.dto.BindTeachplanMediaDto;
import com.yanxuexi.content.model.dto.MoveStatusDto;
import com.yanxuexi.content.model.dto.SaveTeachplanDto;
import com.yanxuexi.content.model.dto.TeachplanDto;
import com.yanxuexi.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程计划服务接口
 * @date 2024-07-20 14:52
 **/
public interface TeachplanService {
    /**
     * 查询课程计划树型结构
     * @param courseId 课程Id
     * @return 课程计划
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 保存课程计划
     * @param saveTeachplanDto 课程计划信息
     */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId 课程计划 Id
     */
    void deleteTeachplan(Long teachplanId);

    /**
     * 课程技术（章节）移动顺序
     * @param teachplanId 课程计划 Id
     * @param moveStatus 课程移动状态
     */
    void moveTeachPlan(Long teachplanId, MoveStatusDto moveStatus);

    /**
     * @description: 教学计划绑定媒资
     * @param bindTeachplanMediaDto 绑定参数
     * @return 教学计划媒资绑定信息
     */
    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * @description: 课程计划媒资解除绑定
     * @param teachplanId 课程计划Id
     * @param mediaId 媒资Id
     */
    void deleteAssociatedMedia(Long teachplanId, String mediaId);
}
