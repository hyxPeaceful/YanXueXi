package com.yanxuexi.content.service;

import com.yanxuexi.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程分类查询服务定义
 * @date 2024-07-15 21:27
 **/
public interface CourseCategoryService {
    /**
     * @description: 查询 id 节点的子节点集合（子节点有一个属性存储其子节点，即递归存放）
     * @param id 节点 id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
