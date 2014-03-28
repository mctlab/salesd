package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.provider.TasksDatabaseHelper.CustomersColumns;
import com.mctlab.salesd.provider.TasksProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CustomerEditActivity extends Activity
        implements CustomerQueryHandler.OnQueryCompleteListener {

    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mDescriptionEditText;
    private Spinner mCategorySpinner;

    private String mName;
    private String mAddress;
    private String mDescription;
    private int mCategory;

    private ContentResolver mContentResolver;
    private CustomerQueryHandler mQueryHandler;

    private long mId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_edit_activity);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, -1);
        }

        mNameEditText = (EditText) findViewById(R.id.name);
        mAddressEditText = (EditText) findViewById(R.id.address);
        mDescriptionEditText = (EditText) findViewById(R.id.description);
        mCategorySpinner = SalesDUtils.setupSpinner(this, R.id.category,
                R.array.customer_category_values);

        mContentResolver = getContentResolver();
        mQueryHandler = new CustomerQueryHandler(mContentResolver);
        mQueryHandler.setOnQueryCompleteListener(this);
        mQueryHandler.startQueryCustomer(0, mId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_edit_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.opt_done:
            saveCustomer();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            loadCustomer(cursor);
        }
    }

    private void loadCustomer(Cursor cursor) {
        String name = mQueryHandler.getName(cursor);
        mNameEditText.setText(name);

        String address = mQueryHandler.getAddress(cursor);
        mAddressEditText.setText(address);

        int category = mQueryHandler.getCategory(cursor);
        switch (category) {
        case CustomersColumns.CATEGORY_INSTITUTE_OF_DESIGN:
            mCategorySpinner.setSelection(1);
            break;
        case CustomersColumns.CATEGORY_GENERAL_CONTRACTOR:
            mCategorySpinner.setSelection(2);
            break;
        case CustomersColumns.CATEGORY_DIRECT_OWNER:
            mCategorySpinner.setSelection(3);
            break;
        case CustomersColumns.CATEGORY_OTHERS:
            mCategorySpinner.setSelection(4);
            break;
        default:
            mCategorySpinner.setSelection(0);
            break;
        }

        String description = mQueryHandler.getDescription(cursor);
        mDescriptionEditText.setText(description);
    }

    private void saveCustomer() {
        if (!checkFields()) {
            return;
        }

        ContentValues values = getFieldValues();
        boolean failed = false;
        if (mId > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.CUSTOMERS_CONTENT_URI, mId);
            int count = mContentResolver.update(uri, values, null, null);
            failed = count <= 0;
        } else {
            Uri uri = mContentResolver.insert(TasksProvider.CUSTOMERS_CONTENT_URI, values);
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
            String field = getString(R.string.customer_name);
            String message = getString(R.string.tip_field_could_not_be_null, field);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        mAddress = mAddressEditText.getText().toString().trim();

        mCategory = mCategorySpinner.getSelectedItemPosition();

        mDescription = mDescriptionEditText.getText().toString().trim();
        return true;
    }

    private ContentValues getFieldValues() {
        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(mName)) {
            values.put(CustomersColumns.NAME, mName);
        }

        if (!TextUtils.isEmpty(mAddress)) {
            values.put(CustomersColumns.COMPANY_ADDRESS, mAddress);
        }

        values.put(CustomersColumns.CATEGORY, mCategory);

        if (!TextUtils.isEmpty(mDescription)) {
            values.put(CustomersColumns.BUSINESS_DESCRIPTION, mDescription);
        }

        return values;
    }
}
