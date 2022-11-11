package com.villa.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    /**
     * @Async可满足一下需求
     * 1. 不同任务之间互不影响
     * 2. 相同任务之间也互不影响
     *
     * 如果需求为:
     * 1. 不同任务之间互不影响
     * 2. 相同任务之间排队执行
     * 则修改此值为大于任务数的值即可
     * 目前仅一条线程执行任务,与默认配置一致 所以所有任务都是排队执行
     */
    @Value("${villa.task.num:1}")
    private int num;
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(num));
    }
}