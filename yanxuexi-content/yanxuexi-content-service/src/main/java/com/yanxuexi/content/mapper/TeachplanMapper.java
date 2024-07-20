package com.yanxuexi.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxuexi.content.model.dto.TeachplanDto;
import com.yanxuexi.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * 查询课程计划树型结构
     * @param courseId 课程 Id
     * @return 课程计划树型结构
     */
    List<TeachplanDto> selectTreeNodes(Long courseId);

    /**
     * 查询同级节点排序字段最大值
     * @param courseId 课程 Id
     * @param parentid 父节点 Id
     * @return 同级节点排序字段最大值
     */
    Integer selectMaxOrderBy(@Param("courseId") Long courseId, @Param("parentid") Long parentid);

    /**
     * 查询课程计划子节点个数（查询某个大章节的子章节数量）
     * @param teachplanId
     * @return
     */
    Integer selectChildNodeNum(Long teachplanId);
}
