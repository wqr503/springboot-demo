package com.cn.lp.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

public interface MongoDBSupport {

    MongoTemplate getMongoTemplate();

    default Query query(CriteriaDefinition criteriaDefinition) {
        return Query.query(criteriaDefinition);
    }

    default Criteria where(String key) {
        return Criteria.where(key);
    }

    default <T> Page<T> findPage(Class<T> clazz, Query query, Pageable pageable) {
        var count = getMongoTemplate().count(query, clazz);
        var items = getMongoTemplate().find(query.with(pageable), clazz);
        return new PageImpl<>(items, pageable, count);
    }

    default <T> Page<T> findPage(Class<T> clazz, String collectionName, Query query, Pageable pageable) {
        var count = getMongoTemplate().count(query, clazz, collectionName);
        var items = getMongoTemplate().find(query.with(pageable), clazz, collectionName);
        return new PageImpl<>(items, pageable, count);
    }

    default <T> T save(T objToSave) {
        return getMongoTemplate().save(objToSave);
    }

    default <T> T save(T objToSave, String collectionName) {
        return getMongoTemplate().save(objToSave, collectionName);
    }

    default <T> T insert(T objToSave) {
        return getMongoTemplate().insert(objToSave);
    }

    default <T> T insert(T objToSave, String collectionName) {
        return getMongoTemplate().insert(objToSave, collectionName);
    }

}
