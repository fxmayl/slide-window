package com.my.slide.window.slidewindow.flow;

import java.util.Date;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestLimit {

    @Autowired
    private SlideWindowCounter slideWindowCounter;

    @Value("${redis.flow.max.request:10}")
    private int maxRequest;

    public boolean countAndCheckRequestRate(Long requestId, String source) {
        log.info("request id: {} , source: {}",
            requestId, source);
        Date time = new Date();
        String key = buildKey(requestId, source);
        if (reachMaxRequest(key, time)) {
            log.warn("request id: {} , source: {}, count: {}",
                requestId, source, getCount(key, time));
            countRequest(key, time);
            return true;
        } else {
            countRequest(key, time);
            return false;
        }
    }

    private boolean reachMaxRequest(String key, Date time) {
        return getCount(key, time) >= getMaxRequest();
    }

    private int getMaxRequest() {
        return maxRequest;
    }

    private int getCount(String key, Date time) {
        return slideWindowCounter.count(key, slideWindowCounter.getMinuteBucket(time));
    }

    private String buildKey(Long userId, String resource) {
        return userId + "-" + resource;
    }

    private void countRequest(String key, Date time) {
        slideWindowCounter.increase(key, slideWindowCounter.getMinuteBucket(time));
    }
}
