package com.mctlab.salesd.customer;

import java.util.ArrayList;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.customer.CustomerQueryHandler.Position;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsColumns;
import com.mctlab.salesd.util.LogUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ContactEditActivity extends Activity implements
        CustomerQueryHandler.OnQueryCompleteListener, AdapterView.OnItemSelectedListener {

    private static final int MSG_SETUP_TITLES = 0;

    private static final int TOKEN_QUERY_CONTACT = 0;
    private static final int TOKEN_QUERY_CUSTOMER = 1;
    private static final int TOKEN_QUERY_LEADERS = 2;

    static class SpinnerItem {
        long id;
        String text;

        SpinnerItem(long id, String text) {
            this.id = id;
            this.text = text;
        }

        static String[] toArray(ArrayList<SpinnerItem> list) {
            if (list == null || list.isEmpty()) {
                return new String[0];
            }
            return toArray(list, new String[list.size()], 0);
        }

        static String[] toArray(ArrayList<SpinnerItem> list, String[] array, int start) {
            if (!(list == null || list.isEmpty() || array == null || start < 0
                    || array.length - start < list.size())) {
                for (SpinnerItem item : list) {
                    array[start++] = item.text;
                }
            }
            return array;
        }
    }

    private TextView mCustomerName;
    private EditText mNameEditText;
    private EditText mPhoneNumberEditText;
    private EditText mEmailEditText;
    private EditText mOfficeLocationEditText;
    private EditText mDepartmentEditText;
    private EditText mCharacterEditText;
    private Spinner mTitleSpinner;
    private Spinner mDirectLeaderSpinner;

    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mOfficeLocation;
    private String mDepartment;
    private String mCharacter;

    private ArrayList<SpinnerItem> mTitles;
    private String mTitle;

    private ArrayList<SpinnerItem> mDirectLeaders;
    private String mDirectLeader;

    private ContentResolver mContentResolver;
    private CustomerQueryHandler mQueryHandler;

    private String mNoneString;

    private long mId = -1;
    private long mCustomerId = -1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SETUP_TITLES:
                setupTitles();
                return;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_edit_activity);

        mNoneString = getString(R.string.none);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, -1);
            mCustomerId = getIntent().getLongExtra(SalesDConstant.EXTRA_CUSTOMER_ID, -1);
        }

        mContentResolver = getContentResolver();
        mQueryHandler = new CustomerQueryHandler(mContentResolver);
        mQueryHandler.setOnQueryCompleteListener(this);

        initView();

        if (mId > 0) {
            mQueryHandler.startQueryContact(TOKEN_QUERY_CONTACT, mId);
        } else if (mCustomerId > 0) {
            mQueryHandler.startQueryCustomer(TOKEN_QUERY_CUSTOMER, mCustomerId);
            mHandler.sendEmptyMessage(MSG_SETUP_TITLES);
        } else {
            LogUtil.w("Invalid parameters, id: " + mId +", customer id: " + mCustomerId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_edit_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.opt_done:
            saveContact();
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mTitles.get(position).text.equals(mTitle)) {
            return;
        }
        mTitle = mTitles.get(position).text;

        String leaderTitle = null;
        ArrayList<Position> list = mQueryHandler.getPositionList(mCustomerId);
        if (list != null) {
            long leaderId = 0;
            for (Position item : list) {
                if (item.title.equals(mTitle)) {
                    leaderId = item.upperPositionId;
                    break;
                }
            }
            if (leaderId > 0) {
                for (Position item : list) {
                    if (item.id == leaderId) {
                        leaderTitle = item.title;
                        break;
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(leaderTitle)) {
            mQueryHandler.startQueryLeaders(TOKEN_QUERY_LEADERS, mCustomerId, leaderTitle);
        } else {
            onQueryComplete(TOKEN_QUERY_LEADERS, null);
        }
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            switch (token) {
            case TOKEN_QUERY_CONTACT:
                if (cursor.moveToFirst()) {
                    loadContact(cursor);
                }
                mQueryHandler.startQueryCustomer(TOKEN_QUERY_CUSTOMER, mCustomerId);
                mHandler.sendEmptyMessage(MSG_SETUP_TITLES);
                break;
            case TOKEN_QUERY_CUSTOMER:
                if (cursor.moveToFirst()) {
                    mCustomerName.setText(mQueryHandler.getCustomerName(cursor));
                }
                break;
            case TOKEN_QUERY_LEADERS:
                setupDirectLeaders(cursor);
                break;
            }
            cursor.close();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void initView() {
        mCustomerName = (TextView) findViewById(R.id.customer_name);
        mNameEditText = (EditText) findViewById(R.id.name);
        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mOfficeLocationEditText = (EditText) findViewById(R.id.office_location);
        mDepartmentEditText = (EditText) findViewById(R.id.department);
        mTitleSpinner = (Spinner) findViewById(R.id.title);
        mDirectLeaderSpinner = (Spinner) findViewById(R.id.direct_leader);
        mCharacterEditText = (EditText) findViewById(R.id.character);

        mDirectLeaders = new ArrayList<SpinnerItem>();
        mDirectLeaders.add(new SpinnerItem(-1, mNoneString));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SpinnerItem.toArray(mDirectLeaders));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDirectLeaderSpinner.setAdapter(adapter);
    }

    protected void setupTitles() {
        ArrayList<Position> list = mQueryHandler.getPositionList(mCustomerId);
        if (list != null) {
            mTitles = new ArrayList<SpinnerItem>();
            for (Position position : list) {
                mTitles.add(new SpinnerItem(position.id, position.title));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, SpinnerItem.toArray(mTitles));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mTitleSpinner.setAdapter(adapter);
            mTitleSpinner.setOnItemSelectedListener(this);

            // reset title spinner
            if (!TextUtils.isEmpty(mTitle)) {
                for (int i = 0; i < mTitles.size(); i++) {
                    SpinnerItem item = mTitles.get(i);
                    if (item.text.equals(mTitle)) {
                        mTitleSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    protected void setupDirectLeaders(Cursor cursor) {
        mDirectLeaders = new ArrayList<SpinnerItem>();
        mDirectLeaders.add(new SpinnerItem(-1, mNoneString));
        int index = -1;

        LogUtil.d("Cursor count: " + cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = mQueryHandler.getContactId(cursor);
                if (mId == id) continue;

                String name = mQueryHandler.getContactName(cursor);
                LogUtil.d("Candidate leader: " + name);
                mDirectLeaders.add(new SpinnerItem(id, name));

                if (name.equals(mDirectLeader)) {
                    index = mDirectLeaders.size() - 1;
                }
            }
        }

        String[] array = SpinnerItem.toArray(mDirectLeaders);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDirectLeaderSpinner.setAdapter(adapter);
        if (index > 0) {
            mDirectLeaderSpinner.setSelection(index);
        }
    }

    private void loadContact(Cursor cursor) {
        Long customerId = mQueryHandler.getContactCustomerId(cursor);
        mCustomerId = customerId == null ? 0 : customerId;

        String name = mQueryHandler.getContactName(cursor);
        mNameEditText.setText(name);

        String number = mQueryHandler.getContactPhoneNumber(cursor);
        mPhoneNumberEditText.setText(number);

        String email = mQueryHandler.getContactEmail(cursor);
        mEmailEditText.setText(email);

        String officeLocation = mQueryHandler.getContactOfficeLocation(cursor);
        mOfficeLocationEditText.setText(officeLocation);

        String department = mQueryHandler.getContactDepartment(cursor);
        mDepartmentEditText.setText(department);

        String character = mQueryHandler.getContactCharacters(cursor);
        mCharacterEditText.setText(character);

        mTitle = mQueryHandler.getContactTitle(cursor);

        mDirectLeader = mQueryHandler.getContactDirectLeader(cursor);
    }

    private void saveContact() {
        if (!checkFields()) {
            return;
        }

        ContentValues values = getFieldValues();
        boolean failed = false;
        if (mId > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.CONTACTS_CONTENT_URI, mId);
            int count = mContentResolver.update(uri, values, null, null);
            failed = count <= 0;
        } else {
            Uri uri = mContentResolver.insert(TasksProvider.CONTACTS_CONTENT_URI, values);
            failed = uri == null;
        }

        if (failed) {
            Toast.makeText(this, R.string.tip_save_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.tip_save_succeed, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean checkFields() {
        mName = mNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(mName)) {
            String field = getString(R.string.contact_name);
            String message = getString(R.string.tip_field_could_not_be_null, field);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        mPhoneNumber = mPhoneNumberEditText.getText().toString().trim();
        mEmail = mEmailEditText.getText().toString().trim();
        mOfficeLocation = mOfficeLocationEditText.getText().toString().trim();
        mDepartment = mDepartmentEditText.getText().toString().trim();
        mCharacter = mCharacterEditText.getText().toString().trim();

        int position = mTitleSpinner.getSelectedItemPosition();
        mTitle = mTitles.get(position).text;

        position = mDirectLeaderSpinner.getSelectedItemPosition();
        mDirectLeader = mDirectLeaders.get(position).text;

        return true;
    }

    private ContentValues getFieldValues() {
        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(mName)) {
            values.put(ContactsColumns.NAME, mName);
        }

        if (!TextUtils.isEmpty(mPhoneNumber)) {
            values.put(ContactsColumns.PHONE_NUMBER, mPhoneNumber);
        }

        if (!TextUtils.isEmpty(mEmail)) {
            values.put(ContactsColumns.EMAIL, mEmail);
        }

        if (!TextUtils.isEmpty(mOfficeLocation)) {
            values.put(ContactsColumns.OFFICE_LOCATION, mOfficeLocation);
        }

        if (!TextUtils.isEmpty(mDepartment)) {
            values.put(ContactsColumns.DEPARTMENT, mDepartment);
        }

        if (!TextUtils.isEmpty(mCharacter)) {
            values.put(ContactsColumns.CHARACTERS, mCharacter);
        }

        // TODO: if title change, should reset the leader of his/her staff
        if (!TextUtils.isEmpty(mTitle)) {
            values.put(ContactsColumns.TITLE, mTitle);
        }

        if (!TextUtils.isEmpty(mDirectLeader)) {
            values.put(ContactsColumns.DIRECT_LEADER, mDirectLeader);
        }

        values.put(ContactsColumns.CUSTOMER_ID, mCustomerId);
        return values;
    }
}
