package com.yanxuexi.content.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.yanxuexi.content.model.po.CourseTeacher;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author hyx
 * @version 1.0
 * @description 新增或修改课程教师信息传递参数
 * @date 2024-07-20 22:15
 **/
@Data
@ApiModel(value = "EditCourseTeacherDto", description = "新增或修改课程教师传递参数")
public class EditCourseTeacherDto {
    /**
     * 课程教师 Id
     */
    @ApiModelProperty(value = "课程教师Id")
    private Long id;

    /**
     * 课程 Id
     */
    @ApiModelProperty(value = "课程Id", required = true)
    @NotNull(message = "课程Id不能为空")
    private Long courseId;

    /**
     * 教师姓名
     */
    @ApiModelProperty(value = "教师姓名", required = true)
    @NotBlank(message = "教师姓名不能为空")
    private String teacherName;

    /**
     * 教师职位
     */
    @ApiModelProperty(value = "教师职位", required = true)
    @NotBlank(message = "教师职位不能为空")
    private String position;

    /**
     * 教师简介
     */
    @ApiModelProperty(value = "课程教师简介")
    private String introduction;

    /**
     * 照片
     */
    @ApiModelProperty(value = "课程教师照片")
    private String photograph;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "课程教师创建时间")
    private LocalDateTime createDate;
}
