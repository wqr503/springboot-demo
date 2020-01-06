package com.cn.lp;

import com.cn.lp.domain.DemoDAO;
import com.cn.lp.domain.DemoPO;
import com.cn.lp.dynamic.*;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 测试geo相关方法
 * Created by qirong on 2019/4/10.
 */
@RestController
public class TestController {

    @Autowired
    private DemoDAO demoDAO;

    @Autowired
    private MongoDBManager mongoDBManager;

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @ApiOperation(value = "测试保存", notes = "测试保存")
    @GetMapping("testSave")
    @ResponseBody
    public String testSave() {
        DemoPO demoPO = DemoPO.of("test1", "test", new Point(1, 1));
        demoDAO.save(demoPO);
        return "success";
    }

    @ApiOperation(value = "测试动态记录保存", notes = "测试动态记录保存")
    @GetMapping("testDynamicRecord")
    @ResponseBody
    public String testDynamicRecord() {
        List<RecordExt> extList = new ArrayList<>();
        extList.add(RecordExt.of(RecordExtType.TEST_ATR, "test"));
        mongoDBManager.addRecord(System.currentTimeMillis(), "testRecord", extList);
        return "success";
    }

    //嵌套子查询
    @ApiOperation(value = "测试获取动态记录", notes = "测试获取动态记录")
    @PostMapping("testGetDynamicRecord")
    @ResponseBody
    public String testGetDynamicRecord() {
        SearchRequest searchRequest = new SearchRequest();
        List<SearchExtCondition> extConditionList = new ArrayList<>();
        SearchExtCondition extCondition = new SearchExtCondition()
            .setExtType(RecordExtType.TEST_ATR)
            .setConditionType(ConditionType.EQ)
            .setArgList(List.of("test"));
        extConditionList.add(extCondition);
        searchRequest.setPageNum(1)
            .setPageSize(4)
            .setCollectionName("testRecord")
            .setExtConditionList(extConditionList);
        var result = mongoDBManager.searchRecords(searchRequest);
        return result.getTotalElements() + "";
    }


    //嵌套子查询
    @ApiOperation(value = "测试获取", notes = "测试获取")
    @GetMapping("testGet")
    @ResponseBody
    public String testGet() {
        Optional<DemoPO> optional = demoDAO.findById("test1");
        if (optional.isPresent()) {
            return optional.get().getName();
        }
        return "fail";
    }

    @ApiOperation(value = "测试获取范围内数据", notes = "测试获取范围内数据")
    @GetMapping("testWithin")
    @ResponseBody
    public String testWithin() {
        logger.info("test");
        List<DemoPO> dataList = demoDAO.findByLocationWithin(new Circle(new Point(10, 10), 1));
        logger.info("坐标10 范围1之内的个数为 : " + dataList.size());
        dataList = demoDAO.findByLocationWithin(new Circle(new Point(1, 1), 1));
        logger.info("坐标1 范围1之内的个数为 : " + dataList.size());
        return dataList.size() + "";
    }

    @ApiOperation(value = "测试坐标范围内", notes = "测试坐标范围内")
    @GetMapping("testLocationWithin")
    @ResponseBody
    public String testLocationWithin() {
        List<DemoPO> dataList = demoDAO.findByLocationWithin(new Box(new Point(1, 1), new Point(10, 10)));
        logger.info("坐标1,1 到 10, 10 正方形范围之内的个数为 : " + dataList.size());
        return dataList.size() + "";
    }

}
