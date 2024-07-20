package com.yanxuexi.content.model.dto;

import com.yanxuexi.content.model.po.Teachplan;
import com.yanxuexi.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程计划树型结构DTO
 * @date 2024-07-20 10:47
 **/
@Data
public class TeachplanDto extends Teachplan {
    // 媒资信息
    private TeachplanMedia teachplanMedia;

    // 子节点
    private List<Teachplan> teachPlanTreeNodes;
}
