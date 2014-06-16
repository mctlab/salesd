package com.mctlab.salesd.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mctlab.ansight.common.data.BaseData;
import com.mctlab.ansight.common.exception.JsonException;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.salesd.provider.TasksDatabaseHelper.Tables;

import java.lang.reflect.Type;

/**
 * Created by liqiang on 6/16/14.
 */
public class SyncData extends BaseData {

    private static final String KEY_TABLE = "table";

    protected String table;
    protected String operation;

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

    public static class Deserializer implements JsonDeserializer<SyncData> {

        @Override
        public SyncData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String table = jsonElement.getAsJsonObject().get(KEY_TABLE).getAsString();
            try {
                if (Tables.PROJECTS.equals(table)) {
                    return JsonMapper.parseJsonObject(jsonElement, SyncProject.class);
                } else {
                    return JsonMapper.parseJsonObject(jsonElement, UnknownSyncData.class);
                }
            } catch (JsonException e) {
                throw new JsonParseException(e);
            }
        }
    }

    public static final class UnknownSyncData extends SyncData {

        public UnknownSyncData() {
            this.table = "unknown";
        }
    }
}
