package com.mctlab.salesd.schedule;

import com.mctlab.salesd.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ScheduleListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schedule_list_activity);

        Bundle args = new Bundle();
        args.putBoolean(ScheduleListFragment.ARG_FULL_SCREEN, true);
        ScheduleListFragment fragment = new ScheduleListFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.schedules_container, fragment);
        transaction.commit();
    }

}
