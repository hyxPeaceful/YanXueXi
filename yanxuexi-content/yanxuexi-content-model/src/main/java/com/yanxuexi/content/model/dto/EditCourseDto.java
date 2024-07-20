package com.yanxuexi.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author hyx
 * @version 1.0
 * @description 修改课程传入参数实体类
 * @date 2024-07-19 21:54
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程Id", required = true)
    @NotNull(message = "课程Id不能为空")
    private Long id;
}
