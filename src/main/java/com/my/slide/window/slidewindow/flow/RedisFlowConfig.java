package com.my.slide.window.slidewindow.flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisFlowConfig {

    /**
     * 定义更新redis缓存的线程池
     */
    @Bean(name = "redisFlowThreadPool")
    ExecutorService securityThreadPool(){
        return Executors.newCachedThreadPool();
    }
}
