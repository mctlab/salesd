package com.mctlab.salesd.project;

import java.util.ArrayList;
import java.util.HashMap;

import com.mctlab.salesd.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ConfigListFragment extends ListFragment {

    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";

    protected final String[] mFrom = new String[] {
            NAME, DESCRIPTION };

    protected final int[] mTo = new int[] {
            R.id.name, R.id.description };

    protected ArrayList<HashMap<String, Object>> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.config_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupList();
    }

    protected void setupList() {
        mData = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(NAME, "Config 1");
        item.put(DESCRIPTION, "Description of config 1");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Config 2");
        item.put(DESCRIPTION, "Description of config 2");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Config 3");
        item.put(DESCRIPTION, "Description of config 3");
        mData.add(item);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData,
                R.layout.config_list_item, mFrom, mTo);
        getListView().setAdapter(adapter);
    }
}
