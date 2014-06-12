package com.mctlab.salesd.provider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.mctlab.salesd.AppConfig;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ConfigCategoriesColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ConfigColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsViewColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.CustomersColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProcustsColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.Tables;
import com.mctlab.salesd.util.LogUtil;

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

    public static final String AUTHORITY = "salesd";

    protected static final String URI_AUTHORITY_PREFIX = "content://" + AUTHORITY + "/";

    protected static final String TYPE_PREFIX = "vnd.android.cursor.dir/";
    protected static final String ITEM_TYPE_PREFIX = "vnd.android.cursor.item/";

    public static final Uri USERS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "users");

    protected static final String USERS_TYPE = TYPE_PREFIX + "user";
    protected static final String USERS_ITEM_TYPE = ITEM_TYPE_PREFIX + "user";

    public static final Uri PROJECTS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "projects");

    protected static final String PROJECTS_TYPE = TYPE_PREFIX + "project";
    protected static final String PROJECTS_ITEM_TYPE = ITEM_TYPE_PREFIX + "project";

    public static final Uri CONFIG_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "config");

    protected static final String CONFIG_TYPE = TYPE_PREFIX + "config";
    protected static final String CONFIG_ITEM_TYPE = ITEM_TYPE_PREFIX + "config";

    public static final Uri CONFIG_CATEGORIES_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX +
            "config/categories");

    protected static final String CONFIG_CATEGORIES_TYPE = TYPE_PREFIX + "config_category";
    protected static final String CONFIG_CATEGORIES_ITEM_TYPE = ITEM_TYPE_PREFIX +
            "config_category";

    public static final Uri CUSTOMERS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "customers");

    protected static final String CUSTOMERS_TYPE = TYPE_PREFIX + "customer";
    protected static final String CUSTOMERS_ITEM_TYPE = ITEM_TYPE_PREFIX + "customer";

    public static final Uri CONTACTS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "contacts");

    protected static final String CONTACTS_TYPE = TYPE_PREFIX + "contact";
    protected static final String CONTACTS_ITEM_TYPE = ITEM_TYPE_PREFIX + "contact";

    public static final Uri PROCUSTS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "procusts");

    protected static final String PROCUSTS_TYPE = TYPE_PREFIX + "procust";
    protected static final String PROCUSTS_ITEM_TYPE = ITEM_TYPE_PREFIX + "procust";

    public static final Uri REMINDERS_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "reminders");

    protected static final String REMINDERS_TYPE = TYPE_PREFIX + "reminder";
    protected static final String REMINDERS_ITEM_TYPE = ITEM_TYPE_PREFIX + "reminder";

    public static final Uri SCHEDULES_CONTENT_URI = Uri.parse(URI_AUTHORITY_PREFIX + "schedules");

    protected static final String SCHEDULES_TYPE = TYPE_PREFIX + "schedule";
    protected static final String SCHEDULES_ITEM_TYPE = ITEM_TYPE_PREFIX + "schedule";

    protected static final int USERS = 10;
    protected static final int USERS_ID = 11;
    protected static final int PROJECTS = 20;
    protected static final int PROJECTS_ID = 21;
    protected static final int CONFIG = 30;
    protected static final int CONFIG_ID = 31;
    protected static final int CONFIG_CATEGORIES = 32;
    protected static final int CONFIG_CATEGORIES_ID = 33;
    protected static final int CONFIG_CATEGORIES_LOAD_FROM_XML = 34;
    protected static final int CUSTOMERS = 40;
    protected static final int CUSTOMERS_ID = 41;
    protected static final int CONTACTS = 50;
    protected static final int CONTACTS_ID = 51;
    protected static final int CONTACTS_FOLLOW_LEADER = 52;
    protected static final int CONTACTS_LOAD_FROM_TEMPLATE_XML = 53;
    protected static final int PROCUSTS = 60;
    protected static final int PROCUSTS_ID = 61;
    protected static final int REMINDERS = 70;
    protected static final int REMINDERS_ID = 71;
    protected static final int SCHEDULES = 80;
    protected static final int SCHEDULES_ID = 81;

    protected static final UriMatcher sUriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    protected static String PROJECTS_HAVE_CUSTOMER_SELECT =
            ProjectsColumns._ID + " IN "
                    + "(SELECT " + ProcustsColumns.PROJECT_ID
                    + " FROM " + Tables.PROCUSTS
                    + " WHERE " + ProcustsColumns.CUSTOMER_ID + "=?)";

    protected static String CUSTOMERS_OF_PROJECT_SELECT =
            CustomersColumns._ID + " IN "
                    + "(SELECT " + ProcustsColumns.CUSTOMER_ID
                    + " FROM " + Tables.PROCUSTS
                    + " WHERE " + ProcustsColumns.PROJECT_ID + "=?)";

    protected static String CUSTOMERS_NOT_BELONG_TO_PROJECT_SELECT =
            CustomersColumns._ID + " NOT IN "
                    + "(SELECT " + ProcustsColumns.CUSTOMER_ID
                    + " FROM " + Tables.PROCUSTS
                    + " WHERE " + ProcustsColumns.PROJECT_ID + "=?)";

    protected static String ConfigJoinTable = "config " +
            " LEFT JOIN config_categories ON (config.category_id = config_categories._id)";
    protected static HashMap<String, String> ConfigJoinColumns;

    protected static String ContactsJoinTable = "contacts " +
            " LEFT JOIN contacts leader ON (contacts.direct_leader_id = leader._id)";
    protected static HashMap<String, String> ContactsJoinColumns;

    static {
        sUriMatcher.addURI(AUTHORITY, "users", USERS);
        sUriMatcher.addURI(AUTHORITY, "users/#", USERS_ID);
        sUriMatcher.addURI(AUTHORITY, "projects", PROJECTS);
        sUriMatcher.addURI(AUTHORITY, "projects/#", PROJECTS_ID);
        sUriMatcher.addURI(AUTHORITY, "config", CONFIG);
        sUriMatcher.addURI(AUTHORITY, "config/#", CONFIG_ID);
        sUriMatcher.addURI(AUTHORITY, "config/categories", CONFIG_CATEGORIES);
        sUriMatcher.addURI(AUTHORITY, "config/categories/#", CONFIG_CATEGORIES_ID);
        sUriMatcher.addURI(AUTHORITY, "config/categories/*", CONFIG_CATEGORIES_LOAD_FROM_XML);
        sUriMatcher.addURI(AUTHORITY, "customers", CUSTOMERS);
        sUriMatcher.addURI(AUTHORITY, "customers/#", CUSTOMERS_ID);
        sUriMatcher.addURI(AUTHORITY, "contacts", CONTACTS);
        sUriMatcher.addURI(AUTHORITY, "contacts/#", CONTACTS_ID);
        sUriMatcher.addURI(AUTHORITY, "contacts/follow/#", CONTACTS_FOLLOW_LEADER);
        sUriMatcher.addURI(AUTHORITY, "contacts/template/*", CONTACTS_LOAD_FROM_TEMPLATE_XML);
        sUriMatcher.addURI(AUTHORITY, "procusts", PROCUSTS);
        sUriMatcher.addURI(AUTHORITY, "procusts/#", PROCUSTS_ID);
        sUriMatcher.addURI(AUTHORITY, "reminders", REMINDERS);
        sUriMatcher.addURI(AUTHORITY, "reminders/#", REMINDERS_ID);
        sUriMatcher.addURI(AUTHORITY, "schedules", SCHEDULES);
        sUriMatcher.addURI(AUTHORITY, "schedules/#", SCHEDULES_ID);

        ConfigJoinColumns = new HashMap<String, String>();
        ConfigJoinColumns.put(ConfigColumns._ID, "config." + ConfigColumns._ID);
        ConfigJoinColumns.put(ConfigColumns.PROJECT_ID, "config." + ConfigColumns.PROJECT_ID);
        ConfigJoinColumns.put(ConfigColumns.CATEGORY_ID, "config." + ConfigColumns.CATEGORY_ID);
        ConfigJoinColumns.put(ConfigColumns.TYPE, "config." + ConfigColumns.TYPE);
        ConfigJoinColumns.put(ConfigColumns.NUMBER, "config." + ConfigColumns.NUMBER);
        ConfigJoinColumns.put(ConfigCategoriesColumns.CATEGORY, "config_categories." +
                ConfigCategoriesColumns.CATEGORY);
        ConfigJoinColumns.put(ConfigCategoriesColumns.SORT_INDEX, "config_categories." +
                ConfigCategoriesColumns.SORT_INDEX);

        ContactsJoinColumns = new HashMap<String, String>();
        ContactsJoinColumns.put(ContactsColumns._ID, "contacts." +
                ContactsColumns._ID);
        ContactsJoinColumns.put(ContactsColumns.CUSTOMER_ID, "contacts." +
                ContactsColumns.CUSTOMER_ID);
        ContactsJoinColumns.put(ContactsColumns.NAME, "contacts." +
                ContactsColumns.NAME);
        ContactsJoinColumns.put(ContactsColumns.PHONE_NUMBER, "contacts." +
                ContactsColumns.PHONE_NUMBER);
        ContactsJoinColumns.put(ContactsColumns.EMAIL, "contacts." +
                ContactsColumns.EMAIL);
        ContactsJoinColumns.put(ContactsColumns.OFFICE_LOCATION, "contacts." +
                ContactsColumns.OFFICE_LOCATION);
        ContactsJoinColumns.put(ContactsColumns.DEPARTMENT, "contacts." +
                ContactsColumns.DEPARTMENT);
        ContactsJoinColumns.put(ContactsColumns.TITLE, "contacts." +
                ContactsColumns.TITLE);
        ContactsJoinColumns.put(ContactsColumns.DIRECT_LEADER_ID, "contacts." +
                ContactsColumns.DIRECT_LEADER_ID);
        ContactsJoinColumns.put(ContactsColumns.CHARACTERS, "contacts." +
                ContactsColumns.CHARACTERS);
        ContactsJoinColumns.put(ContactsViewColumns.DIRECT_LEADER, "leader." +
                ContactsColumns.NAME + " AS " + ContactsViewColumns.DIRECT_LEADER);
    }

    private TasksDatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private HashMap<String, Long> mPositionIdMap = new HashMap<String, Long>();
    private HashMap<String, Long> mConfigCategoryIdMap = new HashMap<String, Long>();

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
        case CONFIG_CATEGORIES:
            return CONFIG_CATEGORIES_TYPE;
        case CONFIG_CATEGORIES_ID:
            return CONFIG_CATEGORIES_ITEM_TYPE;
        case CONFIG_CATEGORIES_LOAD_FROM_XML:
            return CONFIG_CATEGORIES_TYPE;
        case CUSTOMERS:
            return CUSTOMERS_TYPE;
        case CUSTOMERS_ID:
            return CUSTOMERS_ITEM_TYPE;
        case CONTACTS:
            return CONTACTS_TYPE;
        case CONTACTS_ID:
            return CONTACTS_ITEM_TYPE;
        case CONTACTS_LOAD_FROM_TEMPLATE_XML:
            return CONTACTS_TYPE;
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
            values = rebuildConfigValues(values);
            table = Tables.CONFIG;
            break;
        case CONFIG_CATEGORIES:
            table = Tables.CONFIG_CATEGORIES;
            break;
        case CONFIG_CATEGORIES_LOAD_FROM_XML:
            return loadConfigCategoriesFromXml(uri.getLastPathSegment());
        case CUSTOMERS:
            table = Tables.CUSTOMERS;
            break;
        case CONTACTS:
            values = rebuildContactsValues(values);
            table = Tables.CONTACTS;
            break;
        case CONTACTS_LOAD_FROM_TEMPLATE_XML:
            long customerId = 0;
            if (values.containsKey(ContactsColumns.CUSTOMER_ID)) {
                Long temp = values.getAsLong(ContactsColumns.CUSTOMER_ID);
                if (temp != null) {
                    customerId = temp.longValue();
                }
            }
            return loadContactsFromTemplateXml(uri.getLastPathSegment(), customerId);
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

    protected HashMap<String, Long> getConfigCategoryIdMap() {
        if (mConfigCategoryIdMap.isEmpty()) {
            Cursor cursor = mDb.query(Tables.CONFIG_CATEGORIES, null, null, null, null, null,
                    null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String category = null;
                    int index = cursor.getColumnIndex(ConfigCategoriesColumns.CATEGORY);
                    if (index != -1) {
                        category = cursor.getString(index);
                    }
                    long id = -1;
                    index = cursor.getColumnIndex(ConfigCategoriesColumns._ID);
                    if (index != -1) {
                        id = cursor.getLong(index);
                    }
                    if (!TextUtils.isEmpty(category) && id > 0) {
                        mConfigCategoryIdMap.put(category, id);
                    }
                }
                cursor.close();
            }
        }
        return mConfigCategoryIdMap;
    }

    protected ContentValues rebuildConfigValues(ContentValues values) {
        ContentValues configValues = new ContentValues();
        if (values.containsKey(ConfigColumns._ID)) {
            configValues.put(ConfigColumns._ID, values.getAsLong(ConfigColumns._ID));
        }
        if (values.containsKey(ConfigColumns.PROJECT_ID)) {
            configValues.put(ConfigColumns.PROJECT_ID, values.getAsInteger(
                    ConfigColumns.PROJECT_ID));
        }
        if (values.containsKey(ConfigColumns.TYPE)) {
            configValues.put(ConfigColumns.TYPE, values.getAsString(ConfigColumns.TYPE));
        }
        if (values.containsKey(ConfigColumns.NUMBER)) {
            configValues.put(ConfigColumns.NUMBER, values.getAsInteger(ConfigColumns.NUMBER));
        }
        if (values.containsKey(ConfigCategoriesColumns.CATEGORY)) {
            HashMap<String, Long> map = getConfigCategoryIdMap();
            String category = values.getAsString(ConfigCategoriesColumns.CATEGORY);
            if (map.containsKey(category)) {
                configValues.put(ConfigColumns.CATEGORY_ID, map.get(category));
            }
        }
        return configValues;
    }

    protected ContentValues rebuildContactsValues(ContentValues values) {
        if (values != null && values.containsKey(ContactsViewColumns.DIRECT_LEADER)) {
            String leader = values.getAsString(ContactsViewColumns.DIRECT_LEADER);
            values.remove(ContactsViewColumns.DIRECT_LEADER);

            if (!TextUtils.isEmpty(leader)) {
                if (values.containsKey(ContactsColumns.CUSTOMER_ID)) {
                    Long customerId = values.getAsLong(ContactsColumns.CUSTOMER_ID);

                    StringBuilder builder = new StringBuilder();
                    builder.append(ContactsColumns.CUSTOMER_ID + "=" + customerId + " AND ");
                    builder.append(ContactsColumns.NAME + "='" + leader + "'");

                    Cursor cursor = mDb.query(Tables.CONTACTS, null, builder.toString(), null,
                            null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(ContactsColumns._ID);
                            values.put(ContactsColumns.DIRECT_LEADER_ID, cursor.getLong(index));
                        }
                        cursor.close();
                    }
                }
            }
        }
        return values;
    }

    protected Uri loadConfigCategoriesFromXml(String path) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(getContext().getAssets().open(path));
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(reader);

            boolean allowUpdate = false;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    if (TextUtils.equals(tag, "categories")) {
                        int version = 0;
                        try {
                            version = Integer.parseInt(parser.getAttributeValue(0));
                        } catch (NumberFormatException e) {
                        }
                        allowUpdate = requestToUpdateCategories(version);
                    } else if (TextUtils.equals(tag, "category")) {
                        String category = parser.getAttributeValue(0);
                        int sortIndex = 0;
                        try {
                            sortIndex = Integer.parseInt(parser.getAttributeValue(1));
                        } catch (NumberFormatException e) {
                        }
                        if (allowUpdate) {
                            insertCategory(category, sortIndex);
                        }
                    }
                }
                parser.next();
                eventType = parser.getEventType();
            }
        } catch (IOException e) {
            LogUtil.w("Failed to open: " + path + ", message: " + e.getMessage());
        } catch (XmlPullParserException e) {
            LogUtil.w("Failed to parse: " + path + ", message: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    protected boolean requestToUpdateCategories(int version) {
        LogUtil.d("Update categories, version: " + version);

        int currentVersion = AppConfig.getCategoriesVersion();
        LogUtil.d("Current categories version: " + currentVersion);
        if (version <= currentVersion) {
            return false;
        }

        AppConfig.setCategoriesVersion(version);
        mDb.delete(Tables.CONFIG_CATEGORIES, null, null);

        mConfigCategoryIdMap.clear();
        return true;
    }

    protected void insertCategory(String category, int sortIndex) {
        LogUtil.d("Insert category: " + category + ", sort index: " + sortIndex);
        if (TextUtils.isEmpty(category)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ConfigCategoriesColumns.CATEGORY, category);
        values.put(ConfigCategoriesColumns.SORT_INDEX, sortIndex);

        mDb.insert(Tables.CONFIG_CATEGORIES, null, values);
    }

    protected Uri loadPositionsFromXml(String path) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(getContext().getAssets().open(path));
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(reader);

            boolean allowUpdate = false;
            String customer = null;
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    if (TextUtils.equals(tag, "customer")) {
                        customer = parser.getAttributeValue(0);
                        int version = 0;
                        try {
                            version = Integer.parseInt(parser.getAttributeValue(1));
                        } catch (NumberFormatException e) {
                        }
                        allowUpdate = requestToUpdateCustomerPositions(customer, version);
                    } else if (TextUtils.equals(tag, "position")) {
                        String title = parser.getAttributeValue(0);
                        String upperPosition = null;
                        if (parser.getAttributeCount() > 1) {
                            upperPosition = parser.getAttributeValue(1);
                        }
                        if (allowUpdate) {
                            insertCustomerPosition(customer, title, upperPosition);
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tag = parser.getName();
                    if (TextUtils.equals(tag, "customer")) {
                        if (allowUpdate) {
                            deleteInvalidCustomerPositions(customer);
                        }
                    }
                }
                parser.next();
                eventType = parser.getEventType();
            }
        } catch (IOException e) {
            LogUtil.w("Failed to open: " + path + ", message: " + e.getMessage());
        } catch (XmlPullParserException e) {
            LogUtil.w("Failed to parse: " + path + ", message: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    protected boolean requestToUpdateCustomerPositions(String customer, int version) {
        LogUtil.d("Update positions of customer: " + customer + ", version: " + version);
        if (TextUtils.isEmpty(customer)) {
            return false;
        }

        int currentVersion = AppConfig.getCustomerPositionsVersion(customer);
        LogUtil.d("Current version: " + currentVersion);
        if (version <= currentVersion) {
            return false;
        }
        AppConfig.setCustomerPositionsVersion(customer, version);

//        String selection = PositionsColumns.CUSTOMER_ID + "=0";
//        mDb.delete(Tables.POSITIONS, selection, null);
        mPositionIdMap.clear();

        return true;
    }

    protected void insertCustomerPosition(String customer, String title, String upperPosition) {
        LogUtil.d("Insert position: " + title + ", upper position: " + upperPosition);
        if (TextUtils.isEmpty(customer) || TextUtils.isEmpty(title)) {
            return;
        }

        long positionId = 0;
        if (mPositionIdMap.containsKey(title)) {
            positionId = mPositionIdMap.get(title);
            if (positionId > 0) {
                LogUtil.d("The position has been defined.");
                return;
            }
        }

        long upperPositionId = 0;
        if (!TextUtils.isEmpty(upperPosition)) {
            if (mPositionIdMap.containsKey(upperPosition)) {
                upperPositionId = mPositionIdMap.get(upperPosition);
            } else {
                upperPositionId = -2 - mPositionIdMap.size();
                mPositionIdMap.put(upperPosition, upperPositionId);
            }
        }

//        long customerId = 0;
//        ContentValues values = new ContentValues();
//        values.put(PositionsColumns.CUSTOMER_ID, customerId);
//        values.put(PositionsColumns.TITLE, title);
//        values.put(PositionsColumns.UPPER_POSITION_ID, upperPositionId);
//
//        long id = mDb.insert(Tables.POSITIONS, null, values);
//        mPositionIdMap.put(title, id);

        if (positionId < 0) {
//            values = new ContentValues();
//            values.put(PositionsColumns.UPPER_POSITION_ID, id);
//
//            StringBuilder builder = new StringBuilder();
//            builder.append(PositionsColumns.CUSTOMER_ID + "=" + customerId).append(" AND ");
//            builder.append(PositionsColumns.UPPER_POSITION_ID + "=" + positionId);
//
//            mDb.update(Tables.POSITIONS, values, builder.toString(), null);
        }
    }

    protected void deleteInvalidCustomerPositions(String customer) {
//        long customerId = 0;

//        StringBuilder builder = new StringBuilder();
//        builder.append(PositionsColumns.CUSTOMER_ID + "=" + customerId).append(" AND ");
//        builder.append(PositionsColumns.UPPER_POSITION_ID + "<0");
//
//        mDb.delete(Tables.POSITIONS, builder.toString(), null);
    }

    protected Uri loadContactsFromTemplateXml(String tplName, long customerId) {
        LogUtil.d("Load contacts template: " + tplName + ", customer id: " + customerId);
        mPositionIdMap.clear();

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(getContext().getAssets().open(tplName));
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(reader);

            clearContacts(customerId, false);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    if (TextUtils.equals(tag, "contact")) {
                        eventType = insertContact(customerId, parser);
                        continue;
                    }
                }
                parser.next();
                eventType = parser.getEventType();
            }
        } catch (IOException e) {
            LogUtil.w("Failed to open: " + tplName + ", message: " + e.getMessage());
        } catch (XmlPullParserException e) {
            LogUtil.w("Failed to parse: " + tplName + ", message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            clearContacts(customerId, true);

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    protected int insertContact(long customerId, XmlPullParser parser) {
        int eventType = XmlPullParser.END_DOCUMENT;

        try {
            ContentValues values = new ContentValues();
            values.put(ContactsColumns.CUSTOMER_ID, customerId);

            String value, title = null, leaderTitle = null;

            LogUtil.d(">> new contact");
            eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    if (TextUtils.equals(tag, "name")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- name: " + value);
                            values.put(ContactsColumns.NAME, value);
                        }
                    } else if (TextUtils.equals(tag, "phone_number")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- phone_number: " + value);
                            values.put(ContactsColumns.PHONE_NUMBER, value);
                        }
                    } else if (TextUtils.equals(tag, "email")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- email: " + value);
                            values.put(ContactsColumns.EMAIL, value);
                        }
                    } else if (TextUtils.equals(tag, "office_location")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- office_location: " + value);
                            values.put(ContactsColumns.OFFICE_LOCATION, value);
                        }
                    } else if (TextUtils.equals(tag, "department")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- department: " + value);
                            values.put(ContactsColumns.DEPARTMENT, value);
                        }
                    } else if (TextUtils.equals(tag, "title")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- title: " + value);
                            title = value;
                            values.put(ContactsColumns.TITLE, value);
                        }
                    } else if (TextUtils.equals(tag, "leader_title")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- leader_title: " + value);
                            leaderTitle = value;
                        }
                    } else if (TextUtils.equals(tag, "characters")) {
                        value = parser.nextText();
                        if (!TextUtils.isEmpty(value)) {
                            LogUtil.d("-- characters: " + value);
                            values.put(ContactsColumns.CHARACTERS, value);
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tag = parser.getName();
                    if (TextUtils.equals(tag, "contact")) {
                        break;
                    }
                }

                parser.next();
                eventType = parser.getEventType();
            }

            boolean insert = true;
            // checking contact title
            if (!values.containsKey(ContactsColumns.TITLE)) {
                // invalid contact, no title field
                LogUtil.d("** ignore: no title field");
                insert = false;
            }

            boolean isLeader = false;
            // checking contact leader title
            if (insert) {
                long leaderId = 0L;
                if (TextUtils.isEmpty(leaderTitle)) {
                    if (mPositionIdMap.containsKey("__00__")) {
                        // customer leader already exists
                        LogUtil.d("** ignore: customer leader already exists");
                        insert = false;
                    } else {
                        isLeader = true;
                    }
                } else {
                    if (mPositionIdMap.containsKey(leaderTitle)) {
                        leaderId = mPositionIdMap.get(leaderTitle);
                    } else {
                        leaderId = -2 - mPositionIdMap.size();
                        mPositionIdMap.put(leaderTitle, leaderId);
                    }
                }
                values.put(ContactsColumns.DIRECT_LEADER_ID, leaderId);
            }

            // checking contact name
            if (insert && !values.containsKey(ContactsColumns.NAME)) {
                values.put(ContactsColumns.NAME, title);
            }

            // insert contact
            if (insert) {
                long id = mDb.insert(Tables.CONTACTS, null, values);
                if (id > 0) {
                    if (isLeader) {
                        // add leader identifier for later use
                        mPositionIdMap.put("__00__", 0L);
                    }
                    if (mPositionIdMap.containsKey(title)) {
                        long oldId = mPositionIdMap.get(title);
                        StringBuilder builder = new StringBuilder();
                        builder.append(ContactsColumns.CUSTOMER_ID + "=" + customerId);
                        builder.append(" AND ");
                        builder.append(ContactsColumns.DIRECT_LEADER_ID + "=" + oldId);

                        values = new ContentValues();
                        values.put(ContactsColumns.DIRECT_LEADER_ID, id);

                        mDb.update(Tables.CONTACTS, values, builder.toString(), null);
                    }
                    mPositionIdMap.put(title, id);
                }
            }
        } catch (Exception e) {

        }

        return eventType;
    }

    protected void clearContacts(long customerId, boolean onlyInvalid) {
        StringBuilder builder = new StringBuilder();
        builder.append(ContactsColumns.CUSTOMER_ID + "=" + customerId);

        if (onlyInvalid) {
            builder.append(" AND ");
            builder.append(ContactsColumns.DIRECT_LEADER_ID + "<0");
        }

        mDb.delete(Tables.CONTACTS, builder.toString(), null);
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
            values = rebuildConfigValues(values);
            table = Tables.CONFIG;
            break;
        case CONFIG_CATEGORIES_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONFIG_CATEGORIES:
            table = Tables.CONFIG_CATEGORIES;
            break;
        case CUSTOMERS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CUSTOMERS:
            table = Tables.CUSTOMERS;
            break;
        case CONTACTS_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONTACTS:
            values = rebuildContactsValues(values);
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
        case CONFIG_CATEGORIES_ID:
            selectionAppend = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONFIG_CATEGORIES:
            table = Tables.CONFIG_CATEGORIES;
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

        String where = null;
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
            String customerId = uri.getQueryParameter("customer_id");
            if (!TextUtils.isEmpty(customerId)) {
                try {
                    long id = Long.parseLong(customerId);
                    if (id > 0) {
                        qb.appendWhere(PROJECTS_HAVE_CUSTOMER_SELECT);
                        selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(id));
                    }
                } catch (NumberFormatException e) {
                }
            }
            qb.setTables(Tables.PROJECTS);
            break;
        case CONFIG_ID:
            where = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONFIG:
            if (isInProjection(projection, ConfigCategoriesColumns.CATEGORY)
                    || isInProjection(projection, ConfigCategoriesColumns.SORT_INDEX)) {
                if (where != null) qb.appendWhere("contacts." + where);
                qb.setTables(ConfigJoinTable);
                qb.setProjectionMap(ConfigJoinColumns);
            } else {
                if (where != null) qb.appendWhere(where);
                qb.setTables(Tables.CONFIG);
            }
            break;
        case CONFIG_CATEGORIES_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case CONFIG_CATEGORIES:
            qb.setTables(Tables.CONFIG_CATEGORIES);
            break;
        case CUSTOMERS_ID:
            qb.appendWhere(BaseColumns._ID + "=" + ContentUris.parseId(uri));
        case CUSTOMERS:
            String projectId = uri.getQueryParameter("project_id");
            String forIntroduction = uri.getQueryParameter("for_introduction");
            if (!TextUtils.isEmpty(projectId)) {
                boolean belongTo = !Boolean.parseBoolean(forIntroduction);
                try {
                    long id = Long.parseLong(projectId);
                    if (id > 0) {
                        if (belongTo) {
                            qb.appendWhere(CUSTOMERS_OF_PROJECT_SELECT);
                        } else {
                            qb.appendWhere(CUSTOMERS_NOT_BELONG_TO_PROJECT_SELECT);
                        }
                        selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(id));
                    }
                } catch (NumberFormatException e) {
                }
            }
            qb.setTables(Tables.CUSTOMERS);
            break;
        case CONTACTS_ID:
            where = BaseColumns._ID + "=" + ContentUris.parseId(uri);
        case CONTACTS:
            if (isInProjection(projection, ContactsViewColumns.DIRECT_LEADER)) {
//              selection = rebuildContactsSelection(selection);
                if (where != null) {
                    qb.appendWhere("contacts." + where);
                }
                qb.setTables(ContactsJoinTable);
                qb.setProjectionMap(ContactsJoinColumns);
            } else {
                if (where != null) {
                    qb.appendWhere(where);
                }
                qb.setTables(Tables.CONTACTS);
            }
            break;
        case CONTACTS_FOLLOW_LEADER:
            long leaderId = ContentUris.parseId(uri);
            where = "contacts." + ContactsColumns.DIRECT_LEADER_ID + "=" + leaderId;
            qb.appendWhere(where);
            qb.setTables(ContactsJoinTable);
            qb.setProjectionMap(ContactsJoinColumns);
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

    public boolean isInProjection(String[] projection, String column) {
        if (projection == null) {
            return true;
        }
        for (String test : projection) {
            if (column.equals(test)) {
                return true;
            }
        }
        return false;
    }

    public String rebuildContactsSelection(String selection) {
        // not full test function
        if (TextUtils.isEmpty(selection)) {
            return selection;
        }

        if (selection.contains(ContactsColumns._ID)) {
            selection = selection.replace(ContactsColumns._ID, "contacts." + ContactsColumns._ID);
        }

        if (selection.contains(ContactsColumns.CUSTOMER_ID)) {
            selection = selection.replace(ContactsColumns.CUSTOMER_ID, "contacts." +
                    ContactsColumns.CUSTOMER_ID);
        }

        if (selection.contains(ContactsColumns.NAME)) {
            selection = selection.replace(ContactsColumns.NAME, "contacts." +
                    ContactsColumns.NAME);
        }

        if (selection.contains(ContactsColumns.PHONE_NUMBER)) {
            selection = selection.replace(ContactsColumns.PHONE_NUMBER, "contacts." +
                    ContactsColumns.PHONE_NUMBER);
        }

        if (selection.contains(ContactsColumns.EMAIL)) {
            selection = selection.replace(ContactsColumns.EMAIL, "contacts." +
                    ContactsColumns.EMAIL);
        }

        if (selection.contains(ContactsColumns.OFFICE_LOCATION)) {
            selection = selection.replace(ContactsColumns.OFFICE_LOCATION, "contacts." +
                    ContactsColumns.OFFICE_LOCATION);
        }

        if (selection.contains(ContactsColumns.DEPARTMENT)) {
            selection = selection.replace(ContactsColumns.DEPARTMENT, "contacts." +
                    ContactsColumns.DEPARTMENT);
        }

        if (selection.contains(ContactsColumns.TITLE)) {
            selection = selection.replace(ContactsColumns.TITLE, "contacts." +
                    ContactsColumns.TITLE);
        }

        if (selection.contains(ContactsColumns.DIRECT_LEADER_ID)) {
            selection = selection.replace(ContactsColumns.DIRECT_LEADER_ID, "contacts." +
                    ContactsColumns.DIRECT_LEADER_ID);
        }

        if (selection.contains(ContactsColumns.CHARACTERS)) {
            selection = selection.replace(ContactsColumns.CHARACTERS, "contacts." +
                    ContactsColumns.CHARACTERS);
        }

        LogUtil.v("new contact selection: " + selection);
        return selection;
    }

    /**
     * Inserts an argument at the beginning of the selection arg list.
     */
    private String[] insertSelectionArg(String[] selectionArgs, String arg) {
        if (selectionArgs == null) {
            return new String[] {arg};
        } else {
            int newLength = selectionArgs.length + 1;
            String[] newSelectionArgs = new String[newLength];
            newSelectionArgs[0] = arg;
            System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, selectionArgs.length);
            return newSelectionArgs;
        }
    }
}
