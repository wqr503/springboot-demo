package com.cn.lp.test;

import com.cn.lp.test.noshare.OrdersDetail;
import com.cn.lp.test.noshare.OrdersDetailDAO;
import com.cn.lp.test.share.Orders;
import com.cn.lp.test.share.OrdersDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})// 指定启动类
public class ApplicationTests {

    @Autowired
    private OrdersDAO ordersDAO;

    @Autowired
    private OrdersDetailDAO ordersDetailDAO;

    /**
     * 测试根据散表字段搜索
     */
    @Test
    public void testFindShareColumn() {
        System.out.println("------------------testFindShareColumn---------------------");
        Optional<Orders> optional = ordersDAO.findById(1L);
        optional.ifPresent(System.out::println);
        System.out.println("-------------------testFindShareColumn--------------------");
    }

    /**
     * 测试根据非散表字段搜索
     */
    @Test
    public void testFindNotShareColumn() {
        System.out.println("------------------testFindNotShareColumn---------------------");
        List<Orders> dataList = ordersDAO.findByTestIDOrderByTestIDDesc(1L);
        for(Orders orders : dataList) {
            System.out.println(orders.getId());
        }
        System.out.println("------------------testFindNotShareColumn---------------------");
    }

    /**
     * 测试分页查询
     */
    @Test
    public void testPageFind() {
        System.out.println("------------------testPageFind---------------------");
        Page<Orders> page = ordersDAO.findAll((Specification) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("testID"), 1),
            PageRequest.of(0, 2, Sort.by("sortID")));
        for(Orders orders : page) {
            System.out.println(orders.getId());
        }
        System.out.println("------------------testPageFind---------------------");
    }

    /**
     * 测试Save分表
     */
    @Test
    public void testSaveShareTable() {
        System.out.println("------------------testSaveShareTable---------------------");
        Orders orders = new Orders();
        orders.setId(1);
        orders.setTestID(0);
        orders.setSortID(1);
        ordersDAO.save(orders);

        orders = new Orders();
        orders.setId(2);
        orders.setTestID(0);
        orders.setSortID(3);
        ordersDAO.save(orders);

        orders = new Orders();
        orders.setId(3);
        orders.setTestID(1);
        orders.setSortID(2);
        ordersDAO.save(orders);

        orders = new Orders();
        orders.setId(4);
        orders.setTestID(0);
        orders.setSortID(4);
        ordersDAO.save(orders);
        System.out.println("------------------testSaveShareTable---------------------");

    }

    /**
     * 测试Save不分表
     */
    @Test
    public void testSaveNoShareTable() {
        System.out.println("------------------testSaveNoShareTable---------------------");
        OrdersDetail ordersDetail = new OrdersDetail();
        ordersDetail.setOrders_id(1);
        ordersDetail.setTestID(0);
        ordersDetail.setSortID(1);
        ordersDetailDAO.save(ordersDetail);

        ordersDetail = new OrdersDetail();
        ordersDetail.setOrders_id(2);
        ordersDetail.setTestID(0);
        ordersDetail.setSortID(2);
        ordersDetailDAO.save(ordersDetail);

        ordersDetail = new OrdersDetail();
        ordersDetail.setOrders_id(3);
        ordersDetail.setTestID(0);
        ordersDetail.setSortID(3);
        ordersDetailDAO.save(ordersDetail);

        ordersDetail = new OrdersDetail();
        ordersDetail.setOrders_id(4);
        ordersDetail.setTestID(1);
        ordersDetail.setSortID(4);
        ordersDetailDAO.save(ordersDetail);
        System.out.println("------------------testSaveNoShareTable---------------------");
    }

}