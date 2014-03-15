package com.mctlab.salesd.reminder;

import java.util.ArrayList;
import java.util.HashMap;

import com.mctlab.salesd.R;
import com.mctlab.salesd.SalesDUtils;
import com.mctlab.salesd.constant.SalesDConstant;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ReminderListFragment extends ListFragment
        implements View.OnClickListener {

    protected static final String ARG_FULL_SCREEN = "argFullScreen";

    protected static final String CUSTOMER = "customer";
    protected static final String PROJECT = "project";
    protected static final String TIME = "time";

    protected final String[] mFrom = new String[] {
            CUSTOMER, PROJECT, TIME };

    protected final int[] mTo = new int[] {
            R.id.customer, R.id.project, R.id.time };

    protected ArrayList<HashMap<String, Object>> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reminder_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean fullScreen = false;
        if (getArguments() != null) {
            fullScreen = getArguments().getBoolean(ARG_FULL_SCREEN, false);
        }
        if (fullScreen) {
            SalesDUtils.setChildViewVisibility(view, R.id.header, View.GONE);
            SalesDUtils.setChildViewVisibility(view, R.id.show_more_reminder, View.GONE);
            SalesDUtils.setChildViewVisibility(view, R.id.header_divider, View.GONE);
            SalesDUtils.setChildViewVisibility(view, R.id.footer_divider, View.GONE);
        } else {
            SalesDUtils.setChildViewOnClickListener(view, R.id.show_more_reminder, this);
        }

        setupList();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SalesDConstant.ACTION_REMINDER_LIST);
        getActivity().startActivity(intent);
    }

    protected void setupList() {
        mData = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(CUSTOMER, "Customer 1");
        item.put(PROJECT, "Project 1");
        item.put(TIME, "10:00 AM");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(CUSTOMER, "Customer 2");
        item.put(PROJECT, "Project 2");
        item.put(TIME, "2:00 PM");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(CUSTOMER, "Customer 3");
        item.put(PROJECT, "Project 3");
        item.put(TIME, "3:00 PM");
        mData.add(item);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData,
                R.layout.reminder_list_item, mFrom, mTo);
        getListView().setAdapter(adapter);
    }

}
