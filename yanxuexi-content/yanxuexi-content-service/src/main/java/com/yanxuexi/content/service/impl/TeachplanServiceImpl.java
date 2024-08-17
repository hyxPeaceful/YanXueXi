package com.yanxuexi.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxuexi.base.exception.YanXueXiException;
import com.yanxuexi.content.mapper.TeachplanMapper;
import com.yanxuexi.content.mapper.TeachplanMediaMapper;
import com.yanxuexi.content.model.dto.BindTeachplanMediaDto;
import com.yanxuexi.content.model.dto.MoveStatusDto;
import com.yanxuexi.content.model.dto.SaveTeachplanDto;
import com.yanxuexi.content.model.dto.TeachplanDto;
import com.yanxuexi.content.model.po.Teachplan;
import com.yanxuexi.content.model.po.TeachplanMedia;
import com.yanxuexi.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程计划服务实现
 * @date 2024-07-20 14:56
 **/
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    /**
     * 查询课程计划树型结构实现
     * @param courseId 课程Id
     * @return
     */
    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * 保存课程计划实现
     * @param saveTeachplanDto 课程计划信息
     */
    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();
        // 根据课程计划 id 是否为空，判断是新增课程计划还是修改课程计划
        // 修改
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            int update = teachplanMapper.updateById(teachplan);
        }
        // 新增
        else {
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            // 确定排序字段，默认新增的章节排在最后，因此可以找到同级节点中排序字段的最大值，然后加1
            Long courseId = teachplan.getCourseId();
            Long parentid = teachplan.getParentid();
            Integer maxOrderBy = teachplanMapper.selectMaxOrderBy(courseId, parentid);
            maxOrderBy = maxOrderBy == null ? 1 : maxOrderBy;
            teachplan.setOrderby(maxOrderBy + 1);
            teachplanMapper.insert(teachplan);
        }
    }

    /**
     * 删除课程计划
     * @param teachplanId 课程计划 Id
     */
    @Override
    @Transactional
    public void deleteTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Long parentId = teachplan.getParentid();
        // 判断删除章节是大章节还是小章节
        if (parentId == 0L) {
            // 删除的是大章节，判断大章节下是否还有小章节
            Integer childNodeNum = teachplanMapper.selectChildNodeNum(teachplanId);
            if (childNodeNum > 0) {
                // 大章节下有小章节
                YanXueXiException.cast("删除课程章节下有小章节，请先删除小章节");
            }
            else {
                // 大章节下没有小章节
                int delete = teachplanMapper.deleteById(teachplanId);
                if (delete <= 0) {
                    YanXueXiException.cast("课程章节删除失败");
                }
            }
        }
        else {
            // 删除的是小章节
            int delete = teachplanMapper.deleteById(teachplanId);
            if (delete <= 0) {
                YanXueXiException.cast("课程章节删除失败");
            }
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getCourseId, teachplan.getCourseId())
                            .eq(TeachplanMedia::getTeachplanId, teachplanId);
            // 同时删除小章节关联的媒资信息
            teachplanMediaMapper.delete(queryWrapper);
        }
    }

    /**
     * 课程计划（章节）移动
     * @param teachplanId 课程计划 Id
     * @param moveStatus 课程移动状态
     */
    @Override
    public void moveTeachPlan(Long teachplanId, MoveStatusDto moveStatus) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 查询同一级的所有节点
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        // 获取当前节点的索引
        int currentNodeIndex = teachplans.indexOf(teachplan);
        // 获取当前节点的排序值
        Integer currentNodeOrderby = teachplan.getOrderby();
        // 待交换的节点（章节）
        Teachplan swapTeachplan = null;
        // 判断是上移还是下移
        if (moveStatus == MoveStatusDto.MOVE_UP) {
            // 上移
            if (currentNodeIndex == 0) {
                YanXueXiException.cast("当前章节已处在最上位置，无法上移");
            }
            else {
                // 获取同一级上一个节点（章节），并交换他们的次序
                swapTeachplan = teachplans.get(currentNodeIndex - 1);
                Integer orderby = swapTeachplan.getOrderby();
                swapTeachplan.setOrderby(currentNodeOrderby);
                teachplan.setOrderby(orderby);
            }
        }
        if (moveStatus == MoveStatusDto.MOVE_DOWN) {
            // 下移
            if (currentNodeIndex == teachplans.size() - 1) {
                YanXueXiException.cast("当前章节已处在最下位置，无法下移");
            }
            else {
                // 获取同一级下一个节点（章节），并交换他们的次序
                swapTeachplan = teachplans.get(currentNodeIndex + 1);
                Integer orderby = swapTeachplan.getOrderby();
                swapTeachplan.setOrderby(currentNodeOrderby);
                teachplan.setOrderby(orderby);
            }
        }
        // 将次序交换完的节点（章节）更新到数据库表中
        teachplanMapper.updateById(teachplan);
        if (swapTeachplan == null) {
            YanXueXiException.cast("课程章节移动失败");
        }
        teachplanMapper.updateById(swapTeachplan);
    }

    /**
     * @description: 教学计划绑定媒资
     * @param bindTeachplanMediaDto 绑定参数
     * @return 教学计划媒资绑定信息
     */
    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        // 校验
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Long courseId = teachplan.getCourseId();
        if (teachplan == null) {
            YanXueXiException.cast("课程计划不存在");
        }
        if (teachplan.getGrade() != 2) {
            YanXueXiException.cast("仅允许二级课程计划绑定媒资");
        }
        // 删除课程计划已绑定的媒资
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId());
        int delete = teachplanMediaMapper.delete(queryWrapper);

        // 课程计划绑定新的媒资
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto, teachplanMedia);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCourseId(courseId);
        int insert = teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    /**
     * @description: 课程计划媒资解除绑定
     * @param teachplanId 课程计划Id
     * @param mediaId 媒资Id
     */
    @Override
    @Transactional
    public void deleteAssociatedMedia(Long teachplanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId)
                .eq(TeachplanMedia::getMediaId, mediaId);
        int delete = teachplanMediaMapper.delete(queryWrapper);
    }
}
