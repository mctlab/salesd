package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class CustomerEditActivity extends Activity
        implements CustomerQueryHandler.OnQueryCompleteListener {

    private EditText mNameEditText;
    private EditText mAddressEditText;
    private EditText mDescriptionEditText;

    private CheckBox mHostManufacturerCB;
    private CheckBox mInstituteOfDesignCB;
    private CheckBox mGeneralContractorCB;
    private CheckBox mDirectOwnerCB;
    private CheckBox mOthersCB;

    private String mName;
    private String mAddress;
    private String mDescription;

    private boolean mHostManufacturer;
    private boolean mInstituteOfDesign;
    private boolean mGeneralContractor;
    private boolean mDirectOwner;
    private boolean mOthers;

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

        mHostManufacturerCB = (CheckBox) findViewById(R.id.is_host_manufacturer);
        mInstituteOfDesignCB = (CheckBox) findViewById(R.id.is_institute_of_design);
        mGeneralContractorCB = (CheckBox) findViewById(R.id.is_general_contractor);
        mDirectOwnerCB = (CheckBox) findViewById(R.id.is_direct_owner);
        mOthersCB = (CheckBox) findViewById(R.id.is_others);

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
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                loadCustomer(cursor);
            }
            cursor.close();
        }
    }

    private void loadCustomer(Cursor cursor) {
        String name = mQueryHandler.getCustomerName(cursor);
        mNameEditText.setText(name);

        String address = mQueryHandler.getCustomerAddress(cursor);
        mAddressEditText.setText(address);

        mHostManufacturerCB.setChecked(mQueryHandler.isCustomerHostManufacturer(cursor));
        mInstituteOfDesignCB.setChecked(mQueryHandler.isCustomerInstituteOfDesign(cursor));
        mGeneralContractorCB.setChecked(mQueryHandler.isCustomerGeneralContractor(cursor));
        mDirectOwnerCB.setChecked(mQueryHandler.isCustomerDirectOwner(cursor));
        mOthersCB.setChecked(mQueryHandler.isCustomerOthers(cursor));

        String description = mQueryHandler.getCustomerDescription(cursor);
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

        mHostManufacturer = mHostManufacturerCB.isChecked();
        mInstituteOfDesign = mInstituteOfDesignCB.isChecked();
        mGeneralContractor = mGeneralContractorCB.isChecked();
        mDirectOwner = mDirectOwnerCB.isChecked();
        mOthers = mOthersCB.isChecked();

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

        values.put(CustomersColumns.IS_HOST_MANUFACTURER, mHostManufacturer ? 1 : 0);
        values.put(CustomersColumns.IS_INSTITUTE_OF_DESIGN, mInstituteOfDesign ? 1 : 0);
        values.put(CustomersColumns.IS_GENERAL_CONTRACTOR, mGeneralContractor ? 1 : 0);
        values.put(CustomersColumns.IS_DIRECT_OWNER, mDirectOwner ? 1 : 0);
        values.put(CustomersColumns.IS_OTHERS, mOthers ? 1 : 0);

        if (!TextUtils.isEmpty(mDescription)) {
            values.put(CustomersColumns.BUSINESS_DESCRIPTION, mDescription);
        }

        return values;
    }
}
