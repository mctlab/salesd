package com.mctlab.salesd.activity;

import java.util.List;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.customer.CustomerPickerDialogFragment;
import com.mctlab.salesd.reminder.ReminderListFragment;
import com.mctlab.salesd.schedule.ScheduleListFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SalesDMainActivity extends Activity implements
        View.OnClickListener, CustomerPickerDialogFragment.OnPickCustomersListener,
        UserQueryHandler.OnQueryCompleteListener,
        UserEditDialogFragment.OnUserInfoUpdatedListener {

    private UserQueryHandler mQueryHandler;

    private TextView mUserName;
    private TextView mAnnualCompleted;
    private TextView mAnnualTarget;
    private ProgressBar mAnnualProgress;

    private long mUserId = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.salesd_main_activity);

        mUserName = (TextView) findViewById(R.id.user_name);
        mAnnualCompleted = (TextView) findViewById(R.id.annual_completed);
        mAnnualTarget = (TextView) findViewById(R.id.annual_target);
        mAnnualProgress = (ProgressBar) findViewById(R.id.annual_progress);

        SalesDUtils.setChildViewOnClickListener(this, R.id.user_info_panel, this);
        SalesDUtils.setChildViewOnClickListener(this, R.id.btn_projects, this);
        SalesDUtils.setChildViewOnClickListener(this, R.id.btn_customers, this);
        SalesDUtils.setChildViewOnClickListener(this, R.id.btn_contacts, this);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.reminders_container, new ReminderListFragment());
        transaction.replace(R.id.schedules_container, new ScheduleListFragment());
        transaction.commit();

        mQueryHandler = new UserQueryHandler(getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserId = 0L;
        mQueryHandler.startQueryUser(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.salesd_main_options, menu);

        MenuItem item = menu.findItem(R.id.opt_add_contact);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
        case R.id.opt_add_project:
            intent = new Intent(SalesDConstant.ACTION_PROJECT_EDIT);
            break;
        case R.id.opt_add_customer:
            intent = new Intent(SalesDConstant.ACTION_CUSTOMER_EDIT);
            break;
        case R.id.opt_add_contact:
            CustomerPickerDialogFragment.actionCreateNewContact(getFragmentManager(), this);
            break;
        case R.id.opt_add_visit_schedule:
            break;
        }
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch(view.getId()) {
        case R.id.user_info_panel:
            UserEditDialogFragment.actionUpdateUserInfo(getFragmentManager(), mUserId, this);
            break;
        case R.id.btn_projects:
//          Toast.makeText(this, R.string.projects, Toast.LENGTH_SHORT).show();
            intent = new Intent(SalesDConstant.ACTION_PROJECT_LIST);
            break;
        case R.id.btn_customers:
//          Toast.makeText(this, R.string.customers, Toast.LENGTH_SHORT).show();
            intent = new Intent(SalesDConstant.ACTION_CUSTOMER_LIST);
            break;
        case R.id.btn_contacts:
//          Toast.makeText(this, R.string.contacts, Toast.LENGTH_SHORT).show();
            intent = new Intent(SalesDConstant.ACTION_CONTACT_LIST);
            break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        updateUserInfo(cursor);

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public void OnUserInfoUpdated() {
        mUserId = 0L;
        mQueryHandler.startQueryUser(0);
    }

    @Override
    public void OnPickCustomers(List<Long> ids) {
        if (ids != null && ids.size() > 0) {
            Long id = ids.get(0);
            Intent intent = new Intent(SalesDConstant.ACTION_CONTACT_EDIT);
            intent.putExtra(SalesDConstant.EXTRA_CUSTOMER_ID, id);
            startActivity(intent);
        }
    }

    protected void updateUserInfo(Cursor cursor) {
        String userName = null;
        int annualTarget = 0;
        int completed = 0;

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            mUserId = mQueryHandler.getUserId(cursor);
            userName = mQueryHandler.getUserName(cursor);
            annualTarget = mQueryHandler.getUserAnnualTarget(cursor);
            completed = mQueryHandler.getUserCompleted(cursor);
        } else {
            mUserId = 0L;
        }

        if (TextUtils.isEmpty(userName)) {
            userName = getString(R.string.user_info_prompt);
        }

        mUserName.setText(userName);
        mAnnualTarget.setText(getString(R.string.user_annual_target, annualTarget));
        mAnnualCompleted.setText(getString(R.string.user_completed, completed));

        mAnnualProgress.setMax(annualTarget);
        mAnnualProgress.setProgress(completed);
    }

}
