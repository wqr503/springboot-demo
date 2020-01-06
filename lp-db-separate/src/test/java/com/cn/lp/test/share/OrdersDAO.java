package com.cn.lp.test.share;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by qirong on 2019/5/17.
 */
@Repository
public interface OrdersDAO extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {

    List<Orders> findByTestIDOrderByTestIDDesc(long id);

}
