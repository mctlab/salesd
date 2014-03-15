package com.mctlab.salesd.project;

import java.util.ArrayList;
import java.util.HashMap;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ProjectListFragment extends ListFragment {

    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String PRIORITY = "priority";

    protected final String[] mFrom = new String[] {
            NAME, DESCRIPTION, PRIORITY };

    protected final int[] mTo = new int[] {
            R.id.name, R.id.description, R.id.priority_indicator };

    protected ArrayList<HashMap<String, Object>> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.project_list_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
//      String project = (String) mData.get(position).get(NAME);
//      Toast.makeText(getActivity(), project, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SalesDConstant.ACTION_PROJECT_DETAIL);
        intent.putExtra(ProjectDetailActivity.EXTRA_ID, position);
        getActivity().startActivity(intent);
    }

    protected void setupList() {
        mData = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(NAME, "Project 1");
        item.put(DESCRIPTION, "Description of project 1");
        item.put(PRIORITY, R.drawable.priority_indicator_1);
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Project 2");
        item.put(DESCRIPTION, "Description of project 2");
        item.put(PRIORITY, R.drawable.priority_indicator_2);
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Project 3");
        item.put(DESCRIPTION, "Description of project 3");
        item.put(PRIORITY, R.drawable.priority_indicator_3);
        mData.add(item);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData,
                R.layout.project_list_item, mFrom, mTo);
        getListView().setAdapter(adapter);
    }
}
