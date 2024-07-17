package com.yanxuexi.base.exception;

import lombok.Getter;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-07-17 20:44
 **/
@Getter
public class YanXueXiException extends RuntimeException{
    private String errMessage;

    public YanXueXiException() {

    }

    public YanXueXiException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    /**
     * 抛出自定义异常
     * @param errMessage 错误信息
     */
    public static void cast(String errMessage) {
        throw new YanXueXiException(errMessage);
    }

    /**
     * 抛出自定义异常
     * @param commonError 错误码
     */
    public static void cast(CommonError commonError) {
        throw new YanXueXiException(commonError.getErrMessage());
    }
}
