package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.project.ProjectListFragment;
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
import android.view.View;
import android.widget.TextView;

public class CustomerDetailActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_ID = "id";

    private static final int TAB_INDEX_PROJECT = 0;
    private static final int TAB_INDEX_VISIT_SCHEDULE = 1;
    private static final int TAB_INDEX_VISIT_RECORD = 2;

    private static final int TAB_INDEX_COUNT = 3;

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            Fragment fragment;

            switch (position) {
            case TAB_INDEX_PROJECT:
                fragment = new ProjectListFragment();
                break;
            case TAB_INDEX_VISIT_SCHEDULE:
                args.putBoolean(ScheduleListFragment.ARG_FULL_SCREEN, true);
                fragment = new ScheduleListFragment();
                fragment.setArguments(args);
                break;
            case TAB_INDEX_VISIT_RECORD:
                args.putBoolean(ScheduleListFragment.ARG_FULL_SCREEN, true);
                fragment = new ScheduleListFragment();
                fragment.setArguments(args);
                break;
            default:
                throw new IllegalStateException("No fragment at position " + position);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_INDEX_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case TAB_INDEX_PROJECT:
                return CustomerDetailActivity.this.getText(R.string.projects);
            case TAB_INDEX_VISIT_SCHEDULE:
                return CustomerDetailActivity.this.getText(R.string.visit_schedules);
            case TAB_INDEX_VISIT_RECORD:
                return CustomerDetailActivity.this.getText(R.string.visit_records);
            }

            throw new IllegalStateException("No fragment at position " + position);
        }

    }

    protected class PageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            View tabUnderline;
            switch (position) {
            case TAB_INDEX_PROJECT:
                tabUnderline = mProjectTabUnderline;
                break;
            case TAB_INDEX_VISIT_SCHEDULE:
                tabUnderline = mVisitScheduleTabUnderline;
                break;
            case TAB_INDEX_VISIT_RECORD:
                tabUnderline = mVisitRecordTabUnderline;
                break;
            default:
                throw new IllegalStateException("Invalid position " + position);
            }

            if (mCurrentTabUnderline != null) {
                mCurrentTabUnderline.setVisibility(View.INVISIBLE);
            }
            mCurrentTabUnderline = tabUnderline;
            mCurrentTabUnderline.setVisibility(View.VISIBLE);
        }

    }
    private TextView mDescription;

    private TextView mProjectTabTitle;
    private TextView mVisitScheduleTabTitle;
    private TextView mVisitRecordTabTitle;

    private View mProjectTabUnderline;
    private View mVisitScheduleTabUnderline;
    private View mVisitRecordTabUnderline;
    private View mCurrentTabUnderline;

    private int mId;

    private final PageChangeListener mPageChangeListener = new PageChangeListener();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.customer_detail_activity);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        mDescription = (TextView) findViewById(R.id.description);

        SalesDUtils.setChildViewOnClickListener(this, R.id.project_tab, this);
        mProjectTabUnderline = findViewById(R.id.project_tab_underline);
        mProjectTabTitle = (TextView) findViewById(R.id.project_tab_title);
        mProjectTabTitle.setText(adapter.getPageTitle(TAB_INDEX_PROJECT));

        SalesDUtils.setChildViewOnClickListener(this, R.id.visit_schedule_tab, this);
        mVisitScheduleTabUnderline = findViewById(R.id.visit_schedule_tab_underline);
        mVisitScheduleTabTitle = (TextView) findViewById(R.id.visit_schedule_tab_title);
        mVisitScheduleTabTitle.setText(adapter.getPageTitle(TAB_INDEX_VISIT_SCHEDULE));

        SalesDUtils.setChildViewOnClickListener(this, R.id.visit_record_tab, this);
        mVisitRecordTabUnderline = findViewById(R.id.visit_record_tab_underline);
        mVisitRecordTabTitle = (TextView) findViewById(R.id.visit_record_tab_title);
        mVisitRecordTabTitle.setText(adapter.getPageTitle(TAB_INDEX_VISIT_RECORD));

        mId = -1;
        if (getIntent() != null) {
            mId = getIntent().getIntExtra(EXTRA_ID, -1);
        }
        mId++;

        updateCustomerInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_detail_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.project_tab:
            mViewPager.setCurrentItem(TAB_INDEX_PROJECT);
            break;
        case R.id.visit_schedule_tab:
            mViewPager.setCurrentItem(TAB_INDEX_VISIT_SCHEDULE);
            break;
        case R.id.visit_record_tab:
            mViewPager.setCurrentItem(TAB_INDEX_VISIT_RECORD);
            break;
        }
    }

    protected void updateCustomerInfo() {
        setTitle("Customer " + mId);

        mDescription.setText("Description of customer " + mId);
    }

}
