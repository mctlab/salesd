package com.mctlab.salesd.util;

import java.util.HashMap;

import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.SDTableVersionsColumns;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

public class TableUtil {
    private static final String[] TABLE_VERSION_PROJECTION = new String[] {
        SDTableVersionsColumns._ID,
        SDTableVersionsColumns.TABLE,
        SDTableVersionsColumns.VERSION
    };

    private static final int TABLE_VERSION_COLUMN_INDEX_TABLE = 1;
    private static final int TABLE_VERSION_COLUMN_INDEX_VERSION = 2;

    private static boolean sInitialized = false;

    private static HashMap<String, Long> mTableVersions = new HashMap<String, Long>();

    private static void init(ContentResolver resolver) {
        if (!sInitialized) {
            synchronized (mTableVersions) {
                Cursor cursor = null;
                try {
                    cursor = resolver.query(TasksProvider.SD_TABLE_VERSIONS_CONTENT_URI,
                            TABLE_VERSION_PROJECTION, null, null, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String table = cursor.getString(TABLE_VERSION_COLUMN_INDEX_TABLE);
                            Long version = cursor.getLong(TABLE_VERSION_COLUMN_INDEX_VERSION);
                            if (!TextUtils.isEmpty(table) && version != null) {
                                mTableVersions.put(table, version);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                sInitialized = true;
            }
        }
    }

    public static long getTableVersion(ContentResolver resolver, String table) {
        init(resolver);

        long version = 0;
        synchronized (mTableVersions) {
            if (!TextUtils.isEmpty(table) && mTableVersions.containsKey(table)) {
                version = mTableVersions.get(table);
            }
        }

        LogUtil.v("Get table '" + table + "' version: " + version);
        return version;
    }

    public static void setTableVersion(ContentResolver resolver, String table, Long version) {
        init(resolver);

        synchronized (mTableVersions) {
            if (!TextUtils.isEmpty(table)) {
                boolean succeed = false;

                ContentValues values = new ContentValues();
                values.put(SDTableVersionsColumns.TABLE, table);
                values.put(SDTableVersionsColumns.VERSION, version);

                if (mTableVersions.containsKey(table)) {
                    String where = SDTableVersionsColumns.TABLE + "='" + table + "'";
                    succeed = resolver.update(TasksProvider.SD_TABLE_VERSIONS_CONTENT_URI,
                            values, where, null) > 0;
                } else {
                    succeed = resolver.insert(TasksProvider.SD_TABLE_VERSIONS_CONTENT_URI,
                            values) != null;
                }

                if (succeed) {
                    LogUtil.v("Set table '" + table + "' version: " + version);
                    mTableVersions.put(table, version);
                }
            }
        }
    }
}
