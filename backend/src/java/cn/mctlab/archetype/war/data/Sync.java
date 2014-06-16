package cn.mctlab.archetype.war.data;

/**
 * Created by liqiang on 6/16/14.
 */
public class Sync extends BaseData {

    private String table;
    private long version;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
