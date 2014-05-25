package com.mctlab.salesd.customer;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;

import com.mctlab.salesd.R;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.CustomersColumns;
import com.mctlab.salesd.provider.TasksDatabaseHelper.PositionsColumns;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.util.LogUtil;
import com.mctlab.salesd.util.QueryHandler;

public class CustomerQueryHandler extends QueryHandler {

    public static class Position {
        long id;
        String title;
        long upperPositionId;
    }

    public static final String[] CUSTOMER_PROJECTION = new String[] {
        CustomersColumns._ID,
        CustomersColumns.NAME,
        CustomersColumns.COMPANY_ADDRESS,
        CustomersColumns.IS_HOST_MANUFACTURER,
        CustomersColumns.IS_INSTITUTE_OF_DESIGN,
        CustomersColumns.IS_GENERAL_CONTRACTOR,
        CustomersColumns.IS_DIRECT_OWNER,
        CustomersColumns.IS_OTHERS,
        CustomersColumns.BUSINESS_DESCRIPTION
    };

    public static final int CUSTOMER_COLUMN_INDEX_ID = 0;
    public static final int CUSTOMER_COLUMN_INDEX_NAME = 1;
    public static final int CUSTOMER_COLUMN_INDEX_ADDRESS = 2;
    public static final int CUSTOMER_COLUMN_INDEX_IS_HOST_MANUFACTURER = 3;
    public static final int CUSTOMER_COLUMN_INDEX_IS_INSTITUTE_OF_DESIGN = 4;
    public static final int CUSTOMER_COLUMN_INDEX_IS_GENERAL_CONTRACTOR = 5;
    public static final int CUSTOMER_COLUMN_INDEX_IS_DIRECT_OWNER = 6;
    public static final int CUSTOMER_COLUMN_INDEX_IS_OTHERS = 7;
    public static final int CUSTOMER_COLUMN_INDEX_DESCRIPTION = 8;

    public static final String[] CONTACT_PROJECTION = new String[] {
        ContactsColumns._ID,
        ContactsColumns.NAME,
        ContactsColumns.PHONE_NUMBER,
        ContactsColumns.EMAIL,
        ContactsColumns.OFFICE_LOCATION,
        ContactsColumns.DEPARTMENT,
        ContactsColumns.TITLE,
        ContactsColumns.CHARACTERS,
        ContactsColumns.DIRECT_LEADER,
        ContactsColumns.CUSTOMER_ID
    };

    public static final int CONTACT_COLUMN_INDEX_ID = 0;
    public static final int CONTACT_COLUMN_INDEX_NAME = 1;
    public static final int CONTACT_COLUMN_INDEX_PHONE_NUMBER = 2;
    public static final int CONTACT_COLUMN_INDEX_EMAIL = 3;
    public static final int CONTACT_COLUMN_INDEX_OFFICE_LOCATION = 4;
    public static final int CONTACT_COLUMN_INDEX_DEPARTMENT = 5;
    public static final int CONTACT_COLUMN_INDEX_TITLE = 6;
    public static final int CONTACT_COLUMN_INDEX_CHARACTERS = 7;
    public static final int CONTACT_COLUMN_INDEX_DIRECT_LEADER = 8;
    public static final int CONTACT_COLUMN_INDEX_CUSTOMER_ID = 9;

    public static final String[] POSITION_PROJECTION = new String[] {
        PositionsColumns._ID,
        PositionsColumns.CUSTOMER_ID,
        PositionsColumns.TITLE,
        PositionsColumns.UPPER_POSITION_ID
    };

    public static final int POSITION_COLUMN_INDEX_ID = 0;
    public static final int POSITION_COLUMN_INDEX_CUSTOMER_ID = 1;
    public static final int POSITION_COLUMN_INDEX_TITLE = 2;
    public static final int POSITION_COLUMN_INDEX_UPPER_POSITION_ID = 3;

    private static final HashMap<Long, ArrayList<Position>> mPositionMap =
            new HashMap<Long, ArrayList<Position>>();

    private final Context mContext;

    public CustomerQueryHandler(Context context) {
        super(context.getContentResolver());
        mContext = context;
    }

    public void startQueryCustomers(int token) {
        startQuery(token, null, TasksProvider.CUSTOMERS_CONTENT_URI, CUSTOMER_PROJECTION,
                null, null, null);
    }

    public void startQueryCustomers(int token, long projectId, boolean forIntroduction) {
        if (projectId > 0) {
            Builder builder = TasksProvider.CUSTOMERS_CONTENT_URI.buildUpon();
            builder.appendQueryParameter("project_id", String.valueOf(projectId));
            if (forIntroduction) {
                builder.appendQueryParameter("for_introduction", String.valueOf(true));
            }
            startQuery(token, null, builder.build(), CUSTOMER_PROJECTION,
                    null, null, null);
        }
    }

    public void startQueryCustomer(int token, long customerId) {
        if (customerId > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.CUSTOMERS_CONTENT_URI, customerId);
            startQuery(token, null, uri, CUSTOMER_PROJECTION, null, null, null);
        }
    }

    public void startQueryCustomerLeader(int token, long customerId) {
        if (customerId > 0) {
            String none = mContext.getString(R.string.none);
            StringBuilder selection = new StringBuilder();
            selection.append(ContactsColumns.CUSTOMER_ID + "=" + customerId).append(" AND (");
            selection.append(ContactsColumns.DIRECT_LEADER + " is null OR ");
            selection.append(ContactsColumns.DIRECT_LEADER + "='" + none + "')");
            startQuery(token, null, TasksProvider.CONTACTS_CONTENT_URI, CONTACT_PROJECTION,
                    selection.toString(), null, null);
        }
    }

    public void startQueryContacts(int token, long customerId) {
        String selection = null;
        if (customerId > 0) {
            selection = ContactsColumns.CUSTOMER_ID + "=" + customerId;
        }
        startQuery(token, null, TasksProvider.CONTACTS_CONTENT_URI, CONTACT_PROJECTION,
                selection, null, null);
    }

    public void startQueryContact(int token, long contactId) {
        if (contactId > 0) {
            LogUtil.d("Contact id: " + contactId);
            Uri uri = ContentUris.withAppendedId(TasksProvider.CONTACTS_CONTENT_URI, contactId);
            startQuery(token, null, uri, CONTACT_PROJECTION, null, null, null);
        }
    }

    public void startQueryLeaders(int token, long customerId, String leaderTitle) {
        LogUtil.v("Query leaders, customer id: " + customerId + ", title: " + leaderTitle);
        // TODO: correct customer id checking
        if (customerId >= 0) {
            StringBuilder selection = new StringBuilder();
            selection.append(ContactsColumns.CUSTOMER_ID + "=" + customerId);
            if (!TextUtils.isEmpty(leaderTitle)) {
                selection.append(" AND ");
                selection.append(ContactsColumns.TITLE + "='" + leaderTitle + "'");
            }
            startQuery(token, null, TasksProvider.CONTACTS_CONTENT_URI, CONTACT_PROJECTION,
                    selection.toString(), null, null);
        }
    }

    public void startQueryFollowers(int token, long leaderId) {
        Uri.Builder builder = TasksProvider.CONTACTS_CONTENT_URI.buildUpon();
        builder.appendPath("follow").appendPath(String.valueOf(leaderId));
        startQuery(token, null, builder.build(), CONTACT_PROJECTION, null, null, null);
    }

    public ArrayList<Position> getPositionList(long customerId) {
        // for temporary use only
        customerId = 0;
        if (!mPositionMap.containsKey(customerId)) {
            ContentResolver cr = getContentResolver();
            String selection = PositionsColumns.CUSTOMER_ID + "=" + customerId;
            Cursor cursor = null;
            try {
                cursor = cr.query(TasksProvider.POSITIONS_CONTENT_URI, POSITION_PROJECTION,
                        selection, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    HashMap<Long, Position> map = new HashMap<Long, Position>();
                    ArrayList<Long> ids = new ArrayList<Long>();
                    do {
                        Position position = new Position();
                        position.id = cursor.getLong(POSITION_COLUMN_INDEX_ID);
                        position.title = cursor.getString(POSITION_COLUMN_INDEX_TITLE);
                        position.upperPositionId = cursor.getLong(
                                POSITION_COLUMN_INDEX_UPPER_POSITION_ID);
                        map.put(position.id, position);

                        if (position.upperPositionId == 0) {
                            ids.add(position.id);
                        }
                    } while (cursor.moveToNext());

                    ArrayList<Position> positionList = new ArrayList<Position>();
                    mPositionMap.put(customerId, positionList);
                    while (!ids.isEmpty()) {
                        Long id = ids.remove(0);
                        Position position = map.remove(id);
                        if (position != null) {
                            positionList.add(position);
                            for (HashMap.Entry<Long, Position> entry : map.entrySet()) {
                                if (id.equals(entry.getValue().upperPositionId)) {
                                    ids.add(entry.getValue().id);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return mPositionMap.get(customerId);
    }

    public String getCustomerName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CUSTOMER_COLUMN_INDEX_NAME);
        }
        return null;
    }

    public String getCustomerAddress(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CUSTOMER_COLUMN_INDEX_ADDRESS);
        }
        return null;
    }

    public boolean isCustomerHostManufacturer(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CUSTOMER_COLUMN_INDEX_IS_HOST_MANUFACTURER) != 0;
        }
        return false;
    }

    public boolean isCustomerInstituteOfDesign(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CUSTOMER_COLUMN_INDEX_IS_INSTITUTE_OF_DESIGN) != 0;
        }
        return false;
    }

    public boolean isCustomerGeneralContractor(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CUSTOMER_COLUMN_INDEX_IS_GENERAL_CONTRACTOR) != 0;
        }
        return false;
    }

    public boolean isCustomerDirectOwner(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CUSTOMER_COLUMN_INDEX_IS_DIRECT_OWNER) != 0;
        }
        return false;
    }

    public boolean isCustomerOthers(Cursor cursor) {
        if (cursor != null) {
            return cursor.getInt(CUSTOMER_COLUMN_INDEX_IS_OTHERS) != 0;
        }
        return false;
    }

    public String getCustomerDescription(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CUSTOMER_COLUMN_INDEX_DESCRIPTION);
        }
        return null;
    }

    public long getContactId(Cursor cursor) {
        if (cursor != null) {
            Long id = cursor.getLong(CONTACT_COLUMN_INDEX_ID);
            return id == null ? 0l : id;
        }
        return 0l;
    }

    public String getContactName(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_NAME);
        }
        return null;
    }

    public String getContactPhoneNumber(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_PHONE_NUMBER);
        }
        return null;
    }

    public String getContactEmail(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_EMAIL);
        }
        return null;
    }

    public String getContactOfficeLocation(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_OFFICE_LOCATION);
        }
        return null;
    }

    public String getContactDepartment(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_DEPARTMENT);
        }
        return null;
    }

    public String getContactCharacters(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_CHARACTERS);
        }
        return null;
    }

    public String getContactTitle(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_TITLE);
        }
        return null;
    }

    public String getContactDirectLeader(Cursor cursor) {
        if (cursor != null) {
            return cursor.getString(CONTACT_COLUMN_INDEX_DIRECT_LEADER);
        }
        return null;
    }

    public Long getContactCustomerId(Cursor cursor) {
        if (cursor != null) {
            return cursor.getLong(CONTACT_COLUMN_INDEX_CUSTOMER_ID);
        }
        return null;
    }
}
