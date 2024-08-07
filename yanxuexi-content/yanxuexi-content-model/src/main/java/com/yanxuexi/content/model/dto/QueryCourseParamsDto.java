package com.yanxuexi.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hyx
 * @version 1.0
 * @description 课程查询条件模型类
 * @date 2024-06-15 15:30
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCourseParamsDto {
    //审核状态
    @ApiModelProperty("审核状态")
    private String auditStatus;

    //课程名称
    @ApiModelProperty("课程名称")
    private String courseName;

    //发布状态
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
