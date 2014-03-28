package com.mctlab.salesd.project;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.util.QueryHandler;

public class ProjectQueryHandler extends QueryHandler {

    public static final String[] PROJECT_PROJECTION = new String[] {
        ProjectsColumns._ID,
        ProjectsColumns.NAME,
        ProjectsColumns.ESTIMATED_AMOUNT,
        ProjectsColumns.PRIORITY,
        ProjectsColumns.STATUS,
        ProjectsColumns.DESCRIPTION
    };

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_AMOUNT = 2;
    public static final int COLUMN_INDEX_PRIORITY = 3;
    public static final int COLUMN_INDEX_STATUS = 4;
    public static final int COLUMN_INDEX_DESCRIPTION = 5;

    public ProjectQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public void startQueryProjects(int token) {
        startQuery(token, null, TasksProvider.PROJECTS_CONTENT_URI, PROJECT_PROJECTION,
                null, null, null);
    }

    public void startQueryProject(int token, long id) {
        if (id > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.PROJECTS_CONTENT_URI, id);
            startQuery(token, null, uri, PROJECT_PROJECTION, null, null, null);
        }
    }

    public String getName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(COLUMN_INDEX_NAME);
        }
        return null;
    }

    public int getAmount(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(COLUMN_INDEX_AMOUNT);
        }
        return 0;
    }

    public int getPriority(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(COLUMN_INDEX_PRIORITY);
        }
        return ProjectsColumns.PRIORITY_COMMON;
    }

    public int getStatus(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(COLUMN_INDEX_STATUS);
        }
        return ProjectsColumns.STATUS_NOT_STARTED;
    }

    public String getDescription(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(COLUMN_INDEX_DESCRIPTION);
        }
        return null;
    }

}
