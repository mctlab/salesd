package cn.mctlab.archetype.war.data;

/**
 * Created by liqiang on 6/12/14.
 */
public class SyncData {

    private String table;
    private String operation;
    private BaseData data;

    public SyncData(String table, String operation, BaseData data) {
        this.table = table;
        this.operation = operation;
        this.data = data;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public BaseData getData() {
        return data;
    }

    public void setData(BaseData data) {
        this.data = data;
    }
}
