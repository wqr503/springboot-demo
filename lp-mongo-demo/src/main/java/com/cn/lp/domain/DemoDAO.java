package com.cn.lp.domain;

import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DemoDAO extends MongoRepository<DemoPO, String> {

    /**
     * 查找坐标半径之内的数据
     *
     * @param circle
     * @return
     */
    List<DemoPO> findByLocationWithin(Circle circle);


    /**
     * 查找矩形之内的数据（拉框）
     *
     * @param box
     * @return
     */
    List<DemoPO> findByLocationWithin(Box box);

    /**
     * 查找坐标附近数据
     * 需要索引
     *
     * @return
     */
//    List<DemoPO> findByLocationNear(Point point, Distance min, Distance max);
    //findByPointNear(Point point, Distance max)
    //findByPointNear(Point point)
}
