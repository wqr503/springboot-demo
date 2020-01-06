package com.cn.lp;

import javax.persistence.*;
import java.util.Optional;

/**
 * Created by on 2019/8/1.
 */
@Entity
@Table(name = "task_trigger", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})
})
public class TriggerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private Long startTime;

    @Column
    private Long endTime;

    @Column
    private Long nextFireTime;

    @Column
    private Long previousFireTime;

    public static TriggerData of (String name, long startTime) {
        TriggerData data = new TriggerData();
        data.startTime = startTime;
        data.name = name;
        return data;
    }

    public Optional<Long> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    protected TriggerData setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Optional<Long> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    protected TriggerData setEndTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    public Optional<Long> getNextFireTime() {
        return Optional.ofNullable(nextFireTime);
    }

    protected TriggerData setNextFireTime(Long nextFireTime) {
        this.nextFireTime = nextFireTime;
        return this;
    }

    public Optional<Long> getPreviousFireTime() {
        return Optional.ofNullable(previousFireTime);
    }

    protected TriggerData setPreviousFireTime(Long previousFireTime) {
        this.previousFireTime = previousFireTime;
        return this;
    }

    public String getName() {
        return name;
    }
}
