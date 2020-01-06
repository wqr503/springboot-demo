package com.cn.lp.test.noshare;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by qirong on 2019/5/17.
 */
@Repository
public interface OrdersDetailDAO extends JpaRepository<OrdersDetail, Long>, JpaSpecificationExecutor<OrdersDetail> {

    List<OrdersDetail> findByTestIDOrderByTestIDDesc(long id);

}
