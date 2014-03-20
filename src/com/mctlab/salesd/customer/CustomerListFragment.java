package com.mctlab.salesd.customer;

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

public class CustomerListFragment extends ListFragment {

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
        return inflater.inflate(R.layout.customer_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(SalesDConstant.ACTION_CUSTOMER_DETAIL);
        intent.putExtra(CustomerDetailActivity.EXTRA_ID, position);
        getActivity().startActivity(intent);
    }

    protected void setupList() {
        mData = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(NAME, "Customer 1");
        item.put(DESCRIPTION, "Description of customer 1");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Customer 2");
        item.put(DESCRIPTION, "Description of customer 2");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(NAME, "Customer 3");
        item.put(DESCRIPTION, "Description of customer 3");
        mData.add(item);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData,
                R.layout.customer_list_item, mFrom, mTo);
        getListView().setAdapter(adapter);
    }
}
