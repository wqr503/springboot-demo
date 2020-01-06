package com.cn.lp.dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 2019/7/19.
 */
public class SearchRequest {

    private String collectionName;

    private int pageSize;

    private int pageNum;

    private List<SearchCondition> recordIDConditionList = new ArrayList<>();

    private List<SearchCondition> createTimeConditionList = new ArrayList<>();

    private List<SearchExtCondition> extConditionList = new ArrayList<>();

    public List<SearchExtCondition> getExtConditionList() {
        return extConditionList;
    }

    public SearchRequest setExtConditionList(List<SearchExtCondition> extConditionList) {
        this.extConditionList = extConditionList;
        return this;
    }

    public List<SearchCondition> getCreateTimeConditionList() {
        return createTimeConditionList;
    }

    public SearchRequest setCreateTimeConditionList(List<SearchCondition> createTimeConditionList) {
        this.createTimeConditionList = createTimeConditionList;
        return this;
    }

    public List<SearchCondition> getRecordIDConditionList() {
        return recordIDConditionList;
    }

    public SearchRequest setRecordIDConditionList(List<SearchCondition> recordIDConditionList) {
        this.recordIDConditionList = recordIDConditionList;
        return this;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public SearchRequest setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public SearchRequest setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getPageNum() {
        return pageNum;
    }

    public SearchRequest setPageNum(int pageNum) {
        this.pageNum = pageNum;
        return this;
    }

}
