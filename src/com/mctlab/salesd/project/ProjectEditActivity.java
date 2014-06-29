package com.mctlab.salesd.project;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
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

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.RequestAbortedException;
import com.mctlab.ansight.common.util.StringUtils;
import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.activity.WaitingDialogFragment;
import com.mctlab.salesd.api.EditProjectApi;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.data.Project;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.util.LogUtil;

public class ProjectEditActivity extends Activity
        implements ProjectQueryHandler.OnQueryCompleteListener {

    private static final int API_TOKEN_INSERT_PROJECT = 0;
    private static final int API_TOKEN_UPDATE_PROJECT = 1;

    class ProjectCallbackListener extends EditProjectApi.CallbackListener {

        @Override
        public void onApiStart(int token) {
            super.onApiStart(token);
            LogUtil.d("project api: start");
            Resources resources = ProjectEditActivity.this.getResources();
            if ((mId > 0)) {
                WaitingDialogFragment.actionShowProgress(getFragmentManager(),
                        resources.getString(R.string.dlg_message_updating_project));
            } else {
                WaitingDialogFragment.actionShowProgress(getFragmentManager(),
                        resources.getString(R.string.dlg_message_adding_new_project));
            }
        }

        @Override
        public void onApiAborted(int token, RequestAbortedException exception) {
            super.onApiAborted(token, exception);
            LogUtil.d("project api: abort");
            WaitingDialogFragment.actionDismissProgress();
        }

        @Override
        public void onApiSuccess(int token, Void result) {
            super.onApiSuccess(token, result);
            LogUtil.d("project api: success");
            WaitingDialogFragment.actionDismissProgress();

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
                Toast.makeText(ProjectEditActivity.this, R.string.tip_save_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProjectEditActivity.this, R.string.tip_save_succeed,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onApiFailed(int token, ApiException exception) {
            super.onApiFailed(token, exception);
            LogUtil.d("project api: failed");
            WaitingDialogFragment.actionDismissProgress();

            Toast.makeText(ProjectEditActivity.this, R.string.tip_save_failed,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onApiFinish(int token) {
            super.onApiFinish(token);
            LogUtil.d("project api: finish");
        }

    }

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

    private ProjectCallbackListener mProjectCallbackListener;

    private ContentResolver mContentResolver;
    private ProjectQueryHandler mQueryHandler;

    private long mId = SalesDConstant.EMPTY_ID;
    private long mServerId = SalesDConstant.EMPTY_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_edit_activity);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, SalesDConstant.EMPTY_ID);
        }

        mNameEditText = (EditText) findViewById(R.id.name);
        mAmountEditText = (EditText) findViewById(R.id.amount);
        mDescriptionEditText = (EditText) findViewById(R.id.description);
        mPrioritySpinner = SalesDUtils.setupSpinner(this, R.id.priority,
                R.array.project_priority_values);
        mStatusSpinner = SalesDUtils.setupSpinner(this, R.id.status,
                R.array.project_status_values);

        mProjectCallbackListener = new ProjectCallbackListener();
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
        String name = mQueryHandler.getProjectName(cursor);
        mNameEditText.setText(name);

        int amount = mQueryHandler.getProjectAmount(cursor);
        mAmountEditText.setText(String.valueOf(amount));

        int priority = mQueryHandler.getProjectPriority(cursor);
        if (priority == ProjectsColumns.PRIORITY_IMPORTANT) {
            mPrioritySpinner.setSelection(1);
        } else {
            mPrioritySpinner.setSelection(0);
        }

        int status = mQueryHandler.getProjectStatus(cursor);
        if (status == ProjectsColumns.STATUS_ONGOING) {
            mStatusSpinner.setSelection(1);
        } else if (status == ProjectsColumns.STATUS_COMPLETE) {
            mStatusSpinner.setSelection(2);
        } else {
            mStatusSpinner.setSelection(0);
        }

        String description = mQueryHandler.getProjectDescription(cursor);
        mDescriptionEditText.setText(description);

        mServerId = mQueryHandler.getProjectServerId(cursor);
    }

    private void saveProject() {
        if (!checkFields()) {
            return;
        }

        Project project = getProject();
        if (mId > 0) {
            EditProjectApi.updateProject(API_TOKEN_UPDATE_PROJECT, project,
                    mProjectCallbackListener);
        } else {
            EditProjectApi.insertProject(API_TOKEN_INSERT_PROJECT, project,
                    mProjectCallbackListener);
        }
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

    private Project getProject() {
        Project project = new Project();
        project.setName(mName);
        project.setEstimatedAmount(mAmount);
        project.setPriority(mPriority);
        project.setStatus(mStatus);
        if (StringUtils.isNotBlank(mDescription)) {
            project.setDescription(mDescription);
        }
        project.setServerId(mServerId);
        return project;
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
