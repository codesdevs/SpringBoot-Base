package com.liyuxiang.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.liyuxiang.common.enums.ResultCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: liyuxiang
 * @Date: 2025/9/22 15:32
 * @Description: 统一响应结果类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ResponseResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;

    private ResponseResult() {
    }

    private ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建成功响应 (code=200, msg=SUCCESS)
     */
    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    /**
     * 创建成功响应，携带数据
     */
    public static <T> ResponseResult<T> success(T data) {
        return build(ResultCodeEnum.SUCCESS, data);
    }

    /**
     * 创建成功响应，自定义状态码和消息
     */
    public static <T> ResponseResult<T> success(Integer code, String msg) {
        return build(code, msg, null);
    }

    /**
     * 创建成功响应，自定义状态码、消息和数据
     */
    public static <T> ResponseResult<T> success(Integer code, String msg, T data) {
        return build(code, msg, data);
    }

    /**
     * 创建失败响应，使用 ResultStatusEnum
     */
    public static <T> ResponseResult<T> fail(ResultCodeEnum error) {
        return build(error, null);
    }

    /**
     * 创建失败响应，自定义错误码和消息
     */
    public static <T> ResponseResult<T> fail(Integer code, String msg) {
        return build(code, msg, null);
    }

    /**
     * 创建失败响应，使用 ResultStatusEnum 并可选覆盖消息
     */
    public static <T> ResponseResult<T> fail(ResultCodeEnum error, String msg) {
        return build(error.getCode(), msg != null ? msg : error.getMsg(), null);
    }

    /**
     * 使用 ResultStatusEnum 构建响应
     */
    private static <T> ResponseResult<T> build(ResultCodeEnum status, T data) {
        return build(status.getCode(), status.getMsg(), data);
    }

    /**
     * 使用 code, msg, data 构建响应
     */
    private static <T> ResponseResult<T> build(Integer code, String msg, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }

    /**
     * 设置错误状态（链式调用）
     *
     * @deprecated 建议使用静态工厂方法 fail(...) 创建错误响应
     */
    @Deprecated
    public ResponseResult<T> error(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null; // 错误时通常不返回数据
        return this;
    }

    /**
     * 设置成功状态（链式调用）
     *
     * @deprecated 建议使用静态工厂方法 success(...) 创建成功响应
     */
    @Deprecated
    public ResponseResult<T> ok(T data) {
        this.code = ResultCodeEnum.SUCCESS.getCode();
        this.msg = ResultCodeEnum.SUCCESS.getMsg();
        this.data = data;
        return this;
    }
}