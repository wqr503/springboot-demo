package com.cn.lp;

import com.cn.lp.domain.Item;
import com.cn.lp.domain.ItemRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by qirong on 2019/4/15.
 */
@Api(tags = "测试API")
@RestController
public class TestController {

    @Autowired
    private ItemRepository itemRepository;

    @ApiOperation(value = "测试保存", notes = "测试保存")
    @GetMapping("/test")
    public String testSave() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "小米手机7", "手机",
            "小米", 3499.00, "http://image.baidu.com/13123.jpg"
        ));
        items.add(new Item(2L, "小米手机9", "手机",
            "小米", 4000.00, "http://image.baidu.com/13123.jpg"
        ));
        items.add(new Item(3L, "华为pro", "手机",
            "华为", 2000.00, "http://image.baidu.com/13123.jpg"
        ));
        items.add(new Item(4L, "魅族", "手机",
            "魅族", 3000.00, "http://image.baidu.com/13123.jpg"
        ));
        itemRepository.saveAll(items);
        return "success";
    }

    @ApiOperation(value = "测试搜索", notes = "测试搜索")
    @GetMapping("/testFind")
    public String testFind() {
        Optional<Item> optional = itemRepository.findById(1L);
        return optional.isPresent() ? "id : " + optional.get().getId() + "" : "null";
    }

    @ApiOperation(value = "测试匹配", notes = "测试匹配")
    @GetMapping("/testMatch")
    public String testMatchQuery() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机"));
        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();

        System.out.println("--------------Match查询-------------------");
        for (Item item : items) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + total;
    }

    /**
     * @Description: termQuery:功能更强大，除了匹配字符串以外，还可以匹配
     * int/long/double/float/....
     */
    @ApiOperation(value = "测试termQuery", notes = "测试termQuery")
    @GetMapping("/testTerm")
    public String testTermQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.termQuery("price", 3499.00));
        // 查找
        Page<Item> page = this.itemRepository.search(builder.build());

        System.out.println("--------------termQuery查询-------------------");
        for (Item item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + page.getTotalElements();
    }

    /**
     * @Description:布尔查询
     */
    @ApiOperation(value = "测试布尔查询", notes = "测试布尔查询")
    @GetMapping("/testBoolean")
    public String testBooleanQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withQuery(
            QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", "华为"))
                .must(QueryBuilders.matchQuery("brand", "华为"))
        );

        // 查找
        Page<Item> page = this.itemRepository.search(builder.build());

        System.out.println("--------------布尔查询-------------------");
        for (Item item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + page.getTotalElements();
    }

    /**
     * @Description:模糊查询
     */
    @ApiOperation(value = "测试模糊查询", notes = "测试模糊查询")
    @GetMapping("/testFuzzy")
    public String testFuzzyQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.fuzzyQuery("title", "小米"));
        Page<Item> page = this.itemRepository.search(builder.build());
        System.out.println("--------------模糊查询-------------------");
        for (Item item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + page.getTotalElements();
    }

    /**
     * @Description:分页查询
     */
    @ApiOperation(value = "测试分页查询", notes = "测试分页查询")
    @GetMapping("/testPage")
    public String searchByPage() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        // 分页：
        int page = 0;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page, size));

        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);
        // 总页数
        System.out.println("总页数 = " + items.getTotalPages());
        // 当前页
        System.out.println("当前页：" + items.getNumber());
        // 每页大小
        System.out.println("每页大小：" + items.getSize());

        System.out.println("--------------分页查询-------------------");
        for (Item item : items) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + items.getTotalElements();
    }

    /**
     * @Description:排序查询
     */
    @ApiOperation(value = "测试排序查询", notes = "测试排序查询")
    @GetMapping("/testSort")
    public String searchAndSort() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));

        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));

        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);

        System.out.println("---------------排序查询------------------");
        for (Item item : items) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
            System.out.println("price :" + item.getPrice());
        }
        System.out.println("---------------------------------");
        return "total = " + items.getTotalElements();
    }

    /**
     * @Description:按照品牌brand进行分组
     */
    @ApiOperation(value = "测试按照品牌brand进行分组", notes = "测试按照品牌brand进行分组")
    @GetMapping("/testAgg")
    public String testAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
            AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        System.out.println("----------------分组-----------------");
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println("品牌 :" + bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println("数量 : " + bucket.getDocCount());
        }
        System.out.println("---------------------------------");
        return "total = " + buckets.size();
    }

    /**
     * @Description:嵌套聚合，求平均值
     */
    @ApiOperation(value = "测试嵌套聚合，求平均值", notes = "测试嵌套聚合，求平均值")
    @GetMapping("/testSubAgg")
    public String testSubAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
            AggregationBuilders.terms("brands").field("brand")
                .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        System.out.println("--------------嵌套聚合-------------------");
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println("品牌 :" + bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");

            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
        }
        System.out.println("---------------------------------");
        return "total = " + buckets.size();

    }

}
