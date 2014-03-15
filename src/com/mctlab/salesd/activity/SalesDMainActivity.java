package com.mctlab.salesd.activity;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.reminder.ReminderListFragment;
import com.mctlab.salesd.schedule.ScheduleListFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

        setOnClickViewListener(R.id.btn_projects, this);
        setOnClickViewListener(R.id.btn_customers, this);
        setOnClickViewListener(R.id.btn_contacts, this);

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
    public void onClick(View view) {
        Intent intent = null;

        switch(view.getId()) {
        case R.id.btn_projects:
//          Toast.makeText(this, R.string.projects, Toast.LENGTH_SHORT).show();
            intent = new Intent(SalesDConstant.ACTION_PROJECT_LIST);
            break;
        case R.id.btn_customers:
            Toast.makeText(this, R.string.customers, Toast.LENGTH_SHORT).show();
            break;
        case R.id.btn_contacts:
            Toast.makeText(this, R.string.contacts, Toast.LENGTH_SHORT).show();
            break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    protected void setOnClickViewListener(int id, View.OnClickListener listener) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(listener);
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
