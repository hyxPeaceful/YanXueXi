package com.yanxuexi.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hyx
 * @version 1.0
 * @description 分页参数封装类
 * @date 2024-06-15 15:19
 **/
@Data
public class PageParams {
    // 当前页码
    @ApiModelProperty("页码")
    private Long pageNo = 1L;

    // 每页记录数默认值
    @ApiModelProperty("每页记录数")
    private Long pageSize = 30L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
