package com.liyuxiang.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * Redis使用FastJson序列化
 *
 * @author liyuxiang
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "com.liyuxiang", "cn.dev33.satoken"};
    static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(JSON_WHITELIST_STR);

    private final Class<T> targetType;

    // 添加构造函数以支持特定类型
    public FastJson2JsonRedisSerializer() {
        this.targetType = null;
    }
    public FastJson2JsonRedisSerializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) return new byte[0];
        return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) return null;
        String str = new String(bytes, DEFAULT_CHARSET);
        try {
            // 如果有指定目标类型，优先使用
            if (targetType != null) {
                return JSON.parseObject(str, targetType, AUTO_TYPE_FILTER);
            }

            // 尝试检测是否是 OverAllState 类型
//            if (str.contains("\"@type\"") && str.contains("OverAllState")) {
//                return (T) JSON.parseObject(str, OverAllState.class, AUTO_TYPE_FILTER);
//            }

            return (T) JSON.parseObject(str, Object.class, AUTO_TYPE_FILTER);
        } catch (Exception e) {
            throw new SerializationException("Could not deserialize: " + e.getMessage(), e);
        }
    }
}