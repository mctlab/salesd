/**
 * @(#)Project.java, 5/28/14.
 *
 * Copyright 2014 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.mctlab.archetype.war.data;

/**
 * @author liqiang
 */
public class Project {

    private long serverId;
    private String name;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
