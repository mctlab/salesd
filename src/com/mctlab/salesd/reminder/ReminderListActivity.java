package com.mctlab.salesd.reminder;

import com.mctlab.salesd.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

public class ReminderListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.reminder_list_activity);

        Bundle args = new Bundle();
        args.putBoolean(ReminderListFragment.ARG_FULL_SCREEN, true);
        ReminderListFragment fragment = new ReminderListFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.reminders_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
