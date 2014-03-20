package com.mctlab.salesd.customer;

import java.util.ArrayList;
import java.util.HashMap;

import com.mctlab.salesd.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ContactListFragment extends ListFragment {

    protected static final String NAME = "name";
    protected static final String NUMBER = "number";

    protected final String[] mFrom = new String[] {
            NAME, NUMBER };

    protected final int[] mTo = new int[] {
            R.id.name, R.id.number };

    protected ArrayList<HashMap<String, Object>> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupList();
    }

    protected void setupList() {
        mData = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(NAME, "Contact 1");
        item.put(NUMBER, "13800138001");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Contact 2");
        item.put(NUMBER, "13800138002");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Contact 3");
        item.put(NUMBER, "13800138003");
        mData.add(item);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData,
                R.layout.contact_list_item, mFrom, mTo);
        getListView().setAdapter(adapter);
    }
}
