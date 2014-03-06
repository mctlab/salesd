package com.mctlab.salesd.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TasksDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks.db";

    public interface Tables {
        public static final String USERS = "users";
        public static final String PROJECTS = "projects";
        public static final String CUSTOMERS = "customers";
        public static final String CONTACTS = "contacts";
        public static final String ORDERS = "orders";
        public static final String REMINDERS = "reminders";
        public static final String SCHEDULES = "schedules";
    }

    public interface UsersColumns {
        public static final String ACCOUNT = "account";
        public static final String USER_NAME = "user_name";
        public static final String ANNUAL_TARGET = "annual_target";
        public static final String COMPLETED = "completed";
        public static final String PRIVILEGE = "privilege";

        public static final int COMMON_USER = 0;
        public static final int ROOT_USER = 1;
    }

    public interface ProjectsColumns {
        public static final String PROJECT_NAME = "project_name";
        public static final String PRIORITY = "priority";
        public static final String OWNER_ID = "owner_id";
    }

    public interface CustomersColumns {
        public static final String CUSTOMER_NAME = "customer_name";
        public static final String BUSINESS_DESCRIPTION = "business_description";
        public static final String COMPANY_ADDRESS = "company_address";
        public static final String CUSTOMER_CATEGORY = "customer_category";
        public static final String OWNER_ID = "owner_id";

        public static final int CATEGORY_HOST_MANUFACTURER = 0;
        public static final int CATEGORY_INSTITUTE_OF_DESIGN = 1;
        public static final int CATEGORY_GENERAL_CONTRACTOR = 2;
        public static final int CATEGORY_DIRECT_OWNER = 3;
        public static final int CATEGORY_OTHERS = 4;
    }

    public interface ContactsColumns {
        public static final String CONTACT_NAME = "contact_name";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String EMAIL = "email";
        public static final String OFFICE_LOCATION = "office_location";
        public static final String DEPARTMENT = "department";
        public static final String TITLE = "title";
        public static final String DIRECT_LEADER_ID = "direct_leader_id";
        public static final String CHARACTERS = "characters";
    }

    // relational table between projects and customers
    public interface OrdersColumns {
        public static final String PROJECT_ID = "project_id";
        public static final String CUSTOMER_ID = "customer_id";
    }

    // only stored on server side
    public interface RemindersColumns {
        public static final String PROJECT_ID = "project_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String REMINDER_CYCLE = "reminder_cycle";
        public static final String OWNER_ID = "owner_id";

        public static final int WEEK_REMINDER = 1;
        public static final int MONTH_REMINDER = 2;
    }

    public interface SchedulesColumns {
        public static final String START_TIME = "start_time";
        public static final String PROJECT_ID = "project_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String STATUS = "status";
        public static final String NOTE = "note";
        public static final String LAST_MODIFIED = "last_modified";
        public static final String OWNER_ID = "owner_id";

        public static final int STATUS_NOT_STARTED = 0;
        public static final int STATUS_COMPLETE = 1;
    }

    public TasksDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.USERS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UsersColumns.ACCOUNT + " TEXT," +
                UsersColumns.USER_NAME + " TEXT," +
                UsersColumns.ANNUAL_TARGET + " INTEGER NOT NULL DEFAULT 0," +
                UsersColumns.COMPLETED + " INTEGER NOT NULL DEFAULT 0," +
                UsersColumns.PRIVILEGE + " INTEGER NOT NULL DEFAULT 0");

        db.execSQL("CREATE TABLE " + Tables.PROJECTS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProjectsColumns.PROJECT_NAME + " TEXT," +
                ProjectsColumns.PRIORITY + " INTEGER," +
                ProjectsColumns.OWNER_ID + " INTEGER");

        db.execSQL("CREATE TABLE " + Tables.CUSTOMERS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CustomersColumns.CUSTOMER_NAME + " TEXT," +
                CustomersColumns.BUSINESS_DESCRIPTION + " TEXT," +
                CustomersColumns.COMPANY_ADDRESS + " TEXT," +
                CustomersColumns.CUSTOMER_CATEGORY + " INTEGER," +
                CustomersColumns.OWNER_ID + " INTEGER");

        db.execSQL("CREATE TABLE " + Tables.CONTACTS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactsColumns.CONTACT_NAME + " TEXT," +
                ContactsColumns.PHONE_NUMBER + " TEXT," +
                ContactsColumns.EMAIL + " TEXT," +
                ContactsColumns.OFFICE_LOCATION + " TEXT," +
                ContactsColumns.DEPARTMENT + " TEXT," +
                ContactsColumns.TITLE + " TEXT," +
                ContactsColumns.DIRECT_LEADER_ID + " TEXT," +
                ContactsColumns.CHARACTERS + " TEXT");

        db.execSQL("CREATE TABLE " + Tables.ORDERS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                OrdersColumns.PROJECT_ID + " INTEGER," +
                OrdersColumns.CUSTOMER_ID + " INTEGER");

        db.execSQL("CREATE TABLE " + Tables.REMINDERS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RemindersColumns.PROJECT_ID + " INTEGER," +
                RemindersColumns.CUSTOMER_ID + " INTEGER," +
                RemindersColumns.REMINDER_CYCLE + " INTEGER" +
                RemindersColumns.OWNER_ID + " INTEGER");

        db.execSQL("CREATE TABLE " + Tables.SCHEDULES + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SchedulesColumns.START_TIME + " INTEGER," +
                SchedulesColumns.PROJECT_ID + " INTEGER," +
                SchedulesColumns.CUSTOMER_ID + " INTEGER," +
                SchedulesColumns.STATUS + " INTEGER," +
                SchedulesColumns.NOTE + " TEXT," +
                SchedulesColumns.LAST_MODIFIED + " INTEGER" +
                SchedulesColumns.OWNER_ID + " INTEGER");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
