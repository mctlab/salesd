package com.mctlab.salesd.data;

import com.mctlab.ansight.common.data.BaseData;

/**
 * @author liqiang
 */
public class ClassB extends BaseData {

    private int type;
    private String name;
    private int[] refIds;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getRefIds() {
        return refIds;
    }

    public void setRefIds(int[] refIds) {
        this.refIds = refIds;
    }
}
