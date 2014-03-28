package com.mctlab.salesd.provider;

import com.mctlab.salesd.provider.TasksDatabaseHelper.Tables;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class TasksProvider extends ContentProvider {

    protected static final String AUTHORITY = "salesd";
    protected static final String URI_AUTHORITY_PREFIX = "content://" + AUTHORITY + "/";

    public static final Uri USERS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "users");

    protected static final String USERS_TYPE = "vnd.android.cursor.dir/user";
    protected static final String USERS_ITEM_TYPE = "vnd.android.cursor.item/user";

    public static final Uri PROJECTS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "projects");

    protected static final String PROJECTS_TYPE = "vnd.android.cursor.dir/project";
    protected static final String PROJECTS_ITEM_TYPE = "vnd.android.cursor.item/project";

    public static final Uri CONFIG_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "config");

    protected static final String CONFIG_TYPE = "vnd.android.cursor.dir/config";
    protected static final String CONFIG_ITEM_TYPE = "vnd.android.cursor.item/config";

    public static final Uri CUSTOMERS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "customers");

    protected static final String CUSTOMERS_TYPE = "vnd.android.cursor.dir/customer";
    protected static final String CUSTOMERS_ITEM_TYPE = "vnd.android.cursor.item/customer";

    public static final Uri CONTACTS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "contacts");

    protected static final String CONTACTS_TYPE = "vnd.android.cursor.dir/contact";
    protected static final String CONTACTS_ITEM_TYPE = "vnd.android.cursor.item/contact";

    public static final Uri PROCUSTS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "procusts");

    protected static final String PROCUSTS_TYPE = "vnd.android.cursor.dir/procust";
    protected static final String PROCUSTS_ITEM_TYPE = "vnd.android.cursor.item/procust";

    public static final Uri REMINDERS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "reminders");

    protected static final String REMINDERS_TYPE = "vnd.android.cursor.dir/reminder";
    protected static final String REMINDERS_ITEM_TYPE = "vnd.android.cursor.item/reminder";

    public static final Uri SCHEDULES_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "schedules");

    protected static final String SCHEDULES_TYPE = "vnd.android.cursor.dir/schedule";
    protected static final String SCHEDULES_ITEM_TYPE = "vnd.android.cursor.item/schedule";

    protected static final int USERS = 10;
    protected static final int USERS_ID = 11;
    protected static final int PROJECTS = 20;
    protected static final int PROJECTS_ID = 21;
    protected static final int CONFIG = 30;
    protected static final int CONFIG_ID = 31;
    protected static final int CUSTOMERS = 40;
    protected static final int CUSTOMERS_ID = 41;
    protected static final int CONTACTS = 50;
    protected static final int CONTACTS_ID = 51;
    protected static final int PROCUSTS = 60;
    protected static final int PROCUSTS_ID = 61;
    protected static final int REMINDERS = 70;
    protected static final int REMINDERS_ID = 71;
    protected static final int SCHEDULES = 80;
    protected static final int SCHEDULES_ID = 81;

    protected static final UriMatcher sUriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "users", USERS);
        sUriMatcher.addURI(AUTHORITY, "users/#", USERS_ID);
        sUriMatcher.addURI(AUTHORITY, "projects", PROJECTS);
        sUriMatcher.addURI(AUTHORITY, "projects/#", PROJECTS_ID);
        sUriMatcher.addURI(AUTHORITY, "config", CONFIG);
        sUriMatcher.addURI(AUTHORITY, "config/#", CONFIG_ID);
        sUriMatcher.addURI(AUTHORITY, "customers", CUSTOMERS);
        sUriMatcher.addURI(AUTHORITY, "customers/#", CUSTOMERS_ID);
        sUriMatcher.addURI(AUTHORITY, "contacts", CONTACTS);
        sUriMatcher.addURI(AUTHORITY, "contacts/#", CONTACTS_ID);
        sUriMatcher.addURI(AUTHORITY, "procusts", PROCUSTS);
        sUriMatcher.addURI(AUTHORITY, "procusts/#", PROCUSTS_ID);
        sUriMatcher.addURI(AUTHORITY, "reminders", REMINDERS);
        sUriMatcher.addURI(AUTHORITY, "reminders/#", REMINDERS_ID);
        sUriMatcher.addURI(AUTHORITY, "schedules", SCHEDULES);
        sUriMatcher.addURI(AUTHORITY, "schedules/#", SCHEDULES_ID);
    }

    private TasksDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        mDbHelper = new TasksDatabaseHelper(getContext());
        mDb = mDbHelper.getWritableDatabase();
        return false;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case USERS:
            return USERS_TYPE;
        case USERS_ID:
            return USERS_ITEM_TYPE;
        case PROJECTS:
            return PROJECTS_TYPE;
        case PROJECTS_ID:
            return PROJECTS_ITEM_TYPE;
        case CONFIG:
            return CONFIG_TYPE;
        case CONFIG_ID:
            return CONFIG_ITEM_TYPE;
        case CUSTOMERS:
            return CUSTOMERS_TYPE;
        case CUSTOMERS_ID:
            return CUSTOMERS_ITEM_TYPE;
        case CONTACTS:
            return CONTACTS_TYPE;
        case CONTACTS_ID:
            return CONTACTS_ITEM_TYPE;
        case PROCUSTS:
            return PROCUSTS_TYPE;
        case PROCUSTS_ID:
            return PROCUSTS_ITEM_TYPE;
        case REMINDERS:
            return REMINDERS_TYPE;
        case REMINDERS_ID:
            return REMINDERS_ITEM_TYPE;
        case SCHEDULES:
            return SCHEDULES_TYPE;
        case SCHEDULES_ID:
            return SCHEDULES_ITEM_TYPE;
        }

        throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case USERS:
            table = Tables.USERS;
            break;
        case PROJECTS:
            table = Tables.PROJECTS;
            break;
        case CONFIG:
            table = Tables.CONFIG;
            break;
        case CUSTOMERS:
            table = Tables.CUSTOMERS;
            break;
        case CONTACTS:
            table = Tables.CONTACTS;
            break;
        case PROCUSTS:
            table = Tables.PROCUSTS;
            break;
        case REMINDERS:
            table = Tables.REMINDERS;
            break;
        case SCHEDULES:
            table = Tables.SCHEDULES;
            break;
        default:
            throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
        }

        long id = mDb.insert(table, null, values);
        if (id >= 0) {
            return ContentUris.withAppendedId(uri, id);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        String table = null;
        String selectionAppend = null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case USERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case USERS:
            table = Tables.USERS;
            break;
        case PROJECTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case PROJECTS:
            table = Tables.PROJECTS;
            break;
        case CONFIG_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONFIG:
            table = Tables.CONFIG;
            break;
        case CUSTOMERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CUSTOMERS:
            table = Tables.CUSTOMERS;
            break;
        case CONTACTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONTACTS:
            table = Tables.CONTACTS;
            break;
        case PROCUSTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case PROCUSTS:
            table = Tables.PROCUSTS;
            break;
        case REMINDERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case REMINDERS:
            table = Tables.REMINDERS;
            break;
        case SCHEDULES_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case SCHEDULES:
            table = Tables.SCHEDULES;
            break;
        default:
            throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
        }

        if (selectionAppend != null) {
            if (!TextUtils.isEmpty(selection)) {
                selection = selectionAppend + " AND (" + selection + ")";
            } else {
                selection = selectionAppend;
            }
        }

        int count = mDb.update(table, values, selection, selectionArgs);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = null;
        String selectionAppend = null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case USERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case USERS:
            table = Tables.USERS;
            break;
        case PROJECTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case PROJECTS:
            table = Tables.PROJECTS;
            break;
        case CONFIG_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONFIG:
            table = Tables.CONFIG;
            break;
        case CUSTOMERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CUSTOMERS:
            table = Tables.CUSTOMERS;
            break;
        case CONTACTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONTACTS:
            table = Tables.CONTACTS;
            break;
        case PROCUSTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case PROCUSTS:
            table = Tables.PROCUSTS;
            break;
        case REMINDERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case REMINDERS:
            table = Tables.REMINDERS;
            break;
        case SCHEDULES_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case SCHEDULES:
            table = Tables.SCHEDULES;
            break;
        default:
            throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
        }

        if (selectionAppend != null) {
            if (!TextUtils.isEmpty(selection)) {
                selection = selectionAppend + " AND (" + selection + ")";
            } else {
                selection = selectionAppend;
            }
        }

        int count = mDb.delete(table, selection, selectionArgs);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        final int match = sUriMatcher.match(uri);
        switch (match) {
        case USERS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case USERS:
            qb.setTables(Tables.USERS);
            break;
        case PROJECTS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case PROJECTS:
            qb.setTables(Tables.PROJECTS);
            break;
        case CONFIG_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case CONFIG:
            qb.setTables(Tables.CONFIG);
            break;
        case CUSTOMERS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case CUSTOMERS:
            qb.setTables(Tables.CUSTOMERS);
            break;
        case CONTACTS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case CONTACTS:
            qb.setTables(Tables.CONTACTS);
            break;
        case PROCUSTS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case PROCUSTS:
            qb.setTables(Tables.PROCUSTS);
            break;
        case REMINDERS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case REMINDERS:
            qb.setTables(Tables.REMINDERS);
            break;
        case SCHEDULES_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case SCHEDULES:
            qb.setTables(Tables.SCHEDULES);
            break;
        default:
            throw new IllegalArgumentException("Unkwon uri: " + uri.toString());
        }

        Cursor cursor = qb.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

}
