package com.cn.lp;

import com.cn.lp.quartz.ScheduleTask;
import com.cn.lp.quartz.ScheduledExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Optional;

/**
 * Created by qirong on 2019/7/28.
 */
@Component
public class TestService {

    @Autowired
    private ScheduledExecutor scheduledExecutor;

    @Autowired
    private TriggerDateDAO triggerDateDAO;

    @PostConstruct
    public void init() throws ParseException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> scheduledExecutor.release()));
        CronTaskTrigger taskTrigger = new CronTaskTrigger("0/1 * * * * ?", triggerDateDAO);
        Optional<TriggerData> triggerDataOp = triggerDateDAO.findByName("test");
        if (triggerDataOp.isPresent()) {
            taskTrigger.goOn(triggerDataOp.get());
        } else {
            TriggerData triggerData = TriggerData.of("test", System.currentTimeMillis());
            taskTrigger.start(triggerData);
        }
        ScheduleTask task = new TestScheduleTask("test", taskTrigger);
        scheduledExecutor.addTask(task);
    }

}
