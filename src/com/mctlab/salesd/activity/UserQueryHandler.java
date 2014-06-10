package com.mctlab.salesd.activity;

import android.content.ContentResolver;
import android.database.Cursor;

import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.UsersColumns;
import com.mctlab.salesd.util.QueryHandler;

public class UserQueryHandler extends QueryHandler {

    public static final String[] USER_PROJECTION = new String[] {
        UsersColumns._ID,
        UsersColumns.ACCOUNT,
        UsersColumns.NAME,
        UsersColumns.ANNUAL_TARGET,
        UsersColumns.COMPLETED
    };

    public static final int USER_COLUMN_INDEX_ID = 0;
    public static final int USER_COLUMN_INDEX_ACCOUNT = 1;
    public static final int USER_COLUMN_INDEX_NAME = 2;
    public static final int USER_COLUMN_INDEX_ANNUAL_TARGET = 3;
    public static final int USER_COLUMN_INDEX_COMPLETED = 4;

    public UserQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public void startQueryUser(int token) {
        startQueryUser(token, 0L);
    }

    public void startQueryUser(int token, long id) {
        String where = null;
        if (id > 0L) {
            where = UsersColumns._ID + "=" + id;
        }
        startQuery(token, null, TasksProvider.USERS_CONTENT_URI, USER_PROJECTION,
                where, null, UsersColumns._ID + " asc");
    }

    public long getUserId(Cursor cursor) {
        if (cursor != null) {
            return cursor.getLong(USER_COLUMN_INDEX_ID);
        }
        return 0L;
    }

    public String getUserName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(USER_COLUMN_INDEX_NAME);
        }
        return null;
    }

    public int getUserAnnualTarget(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(USER_COLUMN_INDEX_ANNUAL_TARGET);
        }
        return 0;
    }

    public int getUserCompleted(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(USER_COLUMN_INDEX_COMPLETED);
        }
        return 0;
    }
}
