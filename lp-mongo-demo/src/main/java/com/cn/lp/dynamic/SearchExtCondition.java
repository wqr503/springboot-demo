package com.cn.lp.dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 2019/7/23.
 */
public class SearchExtCondition {

    private RecordExtType extType;

    private ConditionType conditionType;

    private List<String> argList = new ArrayList<>();

    public RecordExtType getExtType() {
        return extType;
    }

    public SearchExtCondition setExtType(RecordExtType extType) {
        this.extType = extType;
        return this;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public SearchExtCondition setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
        return this;
    }

    public List<String> getArgList() {
        return argList;
    }

    public SearchExtCondition setArgList(List<String> argList) {
        this.argList = argList;
        return this;
    }
}
