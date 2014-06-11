package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.project.ProjectListFragment;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ContactsColumns;
import com.mctlab.salesd.schedule.ScheduleListFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CustomerDetailActivity extends Activity implements View.OnClickListener,
        CustomerQueryHandler.OnQueryCompleteListener,
        ContactTplSelectorDialogFragment.OnContactTemplateSelectedListener {

    private static final int TOKEN_QUERY_CUSTOMER = 0;
    private static final int TOKEN_QUERY_CUSTOMER_LEADER = 1;

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
                args.putLong(SalesDConstant.EXTRA_CUSTOMER_ID, mId);
                fragment = new ProjectListFragment();
                fragment.setArguments(args);
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

    private TextView mProjectTabTitle;
    private TextView mVisitScheduleTabTitle;
    private TextView mVisitRecordTabTitle;

    private View mProjectTabUnderline;
    private View mVisitScheduleTabUnderline;
    private View mVisitRecordTabUnderline;
    private View mCurrentTabUnderline;

    private long mId = -1;
    private long mLeaderId = -1;
    private String mLeader;

    private final PageChangeListener mPageChangeListener = new PageChangeListener();
    private ViewPager mViewPager;

    private CustomerQueryHandler mQueryHandler;

    private TextView mCategoryTextView;
    private TextView mAddressTextView;
    private TextView mLeaderTextView;
    private TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.customer_detail_activity);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, -1);
        }

        mQueryHandler = new CustomerQueryHandler(this);
        mQueryHandler.setOnQueryCompleteListener(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
        mQueryHandler.startQueryCustomer(TOKEN_QUERY_CUSTOMER, mId);
        mQueryHandler.startQueryCustomerLeader(TOKEN_QUERY_CUSTOMER_LEADER, mId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_detail_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mId > 0) {
            int itemId = item.getItemId();
            switch (itemId) {
            case R.id.opt_edit:
                Intent intent = new Intent(SalesDConstant.ACTION_CUSTOMER_EDIT);
                intent.putExtra(SalesDConstant.EXTRA_ID, mId);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnContactTemplateSelected(String tplName) {
        Builder builder = TasksProvider.CONTACTS_CONTENT_URI.buildUpon();
        builder.appendEncodedPath("template");
        builder.appendEncodedPath(tplName);

        ContentValues values = new ContentValues();
        values.put(ContactsColumns.CUSTOMER_ID, mId);

        getContentResolver().insert(builder.build(), values);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.leader:
            Intent intent = null;
            if (mLeaderId > 0) {
                intent = new Intent(SalesDConstant.ACTION_CONTACT_DETAIL);
                intent.putExtra(SalesDConstant.EXTRA_ID, mLeaderId);
                startActivity(intent);
            } else {
//              intent = new Intent(SalesDConstant.ACTION_CONTACT_EDIT);
//              intent.putExtra(SalesDConstant.EXTRA_CUSTOMER_ID, mId);
//              intent.putExtra(ContactEditActivity.EXTRA_IS_LEADER, true);
//              intent.putExtra(ContactEditActivity.EXTRA_MY_LEADER, mLeader);

                ContactTplSelectorDialogFragment.actionSelectContactTemplate(
                        getFragmentManager(), this);
            }
            break;
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

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (token == TOKEN_QUERY_CUSTOMER) {
                    updateCustomerInfo(cursor);
                } else if (token == TOKEN_QUERY_CUSTOMER_LEADER) {
                    updateCustomerLeader(cursor);
                }
            }
            cursor.close();
        }
    }

    protected void initView() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

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

        mCategoryTextView = (TextView) findViewById(R.id.category);
        mAddressTextView = (TextView) findViewById(R.id.address);
        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mLeaderTextView = (TextView) findViewById(R.id.leader);
        mLeaderTextView.setOnClickListener(this);
    }

    protected void updateCustomerInfo(Cursor cursor) {
        String name = mQueryHandler.getCustomerName(cursor);
        setTitle(name);

        String address = mQueryHandler.getCustomerAddress(cursor);
        mAddressTextView.setText(address);

        StringBuilder builder = new StringBuilder();
        String comma = getString(R.string.comma);
        boolean needComma = false;

        if (mQueryHandler.isCustomerHostManufacturer(cursor)) {
            builder.append(getString(R.string.customer_category_host_manufacturer));
            needComma = true;
        }

        if (mQueryHandler.isCustomerInstituteOfDesign(cursor)) {
            if (needComma) builder.append(comma);
            builder.append(getString(R.string.customer_category_institute_of_design));
            needComma = true;
        }

        if (mQueryHandler.isCustomerGeneralContractor(cursor)) {
            if (needComma) builder.append(comma);
            builder.append(getString(R.string.customer_category_general_contractor));
            needComma = true;
        }

        if (mQueryHandler.isCustomerDirectOwner(cursor)) {
            if (needComma) builder.append(comma);
            builder.append(getString(R.string.customer_category_direct_owner));
            needComma = true;
        }

        if (mQueryHandler.isCustomerOthers(cursor)) {
            if (needComma) builder.append(comma);
            builder.append(getString(R.string.customer_category_others));
            needComma = true;
        }

        mCategoryTextView.setText(builder.toString());

        String description = mQueryHandler.getCustomerDescription(cursor);
        mDescriptionTextView.setText(description);
    }

    protected void updateCustomerLeader(Cursor cursor) {
        mLeaderId = mQueryHandler.getContactId(cursor);

        mLeader = mQueryHandler.getContactName(cursor);
        mLeaderTextView.setText(Html.fromHtml("<u>" + mLeader + "</u>"));
    }

}
