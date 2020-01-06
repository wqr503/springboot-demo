package com.cn.lp;

import com.cn.lp.quartz.ManyTimesScheduleTask;
import com.cn.lp.quartz.TaskTrigger;

/**
 * Created by on 2019/7/31.
 */
public class TestScheduleTask extends ManyTimesScheduleTask {

    public TestScheduleTask(String name, TaskTrigger trigger) {
        super(name, trigger);
    }

    @Override
    public boolean isDoneEveryTimes() {
        return false;
    }

    @Override
    protected void toDo(long startTime, int runTimes, long executeTime) {
        System.out.println(this.name + ":" + executeTime + ":" + runTimes);
    }

}
