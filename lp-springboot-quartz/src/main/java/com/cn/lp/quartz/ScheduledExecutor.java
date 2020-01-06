package com.cn.lp.quartz;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qirong on 2019/7/27.
 */
@Component
public class ScheduledExecutor {

    private ExecutorService runThreadPool;

    private ExecutorService heartbeatThreadPool;

    private Map<String, ScheduleTask> taskMap = new ConcurrentHashMap<>();

    private volatile boolean running;

    public ScheduledExecutor() {
        this(Executors.newCachedThreadPool());
    }

    public ScheduledExecutor(int runThreadNum) {
        this(Executors.newFixedThreadPool(runThreadNum));
    }

    public ScheduledExecutor(ExecutorService runThreadPool) {
        this.runThreadPool = runThreadPool;
        this.heartbeatThreadPool = Executors.newSingleThreadExecutor();
        this.running = true;
        this.heartbeatThreadPool.submit(() -> {
            while (true) {
                try {
                    if (running) {
                        for (ScheduleTask task : taskMap.values()) {
                            if (task.getNextTime().isPresent() && (System.currentTimeMillis() >= task.getNextTime().get())) {
                                if (task.mark()) {
                                    runThreadPool.submit(() -> {
                                        try {
                                            task.done();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            task.cancelMark();
                                        }
                                    });
                                }
                            }
                        }
                    }
                    Thread.sleep(250);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ScheduleTask getTask(String name) {
        return taskMap.get(name);
    }

    public void stop() {
        running = false;
    }

    public void restart() {
        running = true;
    }

    public void release() {
        this.stop();
        heartbeatThreadPool.shutdown();
        runThreadPool.shutdown();
    }

    public void addTask(ScheduleTask task) {
        Long nextTime = task.getNextTime().orElse(null);
        if (nextTime != null) {
            taskMap.putIfAbsent(task.getName(), task);
        }
    }

    public void removeTask(String name) {
        taskMap.remove(name);
    }

}
