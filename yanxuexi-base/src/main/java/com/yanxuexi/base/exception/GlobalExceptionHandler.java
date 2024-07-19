package com.yanxuexi.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 全局异常处理
 * @date 2024-07-17 20:55
 **/

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(YanXueXiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customExceptionHandler(YanXueXiException exception) {
        log.error("系统异常，{}", exception.getErrMessage());
        return new RestErrorResponse(exception.getErrMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exceptionHandler(Exception exception) {
        log.error("系统异常, {}", exception.getMessage(), exception);
        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        BindingResult errResult = exception.getBindingResult();
        // 存储错误信息
        List<String> errMessages = new ArrayList<>();
        errResult.getFieldErrors().stream().forEach(item -> {
            errMessages.add(item.getField() + item.getDefaultMessage());
        });
        String errMessage = StringUtils.join(errMessages, ", ");
        log.error("系统异常，{}", errMessage);
        return new RestErrorResponse(errMessage);
    }
}
