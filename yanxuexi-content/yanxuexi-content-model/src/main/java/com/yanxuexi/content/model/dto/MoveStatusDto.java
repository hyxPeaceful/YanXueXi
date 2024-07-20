package com.yanxuexi.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author hyx
 * @version 1.0
 * @description 课程计划（章节）移动状态
 * @date 2024-07-20 21:01
 **/
@Getter
public enum MoveStatusDto {
    MOVE_UP("上移"),
    MOVE_DOWN("下移");

    private final String status;

    MoveStatusDto(String status) {
        this.status = status;
    }
}
