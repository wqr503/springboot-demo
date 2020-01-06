package com.cn.lp.quartz;

/**
 * Created by on 2019/7/31.
 */
public interface TaskTrigger {

    Long getFireTimeAfter(long currentTime);

    Long getNextTime();

    Long getStartTime();

    Long getEndTime();

    Long getPreviousFireTime();

    void trigger();

    void trigger(long executeTime);

}
