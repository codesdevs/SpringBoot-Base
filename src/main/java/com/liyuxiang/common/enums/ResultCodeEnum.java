package com.liyuxiang.common.enums;

import lombok.Getter;

/**
 * @Author: liyuxiang
 * @Date: 2025/9/22 14:40
 * @Description: 统一响应状态码枚举
 */
@Getter
public enum ResultCodeEnum {
    // --------- 通用HTTP状态码 ---------
    // 2xx Success
    SUCCESS(200, "ok"),
    CREATED(201, "资源创建成功"),
    // 4xx 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问此资源"),
    NOT_FOUND(404, "资源不存在"),
    // 5xx
    FAIL(500, "FAIL"),
    // 用户相关 4000-4099
    USERNAME_OR_PASSWORD_ERROR(4001, "用户名或密码错误"),
    USER_NOT_EXIST(4002, "用户不存在"),
    USER_DISABLED(4003, "用户已被禁用"),
    USER_LOCKED(4004, "用户已被锁定"),
    CREDENTIALS_EXPIRED(4005, "凭证已过期"),
    USERNAME_ALREADY_EXISTS(4006, "用户名已存在"),
    USER_EMAIL_ALREADY_EXISTS(4007, "邮箱已存在"),
    USER_PHONE_ALREADY_EXISTS(4008, "手机号已存在"),
    USER_REGISTRATION_FAILED(4009, "用户注册失败"),
    USER_PASSWORD_TOO_WEAK(4010, "密码强度不够"),
    // mdt 4020-4040

    // product 3000-3999
    PRODUCT_NOT_FOUND(3001, "商品信息不存在"),
    PRODUCT_STOCK_NOT_ENOUGH(3002, "商品库存不足"),
    PRODUCT_OFF_SHELF(3003, "商品已下架"),
    PRODUCT_INVALID(3004, "商品信息无效"),
    // order 5000-5999
    ORDER_ADD_FAIL(5001, "订单添加失败"),
    ORDER_NOT_FOUND(5002, "订单不存在"),
    ORDER_STATUS_ERROR(5003, "订单状态异常"),
    ORDER_PAY_TIMEOUT(5004, "订单支付超时"),
    ORDER_CANCELLED(5005, "订单已取消"),
    ORDER_PAYMENT_PENDING(5006, "订单待支付"),
    // auth 4100-4199
    AUTH_TOKEN_INVALID(4101, "认证令牌无效"),
    AUTH_TOKEN_EXPIRED(4102, "认证令牌已过期"),
    AUTH_LOGIN_FAILED(4103, "登录失败，用户名或密码错误"),
    AUTH_LOGOUT_SUCCESS(4104, "退出登录成功"),
    AUTH_CAPTCHA_INVALID(4105, "验证码已过期或不存在"),
    // gateway 4200-4299
    GATEWAY_SERVICE_UNAVAILABLE(4201, "网关服务不可用"),
    GATEWAY_REQUEST_TIMEOUT(4202, "网关请求超时"),
    GATEWAY_FORBIDDEN(4203, "网关访问被拒绝"),
    // security 4300-4399
    SECURITY_ACCESS_DENIED(4301, "访问被拒绝"),
    SECURITY_ROLE_NOT_FOUND(4302, "角色不存在"),
    SECURITY_PERMISSION_DENIED(4303, "权限不足"),
    SECURITY_SESSION_EXPIRED(4304, "会话已过期"),
    // 5xx 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    SYSTEM_BUSY(500100, "系统繁忙，请稍后重试"),
    PAYMENT_FAILED(6001, "支付失败"),
    PAYMENT_PROCESSING(6002, "支付处理中"),
    PAYMENT_AMOUNT_MISMATCH(6003, "支付金额不匹配");

    private final int code;
    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 根据状态码查找枚举
     */
    public static ResultCodeEnum fromCode(int code) {
        for (ResultCodeEnum status : ResultCodeEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }

    @Override
    public String toString() {
        return code + " : " + msg;
    }
}