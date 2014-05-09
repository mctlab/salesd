package com.mctlab.salesd.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TasksDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "tasks.db";

    public interface Tables {
        public static final String USERS = "users";
        public static final String PROJECTS = "projects";
        public static final String CONFIG = "config";
        public static final String CONFIG_CATEGORIES = "config_categories";
        public static final String CUSTOMERS = "customers";
        public static final String POSITIONS = "positions";
        public static final String CONTACTS = "contacts";
        public static final String PROCUSTS = "procusts";
        public static final String REMINDERS = "reminders";
        public static final String SCHEDULES = "schedules";
    }

    public interface UsersColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String ACCOUNT = "account";
        public static final String NAME = "name";
        public static final String ANNUAL_TARGET = "annual_target";
        public static final String COMPLETED = "completed";
        public static final String PRIVILEGE = "privilege";

        public static final int COMMON_USER = 0;
        public static final int ROOT_USER = 1;
    }

    public interface ProjectsColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String PRIORITY = "priority";
        public static final String ESTIMATED_AMOUNT = "estimated_amount";
        public static final String STATUS = "status";
        public static final String DESCRIPTION = "description";
        public static final String OWNER_ID = "owner_id";

        public static final int PRIORITY_COMMON = 0;
        public static final int PRIORITY_IMPORTANT = 1;

        public static final int STATUS_NOT_STARTED = 0;
        public static final int STATUS_ONGOING = 1;
        public static final int STATUS_COMPLETE = 2;
    }

    public interface ConfigColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String PROJECT_ID = "project_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String TYPE = "type";
        public static final String NUMBER = "number";
    }

    public interface ConfigCategoriesColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String CATEGORY = "category";
        public static final String SORT_INDEX = "sort_index";
    }

    public interface CustomersColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String BUSINESS_DESCRIPTION = "business_description";
        public static final String COMPANY_ADDRESS = "company_address";
        public static final String CATEGORY = "category";
        public static final String OWNER_ID = "owner_id";

        public static final int CATEGORY_HOST_MANUFACTURER = 0;
        public static final int CATEGORY_INSTITUTE_OF_DESIGN = 1;
        public static final int CATEGORY_GENERAL_CONTRACTOR = 2;
        public static final int CATEGORY_DIRECT_OWNER = 3;
        public static final int CATEGORY_OTHERS = 4;
    }

    // Customer position framework
    public interface PositionsColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String CUSTOMER_ID = "customer_id";
        public static final String TITLE = "title";
        public static final String UPPER_POSITION_ID = "upper_position_id";
    }

    public interface ContactsColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String CUSTOMER_ID = "customer_id";
        public static final String NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String EMAIL = "email";
        public static final String OFFICE_LOCATION = "office_location";
        public static final String DEPARTMENT = "department";
        public static final String TITLE = "title";
        public static final String DIRECT_LEADER = "direct_leader";
        public static final String CHARACTERS = "characters";
    }

    // relational table between projects and customers
    public interface ProcustsColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String PROJECT_ID = "project_id";
        public static final String CUSTOMER_ID = "customer_id";
    }

    // only stored on server side
    public interface RemindersColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String PROJECT_ID = "project_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String REMINDER_CYCLE = "reminder_cycle";
        public static final String OWNER_ID = "owner_id";

        public static final int WEEK_REMINDER = 1;
        public static final int MONTH_REMINDER = 2;
    }

    public interface SchedulesColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String PROJECT_ID = "project_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String START_TIME = "start_time";
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
                UsersColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UsersColumns.ACCOUNT + " TEXT," +
                UsersColumns.NAME + " TEXT," +
                UsersColumns.ANNUAL_TARGET + " INTEGER NOT NULL DEFAULT 0," +
                UsersColumns.COMPLETED + " INTEGER NOT NULL DEFAULT 0," +
                UsersColumns.PRIVILEGE + " INTEGER NOT NULL DEFAULT 0" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.PROJECTS + " (" +
                ProjectsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProjectsColumns.NAME + " TEXT," +
                ProjectsColumns.PRIORITY + " INTEGER," +
                ProjectsColumns.ESTIMATED_AMOUNT + " INTEGER," +
                ProjectsColumns.STATUS + " INTEGER," +
                ProjectsColumns.DESCRIPTION + " TEXT," +
                ProjectsColumns.OWNER_ID + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.CONFIG + " (" +
                ConfigColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ConfigColumns.PROJECT_ID + " INTEGER," +
                ConfigColumns.CATEGORY_ID + " INTEGER," +
                ConfigColumns.TYPE + " TEXT," +
                ConfigColumns.NUMBER + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.CONFIG_CATEGORIES + " (" +
                ConfigCategoriesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ConfigCategoriesColumns.CATEGORY + " TEXT," +
                ConfigCategoriesColumns.SORT_INDEX + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.CUSTOMERS + " (" +
                CustomersColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CustomersColumns.NAME + " TEXT," +
                CustomersColumns.BUSINESS_DESCRIPTION + " TEXT," +
                CustomersColumns.COMPANY_ADDRESS + " TEXT," +
                CustomersColumns.CATEGORY + " INTEGER," +
                CustomersColumns.OWNER_ID + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.POSITIONS + " (" +
                PositionsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PositionsColumns.CUSTOMER_ID + " INTEGER," +
                PositionsColumns.TITLE + " TEXT," +
                PositionsColumns.UPPER_POSITION_ID + " INTEGER" +
                ");");

        // TODO: store leader id instead of leader name
        db.execSQL("CREATE TABLE " + Tables.CONTACTS + " (" +
                ContactsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactsColumns.CUSTOMER_ID + " INTEGER," +
                ContactsColumns.NAME + " TEXT," +
                ContactsColumns.PHONE_NUMBER + " TEXT," +
                ContactsColumns.EMAIL + " TEXT," +
                ContactsColumns.OFFICE_LOCATION + " TEXT," +
                ContactsColumns.DEPARTMENT + " TEXT," +
                ContactsColumns.TITLE + " TEXT," +
                ContactsColumns.DIRECT_LEADER + " TEXT," +
                ContactsColumns.CHARACTERS + " TEXT" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.PROCUSTS + " (" +
                ProcustsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProcustsColumns.PROJECT_ID + " INTEGER," +
                ProcustsColumns.CUSTOMER_ID + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.REMINDERS + " (" +
                RemindersColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RemindersColumns.PROJECT_ID + " INTEGER," +
                RemindersColumns.CUSTOMER_ID + " INTEGER," +
                RemindersColumns.REMINDER_CYCLE + " INTEGER" +
                RemindersColumns.OWNER_ID + " INTEGER" +
                ");");

        db.execSQL("CREATE TABLE " + Tables.SCHEDULES + " (" +
                SchedulesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SchedulesColumns.PROJECT_ID + " INTEGER," +
                SchedulesColumns.CUSTOMER_ID + " INTEGER," +
                SchedulesColumns.START_TIME + " INTEGER," +
                SchedulesColumns.STATUS + " INTEGER," +
                SchedulesColumns.NOTE + " TEXT," +
                SchedulesColumns.LAST_MODIFIED + " INTEGER" +
                SchedulesColumns.OWNER_ID + " INTEGER" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PROJECTS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CONFIG + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CONFIG_CATEGORIES + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CUSTOMERS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.POSITIONS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CONTACTS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PROCUSTS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.REMINDERS + ";");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULES + ";");

        onCreate(db);
    }

}
