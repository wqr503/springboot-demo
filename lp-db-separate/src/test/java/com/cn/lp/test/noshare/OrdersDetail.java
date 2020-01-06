package com.cn.lp.test.noshare;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by qirong on 2019/5/17.
 */
@Table(name = "orders_detail")
@Entity
public class OrdersDetail {

    @Id
    private long orders_id;

    private long testID;

    private long sortID;

    public long getSortID() {
        return sortID;
    }

    public OrdersDetail setSortID(long sortID) {
        this.sortID = sortID;
        return this;
    }

    public long getTestID() {
        return testID;
    }

    public OrdersDetail setTestID(long testID) {
        this.testID = testID;
        return this;
    }

    public long getOrders_id() {
        return orders_id;
    }

    public OrdersDetail setOrders_id(long orders_id) {
        this.orders_id = orders_id;
        return this;
    }

}
