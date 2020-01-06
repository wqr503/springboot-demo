package com.cn.lp.dynamic;

import com.cn.lp.base.MongoDBSupport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by on 2019/7/24.
 */
@Service
public class MongoDBManager implements MongoDBSupport {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加record，并可指定collection
     * @param recordID
     * @param collectionName
     * @param extList
     */
    public void addRecord(long recordID, String collectionName, List<RecordExt> extList) {
        var record = Record.of(recordID);
        extList.stream().forEach(record::addExt);
        insert(record, collectionName);
    }

    public Page<Record> searchRecords(SearchRequest request) {
        var where = new Criteria();

        // id
        var idConditions = request.getRecordIDConditionList();
        if (idConditions != null && !idConditions.isEmpty()) {
            where = where.and(Record.RECORD_ID);
            for (var idCondition : idConditions) {
                where = appendCondition(where, idCondition.getConditionType(), ExtValueType.LONG, idCondition.getArgList());
            }
        }

        // time
        var createTimeConditions = request.getCreateTimeConditionList();
        if (createTimeConditions != null && !createTimeConditions.isEmpty()) {
            where = where.and(Record.CREATE_TIME);
            for (var createTimeCondition : createTimeConditions) {
                where = appendCondition(where, createTimeCondition.getConditionType(), ExtValueType.DATE, createTimeCondition.getArgList());
            }
        }

        // ext
        Map<Integer, Object> extMap = Maps.newHashMap();
        for (var extCondition : request.getExtConditionList()) {
            if (!extMap.containsKey(extCondition.getExtType().getId())) {
                extMap.put(extCondition.getExtType().getId(), extCondition);
            } else {
                var condition = extMap.get(extCondition.getExtType().getId());
                if (condition instanceof SearchExtCondition) {
                    var conditionList = Lists.newArrayList((SearchExtCondition) condition);
                    extMap.put(extCondition.getExtType().getId(), conditionList);
                } else if (condition instanceof List) {
                    var list = (List<SearchExtCondition>) condition;
                    list.add(extCondition);
                }
            }
        }

        for (var kv : extMap.entrySet()) {
            var extType = RecordExtType.get(kv.getKey());
            where = where.and(extType.getFieldName("ext."));
            var obj = kv.getValue();
            if (obj instanceof SearchExtCondition) {
                var extCondition = (SearchExtCondition) obj;
                where = appendCondition(where, extCondition.getConditionType(), extCondition.getExtType().getValueType(), extCondition.getArgList());
            } else if (obj instanceof List) {
                var list = (List<SearchExtCondition>) obj;
                for (var c : list) {
                    where = appendCondition(where, c.getConditionType(), c.getExtType().getValueType(), c.getArgList());
                }
            }
        }

        // 分页
        var pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), new Sort(Sort.Direction.DESC, Record.RECORD_ID));
        return findPage(Record.class, request.getCollectionName(), query(where), pageable);
    }

    private Criteria appendCondition(Criteria where, ConditionType type, ExtValueType valueType, List<String> args) {
        switch (type) {
            case EQ:
                return where.is(valueType.toValue(args.get(0)));
            case NOT_EQ:
                return where.is(valueType.toValue(args.get(0))).not();
            case GT:
                return where.gt(valueType.toValue(args.get(0)));
            case GE:
                return where.gte(valueType.toValue(args.get(0)));
            case LT:
                return where.lt(valueType.toValue(args.get(0)));
            case LE:
                return where.lte(valueType.toValue(args.get(0)));
            case IN:
                return where.in(args.stream().map(valueType::toValue).collect(Collectors.toList()));
            case NOT_IN:
                return where.nin(args.stream().map(valueType::toValue).collect(Collectors.toList()));
            case LIKE:
                return where.regex("/".concat((String) valueType.toValue(args.get(0))).concat("/"));
        }
        throw new IllegalArgumentException();
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return this.mongoTemplate;
    }
}
