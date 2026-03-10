package com.liyuxiang.common.enums;

import com.liyuxiang.common.constant.HttpStatus;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: liyuxiang
 * @Date: 2025/9/22 14:40
 * @Description: 统一响应状态码枚举
 */
@Getter
public enum ResultCodeEnum {
    // ── 成功类 ───────────────────────────────────────────────────────────────
    SUCCESS(200, HttpStatus.SUCCESS, "操作成功"),
    CREATED(201, HttpStatus.CREATED, "资源创建成功"),

    // ── 通用客户端错误 (4xx) ─────────────────────────────────────────────────
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "请求参数错误"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "未授权，请先登录"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "无权限访问此资源"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "资源不存在"),

    // ── 用户 & 认证相关 (4000~4199) ──────────────────────────────────────────
    USERNAME_OR_PASSWORD_ERROR(4001, HttpStatus.UNAUTHORIZED, "用户名或密码错误"),
    USER_NOT_EXIST(4002, HttpStatus.NOT_FOUND, "用户不存在"),
    USER_DISABLED(4003, HttpStatus.FORBIDDEN, "用户已被禁用"),
    USER_LOCKED(4004, HttpStatus.FORBIDDEN, "用户已被锁定"),
    CREDENTIALS_EXPIRED(4005, HttpStatus.UNAUTHORIZED, "凭证已过期"),
    USERNAME_ALREADY_EXISTS(4006, HttpStatus.BAD_REQUEST, "用户名已存在"),
    USER_EMAIL_ALREADY_EXISTS(4007, HttpStatus.BAD_REQUEST, "邮箱已存在"),
    USER_PHONE_ALREADY_EXISTS(4008, HttpStatus.BAD_REQUEST, "手机号已存在"),
    USER_REGISTRATION_FAILED(4009, HttpStatus.ERROR, "用户注册失败"),
    USER_PASSWORD_TOO_WEAK(4010, HttpStatus.BAD_REQUEST, "密码强度不够"),

    AUTH_TOKEN_INVALID(4101, HttpStatus.UNAUTHORIZED, "认证令牌无效"),
    AUTH_TOKEN_EXPIRED(4102, HttpStatus.UNAUTHORIZED, "认证令牌已过期"),
    AUTH_LOGIN_FAILED(4103, HttpStatus.UNAUTHORIZED, "登录失败，用户名或密码错误"),
    AUTH_LOGOUT_SUCCESS(4104, HttpStatus.SUCCESS, "退出登录成功"),
    AUTH_CAPTCHA_INVALID(4105, HttpStatus.BAD_REQUEST, "验证码已过期或不存在"),

    // ── 商品相关 (3000~3999) ─────────────────────────────────────────────────
    PRODUCT_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "商品信息不存在"),
    PRODUCT_STOCK_NOT_ENOUGH(3002, HttpStatus.BAD_REQUEST, "商品库存不足"),
    PRODUCT_OFF_SHELF(3003, HttpStatus.BAD_REQUEST, "商品已下架"),
    PRODUCT_INVALID(3004, HttpStatus.BAD_REQUEST, "商品信息无效"),

    // ── 订单相关 (5000~5999) ─────────────────────────────────────────────────
    ORDER_ADD_FAIL(5001, HttpStatus.ERROR, "订单添加失败"),
    ORDER_NOT_FOUND(5002, HttpStatus.NOT_FOUND, "订单不存在"),
    ORDER_STATUS_ERROR(5003, HttpStatus.BAD_REQUEST, "订单状态异常"),
    ORDER_PAY_TIMEOUT(5004, HttpStatus.BAD_REQUEST, "订单支付超时"),
    ORDER_CANCELLED(5005, HttpStatus.BAD_REQUEST, "订单已取消"),
    ORDER_PAYMENT_PENDING(5006, HttpStatus.BAD_REQUEST, "订单待支付"),

    // ── 系统 & 服务器错误 (5xx) ──────────────────────────────────────────────
    FAIL(500, HttpStatus.ERROR, "操作失败"),
    INTERNAL_SERVER_ERROR(500, HttpStatus.ERROR, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, HttpStatus.ERROR, "服务暂时不可用"),
    SYSTEM_BUSY(500100, HttpStatus.ERROR, "系统繁忙，请稍后重试"),

    // ── 支付相关 (6000~) ─────────────────────────────────────────────────────
    PAYMENT_FAILED(6001, HttpStatus.BAD_REQUEST, "支付失败"),
    PAYMENT_PROCESSING(6002, HttpStatus.ACCEPTED, "支付处理中"),
    PAYMENT_AMOUNT_MISMATCH(6003, HttpStatus.BAD_REQUEST, "支付金额不匹配");

    private final int code;
    private final int httpStatus;
    private final String msg;

    ResultCodeEnum(int code, int httpStatus, String msg) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    /**
     * 根据 code 查找枚举（推荐使用 Optional 避免 null）
     */
//    public static Optional<ResultCodeEnum> fromCode(int code) {
//        return Arrays.stream(values())
//                .filter(e -> e.code == code)
//                .findFirst();
//    }

    /**
     * 是否成功（code 以 2 开头）
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    @Override
    public String toString() {
        return code + " - " + msg;
    }
}