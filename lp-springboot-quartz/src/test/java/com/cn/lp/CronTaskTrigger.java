package com.cn.lp;

import com.cn.lp.quartz.TaskTrigger;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by on 2019/7/31.
 */
public class CronTaskTrigger implements TaskTrigger {

    private CronTriggerImpl cronTrigger;

    private TriggerDateDAO triggerDateDAO;

    private TriggerData date;

    private String cronExpression;

    public CronTaskTrigger(String cronExpression, TriggerDateDAO triggerDateDAO) {
        this.triggerDateDAO = triggerDateDAO;
        this.cronExpression = cronExpression;
    }

    public void start(TriggerData triggerData) {
        this.date = triggerData;
        try {
            CronTriggerImpl cronTrigger = new CronTriggerImpl();
            cronTrigger.setCronExpression(this.cronExpression);
            cronTrigger.setStartTime(triggerData.getStartTime().map(Date::new).orElse(null));
            cronTrigger.setEndTime(triggerData.getEndTime().map(Date::new).orElse(null));
            cronTrigger.setPreviousFireTime(triggerData.getPreviousFireTime().map(Date::new).orElse(null));
            cronTrigger.setNextFireTime(triggerData.getPreviousFireTime().map(Date::new).orElse(null));
            this.cronTrigger = cronTrigger;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.trigger();
    }

    public void goOn(TriggerData triggerData) {
        this.date = triggerData;
        try {
            CronTriggerImpl cronTrigger = new CronTriggerImpl();
            cronTrigger.setCronExpression(this.cronExpression);
            cronTrigger.setStartTime(triggerData.getStartTime().map(Date::new).orElse(null));
            cronTrigger.setEndTime(triggerData.getEndTime().map(Date::new).orElse(null));
            cronTrigger.setPreviousFireTime(triggerData.getPreviousFireTime().map(Date::new).orElse(null));
            cronTrigger.setNextFireTime(triggerData.getPreviousFireTime().map(Date::new).orElse(null));
            this.cronTrigger = cronTrigger;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long getFireTimeAfter(long currentTime) {
        Date date = cronTrigger.getFireTimeAfter(new Date(currentTime));
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @Override
    public Long getNextTime() {
        Date date = cronTrigger.getNextFireTime();
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @Override
    public Long getStartTime() {
        Date date = cronTrigger.getStartTime();
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @Override
    public Long getEndTime() {
        Date date = cronTrigger.getEndTime();
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @Override
    public Long getPreviousFireTime() {
        Date date = cronTrigger.getPreviousFireTime();
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    @Override
    public void trigger() {
        cronTrigger.triggered(null);
        saveDate();
    }

    @Override
    public void trigger(long executeTime) {
        cronTrigger.setNextFireTime(new Date(executeTime));
        cronTrigger.triggered(null);
        saveDate();
    }

    private void saveDate() {
        date.setStartTime(cronTrigger.getStartTime() == null ? null : cronTrigger.getStartTime().getTime());
        date.setEndTime(cronTrigger.getEndTime() == null ? null : cronTrigger.getEndTime().getTime());
        date.setNextFireTime(cronTrigger.getNextFireTime() == null ? null : cronTrigger.getNextFireTime().getTime());
        date.setPreviousFireTime(cronTrigger.getPreviousFireTime() == null ? null : cronTrigger.getPreviousFireTime().getTime());
        triggerDateDAO.save(date);
    }

}
