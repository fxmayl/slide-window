package com.my.slide.window.slidewindow.config;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class RedisHelper {

    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> valueOps;

    public Long incrby(String key, int count) {
        return valueOps.increment(key, count);
    }

    @Nullable
    public Boolean setIfAbsent(String key, String s2, long l, TimeUnit timeUnit) {
        return valueOps.setIfAbsent(key, s2, l, timeUnit);
    }

    @Nullable
    public List<String> multiGet(Collection<String> collection) {
        return valueOps.multiGet(collection);
    }
}
