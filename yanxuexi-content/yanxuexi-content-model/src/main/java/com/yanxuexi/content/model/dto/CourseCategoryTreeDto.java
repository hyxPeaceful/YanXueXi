package com.yanxuexi.content.model.dto;

import com.yanxuexi.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-07-14 21:21
 **/

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    // 子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
