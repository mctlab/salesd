package com.mctlab.salesd.data;

import com.mctlab.ansight.common.data.BaseData;

/**
 * @author liqiang
 */
public class ClassA extends BaseData {

    private int type;
    private String name;
    private ClassB[] b;

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

    public ClassB[] getB() {
        return b;
    }

    public void setB(ClassB[] b) {
        this.b = b;
    }
}
