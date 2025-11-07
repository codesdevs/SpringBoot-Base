package com.liyuxiang.common.exception;

import com.liyuxiang.common.enums.ResultCodeEnum;
import lombok.Getter;

/**
  * @Author: liyuxiang
  * @Date: 2025/9/22 16:04
  * @Description: 自定义业务异常
  */
@Getter
public class BusinessException extends RuntimeException{
    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeEnum errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }
}
