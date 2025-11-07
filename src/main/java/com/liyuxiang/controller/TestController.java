package com.liyuxiang.controller;

import com.liyuxiang.common.result.ResponseResult;
import com.liyuxiang.common.utils.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * @Author: liyuxiang
 * @Date: 2025/9/30 16:21
 * @Description: TODO
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("hello")
    public ResponseResult<String> hello() {
        boolean existsObject = RedisUtils.hasKey("test");
        if (!existsObject) {
            RedisUtils.setCacheObject("test", "test", Duration.ofSeconds(10));
            return ResponseResult.success("hello");
        }
        Object test = RedisUtils.getCacheObject("test");
        return ResponseResult.success(test.toString());
    }
}
