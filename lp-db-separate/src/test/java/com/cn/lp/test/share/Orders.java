package com.cn.lp.test.share;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by qirong on 2019/5/17.
 */
@Table(name = "orders")
@Entity
public class Orders {

    @Id
    private long id;

    private long testID;

    private long sortID;

    public long getSortID() {
        return sortID;
    }

    public Orders setSortID(long sortID) {
        this.sortID = sortID;
        return this;
    }

    public long getId() {
        return id;
    }

    public long getTestID() {
        return testID;
    }

    public Orders setTestID(long testID) {
        this.testID = testID;
        return this;
    }

    public Orders setId(long id) {
        this.id = id;
        return this;
    }
}
