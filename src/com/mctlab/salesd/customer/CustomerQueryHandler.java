package com.mctlab.salesd.customer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.CustomersColumns;
import com.mctlab.salesd.util.QueryHandler;

public class CustomerQueryHandler extends QueryHandler {

    public static final String[] PROJECT_PROJECTION = new String[] {
        CustomersColumns._ID,
        CustomersColumns.NAME,
        CustomersColumns.COMPANY_ADDRESS,
        CustomersColumns.CATEGORY,
        CustomersColumns.BUSINESS_DESCRIPTION
    };

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_ADDRESS = 2;
    public static final int COLUMN_INDEX_CATEGORY = 3;
    public static final int COLUMN_INDEX_DESCRIPTION = 4;

    public CustomerQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public void startQueryCustomers(int token) {
        startQuery(token, null, TasksProvider.CUSTOMERS_CONTENT_URI, PROJECT_PROJECTION,
                null, null, null);
    }

    public void startQueryCustomer(int token, long id) {
        if (id > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.CUSTOMERS_CONTENT_URI, id);
            startQuery(token, null, uri, PROJECT_PROJECTION, null, null, null);
        }
    }

    public String getName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(COLUMN_INDEX_NAME);
        }
        return null;
    }

    public String getAddress(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(COLUMN_INDEX_ADDRESS);
        }
        return null;
    }

    public int getCategory(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(COLUMN_INDEX_CATEGORY);
        }
        return CustomersColumns.CATEGORY_HOST_MANUFACTURER;
    }

    public String getDescription(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(COLUMN_INDEX_DESCRIPTION);
        }
        return null;
    }
}
