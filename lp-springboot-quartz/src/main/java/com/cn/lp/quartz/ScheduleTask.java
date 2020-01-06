package com.cn.lp.quartz;

import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by qirong on 2019/7/28.
 */
public abstract class ScheduleTask implements Comparable<ScheduleTask> {

    public String name;

    public AtomicBoolean mark = new AtomicBoolean(false);

    public TaskTrigger trigger;

    public ScheduleTask(String name, TaskTrigger trigger) {
        this.name = name;
        this.trigger = trigger;
    }

    public Optional<Long> getNextTime() {
        return Optional.ofNullable(trigger.getNextTime());
    }

    public Optional<Long> getNextTime(long currentTime) {
        return Optional.ofNullable(trigger.getFireTimeAfter(currentTime));
    }

    protected void triggerNextTime(long executeTime) {
        trigger.trigger(executeTime);
    }

    public void cancelMark() {
        this.mark.set(false);
    }

    public boolean mark() {
        return this.mark.compareAndSet(false, true);
    }

    public String getName() {
        return name;
    }

    public abstract boolean isDoneEveryTimes();

    protected void preDone(long startTime, int runTimes, long executeTime) {

    }

    public void done() {
        this.done(this.getNextTime().get(), 0, this.getNextTime().get());
    }

    public void done(long startTime, int runTimes, long executeTime) {
        preDone(startTime, runTimes, executeTime);
        try {
            toDo(startTime, runTimes, executeTime);
        } finally {
            triggerNextTime(executeTime);
        }
        afterDone(startTime, runTimes, executeTime);
    }

    protected void afterDone(long startTime, int runTimes, long executeTime) {

    }

    protected abstract void toDo(long startTime, int runTimes, long executeTime);

    @Override
    public int compareTo(ScheduleTask o) {
        if(o == null) {
            return 0;
        }
        // 容错，如果计算失败，则拿当前时间计算
        long currentTime = System.currentTimeMillis();
        Long nextTime = this.getNextTime().orElse(currentTime);
        Long targetNextTime = o == null ? null : o.getNextTime().orElse(currentTime);
        return nextTime.compareTo(targetNextTime);
    }
}
