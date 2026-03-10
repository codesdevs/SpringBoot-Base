package com.liyuxiang.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.liyuxiang.common.constant.CacheConstants;
import com.liyuxiang.common.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Duration;

/**
 * 验证码操作处理
 */
@RestController
public class CaptchaController {

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public void captchaImage(@RequestParam("uuid") String uuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        lineCaptcha.setGenerator(randomGenerator);
        // 重新生成code
        lineCaptcha.createCode();
        lineCaptcha.write(response.getOutputStream());
        //将验证码+uuid 缓存
        RedisUtils.setCacheObject(CacheConstants.CAPTCHA_CODE_KEY + uuid, lineCaptcha.getCode(), Duration.ofMinutes(4));
    }
}
