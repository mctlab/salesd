package com.mctlab.salesd.project;

import com.mctlab.salesd.R;
import com.mctlab.salesd.reminder.ReminderListFragment;
import com.mctlab.salesd.schedule.ScheduleListFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class ProjectDetailActivity extends Activity {

    public static final String EXTRA_ID = "id";

    private static final int TAB_INDEX_CONFIG = 0;
    private static final int TAB_INDEX_CUSTOMER = 1;
    private static final int TAB_INDEX_VISIT_SCHEDULE = 2;
    private static final int TAB_INDEX_VISIT_RECORD = 3;

    private static final int TAB_INDEX_COUNT = 4;

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case TAB_INDEX_CONFIG:
                return new ReminderListFragment();
            case TAB_INDEX_CUSTOMER:
                return new ScheduleListFragment();
            case TAB_INDEX_VISIT_SCHEDULE:
                return new ReminderListFragment();
            case TAB_INDEX_VISIT_RECORD:
                return new ScheduleListFragment();
            }

            throw new IllegalStateException("No fragment at position " + position);
        }

        @Override
        public int getCount() {
            return TAB_INDEX_COUNT;
        }

    }

    protected class PageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

        }

    }

    private TextView mDescription;

    private int mId;

    private final PageChangeListener mPageChangeListener = new PageChangeListener();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.project_detail_activity);

        mDescription = (TextView) findViewById(R.id.description);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        mId = -1;
        if (getIntent() != null) {
            mId = getIntent().getIntExtra(EXTRA_ID, -1);
        }
        mId++;

        updateProjectInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_detail_options, menu);
        return true;
    }

    protected void updateProjectInfo() {
        setTitle("Project " + mId);

        mDescription.setText("Description of project " + mId);
    }

}
