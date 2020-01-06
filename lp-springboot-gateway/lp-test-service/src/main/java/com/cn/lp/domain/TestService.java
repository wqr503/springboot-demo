package com.cn.lp.domain;

import com.google.common.collect.Lists;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qirong on 2019/5/28.
 */
@Component
public class TestService {

    private static AtomicInteger creator = new AtomicInteger(0);

    @Cacheable(value = "emp", key = "targetClass + methodName + #p0")
    public List<Integer> getDataList() {
        return Lists.newArrayList(creator.incrementAndGet(), creator.incrementAndGet(), creator.incrementAndGet());
    }

    @Scheduled(fixedRate = 3000)
    public void scheduledTask() {
        System.out.println("Task executed at " + LocalDateTime.now());
    }

}
