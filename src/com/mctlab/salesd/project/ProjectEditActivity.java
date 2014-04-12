package com.mctlab.salesd.project;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;

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

public class ProjectEditActivity extends Activity
        implements ProjectQueryHandler.OnQueryCompleteListener {

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private Spinner mPrioritySpinner;
    private Spinner mStatusSpinner;

    private String mName;
    private String mDescription;
    private int mAmount;
    private int mPriority;
    private int mStatus;

    private ContentResolver mContentResolver;
    private ProjectQueryHandler mQueryHandler;

    private long mId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_edit_activity);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, -1);
        }

        mNameEditText = (EditText) findViewById(R.id.name);
        mAmountEditText = (EditText) findViewById(R.id.amount);
        mDescriptionEditText = (EditText) findViewById(R.id.description);
        mPrioritySpinner = SalesDUtils.setupSpinner(this, R.id.priority,
                R.array.project_priority_values);
        mStatusSpinner = SalesDUtils.setupSpinner(this, R.id.status,
                R.array.project_status_values);

        mContentResolver = getContentResolver();
        mQueryHandler = new ProjectQueryHandler(mContentResolver);
        mQueryHandler.setOnQueryCompleteListener(this);
        mQueryHandler.startQueryProject(0, mId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_edit_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.opt_done:
            saveProject();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                loadProject(cursor);
            }
            cursor.close();
        }
    }

    private void loadProject(Cursor cursor) {
        String name = mQueryHandler.getName(cursor);
        mNameEditText.setText(name);

        int amount = mQueryHandler.getAmount(cursor);
        mAmountEditText.setText(String.valueOf(amount));

        int priority = mQueryHandler.getPriority(cursor);
        if (priority == ProjectsColumns.PRIORITY_IMPORTANT) {
            mPrioritySpinner.setSelection(1);
        } else {
            mPrioritySpinner.setSelection(0);
        }

        int status = mQueryHandler.getStatus(cursor);
        if (status == ProjectsColumns.STATUS_ONGOING) {
            mStatusSpinner.setSelection(1);
        } else if (status == ProjectsColumns.STATUS_COMPLETE) {
            mStatusSpinner.setSelection(2);
        } else {
            mStatusSpinner.setSelection(0);
        }

        String description = mQueryHandler.getDescription(cursor);
        mDescriptionEditText.setText(description);
    }

    private void saveProject() {
        if (!checkFields()) {
            return;
        }

        ContentValues values = getFieldValues();
        boolean failed = false;
        if (mId > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.PROJECTS_CONTENT_URI, mId);
            int count = mContentResolver.update(uri, values, null, null);
            failed = count <= 0;
        } else {
            Uri uri = mContentResolver.insert(TasksProvider.PROJECTS_CONTENT_URI, values);
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
            String field = getString(R.string.project_name);
            String message = getString(R.string.tip_field_could_not_be_null, field);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        String amount = mAmountEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(amount) && TextUtils.isDigitsOnly(amount)) {
            mAmount = Integer.parseInt(amount);
        }

        mPriority = mPrioritySpinner.getSelectedItemPosition();
        mStatus = mStatusSpinner.getSelectedItemPosition();

        mDescription = mDescriptionEditText.getText().toString().trim();
        return true;
    }

    private ContentValues getFieldValues() {
        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(mName)) {
            values.put(ProjectsColumns.NAME, mName);
        }

        values.put(ProjectsColumns.ESTIMATED_AMOUNT, mAmount);
        values.put(ProjectsColumns.PRIORITY, mPriority);
        values.put(ProjectsColumns.STATUS, mStatus);

        if (!TextUtils.isEmpty(mDescription)) {
            values.put(ProjectsColumns.DESCRIPTION, mDescription);
        }

        return values;
    }

}
