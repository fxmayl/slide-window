package com.my.slide.window.slidewindow.flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import com.my.slide.window.slidewindow.config.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 滑动窗口
 *
 * 粒度：1 分钟
 * 窗口宽度：通过 windowSize 设置
 */
@Component
@Slf4j
public class SlideWindowCounter {

    private static final int MINUTES_IN_HOUR = 60;

    private static final String PREFIX = "redis-flow";

    private static final String SPLIT = "#";

    @Value("${redis.flow.expire.minutes:3}")
    private int windowSize;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    @Qualifier("redisFlowThreadPool")
    private ExecutorService threadPool;

    @PreDestroy
    public void shutdownThreadPool(){
        threadPool.shutdown();
    }

    public int getMinuteBucket(Date time) { // 获取当前时间对应的分钟数，也就是当前对应的桶的位置
        return new DateTime(time).getMinuteOfHour();
    }

    public String keyOfBucket(String token, int bucket) {
        return PREFIX + SPLIT + token + SPLIT + bucket;
    }

    public void increase(String token, int bucket) {
        threadPool.execute(() -> increaseInRedis(keyOfBucket(token, bucket)));
    }

    public int count(String token, int bucket) {
        List<String> mulKeys = getMulKeys(token, bucket);
        List<String> rlts = redisHelper.multiGet(mulKeys);
        return calculateAccessTimes(rlts);
    }

    private List<String> getMulKeys(String token, int bucket) {
        List<String> keys = new ArrayList<String>(windowSize * 2);
        // 获取当前窗口之前几个窗口，也就是获取近windowSize分钟内的访问次数
        /**
         * ----------------------------------------------------------------
         * |    |           |           |        |    |    |    |    |    |
         * |    | bucket-2  | bucket-1  | bucket |    |    |    |    |    |
         * |    |           |           |        |    |    |    |    |    |
         * ----------------------------------------------------------------
         * 每一个窗口代表1分钟，此处遍历为了获取最近的windowSize个窗口
         */
        for (int i = 0; i < windowSize; i++) {
            keys.add(keyOfBucket(token, (MINUTES_IN_HOUR + bucket - i) % MINUTES_IN_HOUR));
        }
        return keys;
    }

    private void increaseInRedis(String key) {
        log.info("slide window redis key:" + key);
        int expireTime = windowSize * MINUTES_IN_HOUR;
        // 第一次设置key值后，不进行加1处理，之后再次设置key值时，进行加1处理
        Boolean isFirstSet = redisHelper.setIfAbsent(key, "1", expireTime, TimeUnit.SECONDS);
        if (!(isFirstSet != null && isFirstSet)) {
            redisHelper.incrby(key, 1);
        }
    }

    private int calculateAccessTimes(List<String> rlts) {
        int accessTimes = 0; // 统计访问次数
        if (rlts != null && rlts.size() > 0) {
            for (String rlt : rlts) {
                if (rlt != null) {
                    accessTimes += Integer.parseInt(rlt);
                }
            }
        }
        return accessTimes;
    }
}
