package com.mctlab.salesd.project;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import com.mctlab.salesd.provider.TasksDatabaseHelper.ConfigCategoriesColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ConfigColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.provider.TasksProvider;
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

    public static final int PROJECT_COLUMN_INDEX_ID = 0;
    public static final int PROJECT_COLUMN_INDEX_NAME = 1;
    public static final int PROJECT_COLUMN_INDEX_AMOUNT = 2;
    public static final int PROJECT_COLUMN_INDEX_PRIORITY = 3;
    public static final int PROJECT_COLUMN_INDEX_STATUS = 4;
    public static final int PROJECT_COLUMN_INDEX_DESCRIPTION = 5;

    public static final String[] CONFIG_CATEGORY_PROJECTION = new String[] {
        ConfigCategoriesColumns._ID,
        ConfigCategoriesColumns.CATEGORY,
        ConfigCategoriesColumns.SORT_INDEX
    };

    public static final int CONFIG_CATEGORY_COLUMN_INDEX_ID = 0;
    public static final int CONFIG_CATEGORY_COLUMN_INDEX_CATEGORY = 1;
    public static final int CONFIG_CATEGORY_COLUMN_INDEX_SORT_INDEX = 2;

    public static final String[] CONFIG_PROJECTION = new String[] {
        ConfigColumns._ID,
        ConfigColumns.PROJECT_ID,
        ConfigColumns.TYPE,
        ConfigColumns.NUMBER,
        ConfigCategoriesColumns.CATEGORY
    };

    public static final int CONFIG_COLUMN_INDEX_ID = 0;
    public static final int CONFIG_COLUMN_INDEX_PROJECT_ID = 1;
    public static final int CONFIG_COLUMN_INDEX_TYPE = 2;
    public static final int CONFIG_COLUMN_INDEX_NUMBER = 3;
    public static final int CONFIG_COLUMN_INDEX_CATEGORY = 4;

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

    public void startQueryConfigCategories(int token) {
        startQuery(token, null, TasksProvider.CONFIG_CATEGORIES_CONTENT_URI,
                CONFIG_CATEGORY_PROJECTION, null, null, ConfigCategoriesColumns.SORT_INDEX);
    }

    public void startQueryConfig(int token, long projectId) {
        if (projectId > 0) {
            String selection = ConfigColumns.PROJECT_ID + "=" + projectId;
            startQuery(token, null, TasksProvider.CONFIG_CONTENT_URI, CONFIG_PROJECTION,
                    selection, null, ConfigCategoriesColumns.SORT_INDEX);
        }
    }

    public String getProjectName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(PROJECT_COLUMN_INDEX_NAME);
        }
        return null;
    }

    public int getProjectAmount(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(PROJECT_COLUMN_INDEX_AMOUNT);
        }
        return 0;
    }

    public int getProjectPriority(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(PROJECT_COLUMN_INDEX_PRIORITY);
        }
        return ProjectsColumns.PRIORITY_COMMON;
    }

    public int getProjectStatus(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(PROJECT_COLUMN_INDEX_STATUS);
        }
        return ProjectsColumns.STATUS_NOT_STARTED;
    }

    public String getProjectDescription(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(PROJECT_COLUMN_INDEX_DESCRIPTION);
        }
        return null;
    }

    public String getConfigCategoryName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONFIG_CATEGORY_COLUMN_INDEX_CATEGORY);
        }
        return null;
    }

    public int getConfigCategorySortIndex(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CONFIG_CATEGORY_COLUMN_INDEX_SORT_INDEX);
        }
        return 0;
    }

    public String getConfigType(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONFIG_COLUMN_INDEX_TYPE);
        }
        return null;
    }

    public int getConfigNumber(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CONFIG_COLUMN_INDEX_NUMBER);
        }
        return 0;
    }

    public String getConfigCategory(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONFIG_COLUMN_INDEX_CATEGORY);
        }
        return null;
    }
}
