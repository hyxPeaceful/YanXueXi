package com.yanxuexi.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hyx
 * @version 1.0
 * @description 错误响应参数包装
 * @date 2024-07-17 20:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestErrorResponse {
    private String errMessage;
}
