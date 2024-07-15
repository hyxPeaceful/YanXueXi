package com.yanxuexi.content.service.impl;

import com.yanxuexi.content.mapper.CourseCategoryMapper;
import com.yanxuexi.content.model.dto.CourseCategoryTreeDto;
import com.yanxuexi.content.model.po.CourseCategory;
import com.yanxuexi.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hyx
 * @version 1.0
 * @description 课程分类查询服务实现
 * @date 2024-07-15 21:28
 **/
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    /**
     * @description: 查询 id 节点的子节点集合（子节点有一个属性存储其子节点，即递归存放）
     * @param id 节点 id
     * @return
     */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        Map<String, CourseCategoryTreeDto> maps = courseCategoryTreeDtos.stream().filter(item -> !item.getId().equals(id)).collect(Collectors.toMap(CourseCategory::getId, value -> value, (key1, key2) -> key2));
        List<CourseCategoryTreeDto> result = new ArrayList<>();
        courseCategoryTreeDtos.stream().filter(item -> !item.getId().equals(id)).forEach(item -> {
            if (item.getParentid().equals(id)) {
                result.add(item);
            }
            CourseCategoryTreeDto courseCategoryParent = maps.get(item.getParentid());
            if (courseCategoryParent != null) {
                if (courseCategoryParent.getChildrenTreeNodes() == null) {
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<>());
                }
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return result;
    }
}
