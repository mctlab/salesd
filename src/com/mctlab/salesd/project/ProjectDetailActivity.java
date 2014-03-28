package com.mctlab.salesd.project;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.customer.CustomerListFragment;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.schedule.ScheduleListFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ProjectDetailActivity extends Activity implements View.OnClickListener,
        ProjectQueryHandler.OnQueryCompleteListener {

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
            Bundle args = new Bundle();
            Fragment fragment;

            switch (position) {
            case TAB_INDEX_CONFIG:
                fragment = new ConfigListFragment();
                break;
            case TAB_INDEX_CUSTOMER:
                fragment = new CustomerListFragment();
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
            case TAB_INDEX_CONFIG:
                return ProjectDetailActivity.this.getText(R.string.config);
            case TAB_INDEX_CUSTOMER:
                return ProjectDetailActivity.this.getText(R.string.customers);
            case TAB_INDEX_VISIT_SCHEDULE:
                return ProjectDetailActivity.this.getText(R.string.visit_schedules);
            case TAB_INDEX_VISIT_RECORD:
                return ProjectDetailActivity.this.getText(R.string.visit_records);
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
            case TAB_INDEX_CONFIG:
                tabUnderline = mConfigTabUnderline;
                break;
            case TAB_INDEX_CUSTOMER:
                tabUnderline = mCustomerTabUnderline;
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

    private ViewPager mViewPager;

    private TextView mConfigTabTitle;
    private TextView mCustomerTabTitle;
    private TextView mVisitScheduleTabTitle;
    private TextView mVisitRecordTabTitle;

    private View mConfigTabUnderline;
    private View mCustomerTabUnderline;
    private View mVisitScheduleTabUnderline;
    private View mVisitRecordTabUnderline;
    private View mCurrentTabUnderline;

    private final PageChangeListener mPageChangeListener = new PageChangeListener();

    private TextView mPriorityTextView;
    private TextView mAmountTextView;
    private TextView mStatusTextView;
    private TextView mOwnerTextView;
    private TextView mDescriptionTextView;

    protected ProjectQueryHandler mQueryHandler;

    private long mId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.project_detail_activity);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, -1);
        }

        mQueryHandler = new ProjectQueryHandler(getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
        mQueryHandler.startQueryProject(0, mId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_detail_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mId > 0) {
            int itemId = item.getItemId();
            switch (itemId) {
            case R.id.opt_edit:
                Intent intent = new Intent(SalesDConstant.ACTION_PROJECT_EDIT);
                intent.putExtra(SalesDConstant.EXTRA_ID, mId);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.config_tab:
            mViewPager.setCurrentItem(TAB_INDEX_CONFIG);
            break;
        case R.id.customer_tab:
            mViewPager.setCurrentItem(TAB_INDEX_CUSTOMER);
            break;
        case R.id.visit_schedule_tab:
            mViewPager.setCurrentItem(TAB_INDEX_VISIT_SCHEDULE);
            break;
        case R.id.visit_record_tab:
            mViewPager.setCurrentItem(TAB_INDEX_VISIT_RECORD);
            break;
        }
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            updateProjectInfo(cursor);
        }
    }

    protected void initView() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        SalesDUtils.setChildViewOnClickListener(this, R.id.config_tab, this);
        mConfigTabUnderline = findViewById(R.id.config_tab_underline);
        mConfigTabTitle = (TextView) findViewById(R.id.config_tab_title);
        mConfigTabTitle.setText(adapter.getPageTitle(TAB_INDEX_CONFIG));

        SalesDUtils.setChildViewOnClickListener(this, R.id.customer_tab, this);
        mCustomerTabUnderline = findViewById(R.id.customer_tab_underline);
        mCustomerTabTitle = (TextView) findViewById(R.id.customer_tab_title);
        mCustomerTabTitle.setText(adapter.getPageTitle(TAB_INDEX_CUSTOMER));

        SalesDUtils.setChildViewOnClickListener(this, R.id.visit_schedule_tab, this);
        mVisitScheduleTabUnderline = findViewById(R.id.visit_schedule_tab_underline);
        mVisitScheduleTabTitle = (TextView) findViewById(R.id.visit_schedule_tab_title);
        mVisitScheduleTabTitle.setText(adapter.getPageTitle(TAB_INDEX_VISIT_SCHEDULE));

        SalesDUtils.setChildViewOnClickListener(this, R.id.visit_record_tab, this);
        mVisitRecordTabUnderline = findViewById(R.id.visit_record_tab_underline);
        mVisitRecordTabTitle = (TextView) findViewById(R.id.visit_record_tab_title);
        mVisitRecordTabTitle.setText(adapter.getPageTitle(TAB_INDEX_VISIT_RECORD));

        mPriorityTextView = (TextView) findViewById(R.id.priority);
        mAmountTextView = (TextView) findViewById(R.id.amount);
        mStatusTextView = (TextView) findViewById(R.id.status);
        mOwnerTextView = (TextView) findViewById(R.id.owner);
        mDescriptionTextView = (TextView) findViewById(R.id.description);
    }

    protected void updateProjectInfo(Cursor cursor) {
        Resources res = getResources();
        String name = mQueryHandler.getName(cursor);
        setTitle(name);

        int amount = mQueryHandler.getAmount(cursor);
        mAmountTextView.setText(String.valueOf(amount));

        String[] array = res.getStringArray(R.array.project_priority_values);
        int priority = mQueryHandler.getPriority(cursor);
        if (priority == ProjectsColumns.PRIORITY_IMPORTANT) {
            mPriorityTextView.setText(array[1]);
        } else {
            mPriorityTextView.setText(array[0]);
        }

        array = res.getStringArray(R.array.project_status_values);
        int status = mQueryHandler.getStatus(cursor);
        if (status == ProjectsColumns.STATUS_ONGOING) {
            mStatusTextView.setText(array[1]);
        } else if (status == ProjectsColumns.STATUS_COMPLETE) {
            mStatusTextView.setText(array[2]);
        } else {
            mStatusTextView.setText(array[0]);
        }

        String description = mQueryHandler.getDescription(cursor);
        mDescriptionTextView.setText(description);
    }

}
