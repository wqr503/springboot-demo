package com.cn.lp.quartz;

/**
 * Created by on 2019/7/31.
 */
public abstract class ManyTimesScheduleTask extends ScheduleTask {

    public ManyTimesScheduleTask(String name, TaskTrigger trigger) {
        super(name, trigger);
    }

    @Override
    public void done() {
        Long startTime = this.getNextTime().get();
        int runTimes = 0;
        long executeTime;
        Long nextTime = this.getNextTime().get();
        do {
            if (this.isDoneEveryTimes()) {
                executeTime = this.getNextTime().get();
                super.done(startTime, runTimes, executeTime);
                nextTime = this.getNextTime().orElse(null);
            } else {
                runTimes = runTimes + 1;
                executeTime = nextTime;
                nextTime = this.getNextTime(executeTime).orElse(null);
            }
        } while (nextTime != null && System.currentTimeMillis() >= nextTime);
        if (!this.isDoneEveryTimes()) {
            super.done(startTime, runTimes - 1, executeTime);
        }
    }
}
