package com.cn.lp.dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 2019/7/23.
 */
public class SearchCondition {

    private ConditionType conditionType;

    private List<String> argList = new ArrayList<>();

    public ConditionType getConditionType() {
        return conditionType;
    }

    public SearchCondition setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
        return this;
    }

    public List<String> getArgList() {
        return argList;
    }

    public SearchCondition setArgList(List<String> argList) {
        this.argList = argList;
        return this;
    }
}
