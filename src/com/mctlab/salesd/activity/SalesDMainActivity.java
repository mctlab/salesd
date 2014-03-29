package com.mctlab.salesd.activity;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.reminder.ReminderListFragment;
import com.mctlab.salesd.schedule.ScheduleListFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SalesDMainActivity extends Activity
        implements View.OnClickListener {

    private TextView mUserName;
    private TextView mAnnualCompleted;
    private TextView mAnnualTarget;
    private ProgressBar mAnnualProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.salesd_main_activity);

        mUserName = (TextView) findViewById(R.id.user_name);
        mAnnualCompleted = (TextView) findViewById(R.id.annual_completed);
        mAnnualTarget = (TextView) findViewById(R.id.annual_target);
        mAnnualProgress = (ProgressBar) findViewById(R.id.annual_progress);

        SalesDUtils.setChildViewOnClickListener(this, R.id.btn_projects, this);
        SalesDUtils.setChildViewOnClickListener(this, R.id.btn_customers, this);
        SalesDUtils.setChildViewOnClickListener(this, R.id.btn_contacts, this);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.reminders_container, new ReminderListFragment());
        transaction.replace(R.id.schedules_container, new ScheduleListFragment());
        transaction.commit();

        updateUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.salesd_main_options, menu);
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

    protected void updateUserInfo() {
        mUserName.setText("Test Name");
        mAnnualCompleted.setText("Has completed: 5");
        mAnnualTarget.setText("Target: 10");

        mAnnualProgress.setMax(10);
        mAnnualProgress.setProgress(5);
    }

}
