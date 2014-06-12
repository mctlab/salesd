package com.mctlab.salesd.customer;

import java.util.ArrayList;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsViewColumns;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsColumns;
import com.mctlab.salesd.util.LogUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ContactEditActivity extends Activity
        implements CustomerQueryHandler.OnQueryCompleteListener {

    public static final String EXTRA_IS_LEADER = "is_leader";
    public static final String EXTRA_MY_LEADER = "my_leader";

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
    private EditText mTitleEditText;
    private EditText mCharacterEditText;
    private Spinner mDirectLeaderSpinner;

    private String mName;
    private String mPhoneNumber;
    private String mEmail;
    private String mOfficeLocation;
    private String mDepartment;
    private String mTitle;
    private String mCharacter;

    private ArrayList<SpinnerItem> mDirectLeaders;
    private String mDirectLeader;

    private ContentResolver mContentResolver;
    private CustomerQueryHandler mQueryHandler;

    private String mNoneString;

    private boolean mIsLeader = false;

    private long mId = -1;
    private long mCustomerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_edit_activity);

        mNoneString = getString(R.string.none);

        Intent intent = getIntent();
        if (intent != null) {
            mId = intent.getLongExtra(SalesDConstant.EXTRA_ID, -1);
            mCustomerId = intent.getLongExtra(SalesDConstant.EXTRA_CUSTOMER_ID, -1);
            mIsLeader = intent.getBooleanExtra(EXTRA_IS_LEADER, false);
            mDirectLeader = intent.getStringExtra(EXTRA_MY_LEADER);
        }

        mContentResolver = getContentResolver();
        mQueryHandler = new CustomerQueryHandler(this);
        mQueryHandler.setOnQueryCompleteListener(this);

        initView();

        if (mId > 0) {
            mQueryHandler.startQueryContact(TOKEN_QUERY_CONTACT, mId);
        } else if (mCustomerId > 0) {
            mQueryHandler.startQueryCustomer(TOKEN_QUERY_CUSTOMER, mCustomerId);
            mQueryHandler.startQueryLeaders(TOKEN_QUERY_LEADERS, mCustomerId);
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
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            switch (token) {
            case TOKEN_QUERY_CONTACT:
                if (cursor.moveToFirst()) {
                    loadContact(cursor);
                }
                mQueryHandler.startQueryCustomer(TOKEN_QUERY_CUSTOMER, mCustomerId);
                mQueryHandler.startQueryLeaders(TOKEN_QUERY_LEADERS, mCustomerId);
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

    protected void initView() {
        mCustomerName = (TextView) findViewById(R.id.customer_name);
        mNameEditText = (EditText) findViewById(R.id.name);
        mPhoneNumberEditText = (EditText) findViewById(R.id.phone_number);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mOfficeLocationEditText = (EditText) findViewById(R.id.office_location);
        mDepartmentEditText = (EditText) findViewById(R.id.department);
        mTitleEditText = (EditText) findViewById(R.id.title);
        mDirectLeaderSpinner = (Spinner) findViewById(R.id.direct_leader);
        mCharacterEditText = (EditText) findViewById(R.id.character);

        mDirectLeaders = new ArrayList<SpinnerItem>();
        mDirectLeaders.add(new SpinnerItem(-1, mNoneString));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SpinnerItem.toArray(mDirectLeaders));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDirectLeaderSpinner.setAdapter(adapter);

        // hide leader spinner if this contact is the leader of the customer
        int leaderVisibility = mIsLeader ? View.GONE : View.VISIBLE;
        TextView mDirectLeaderLabel = (TextView) findViewById(R.id.label_direct_leader);
        mDirectLeaderLabel.setVisibility(leaderVisibility);
        mDirectLeaderSpinner.setVisibility(leaderVisibility);
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

        String title = mQueryHandler.getContactTitle(cursor);
        mTitleEditText.setText(title);

        String character = mQueryHandler.getContactCharacters(cursor);
        mCharacterEditText.setText(character);

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
        mTitle = mTitleEditText.getText().toString().trim();
        mCharacter = mCharacterEditText.getText().toString().trim();

        int position = mDirectLeaderSpinner.getSelectedItemPosition();
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
            values.put(ContactsViewColumns.DIRECT_LEADER, mDirectLeader);
        }

        values.put(ContactsColumns.CUSTOMER_ID, mCustomerId);
        return values;
    }
}
