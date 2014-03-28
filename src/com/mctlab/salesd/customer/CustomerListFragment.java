package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.provider.TasksDatabaseHelper.CustomersColumns;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CustomerListFragment extends ListFragment
        implements CustomerQueryHandler.OnQueryCompleteListener {

    protected CustomerQueryHandler mQueryHandler;
    protected ListAdapter mAdapter;

    protected View mEmptyView;

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
        mEmptyView = view.findViewById(R.id.empty);

        mQueryHandler = new CustomerQueryHandler(getActivity().getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);
        mAdapter = new ListAdapter(getActivity());
        getListView().setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mQueryHandler.startQueryCustomers(0);
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mEmptyView.setVisibility(View.GONE);
            mAdapter.changeCursor(cursor);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mAdapter.changeCursor(null);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(SalesDConstant.ACTION_CUSTOMER_DETAIL);
        intent.putExtra(SalesDConstant.EXTRA_ID, id);
        getActivity().startActivity(intent);
    }

    class ViewHolder {
        TextView mNameTextView;
        TextView mCategoryTextView;
        TextView mDescriptionTextView;
    }

    class ListAdapter extends CursorAdapter {
        protected LayoutInflater mInflater;
        protected Resources mResources;

        public ListAdapter(Context context) {
            super(context, null, false);
            mInflater = LayoutInflater.from(context);
            mResources = getResources();
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            holder.mNameTextView.setText(mQueryHandler.getName(cursor));
            holder.mDescriptionTextView.setText(mQueryHandler.getDescription(cursor));

            String[] array = mResources.getStringArray(R.array.customer_category_values);
            int category = mQueryHandler.getCategory(cursor);
            switch (category) {
            case CustomersColumns.CATEGORY_INSTITUTE_OF_DESIGN:
                holder.mCategoryTextView.setText(array[1]);
                break;
            case CustomersColumns.CATEGORY_GENERAL_CONTRACTOR:
                holder.mCategoryTextView.setText(array[2]);
                break;
            case CustomersColumns.CATEGORY_DIRECT_OWNER:
                holder.mCategoryTextView.setText(array[3]);
                break;
            case CustomersColumns.CATEGORY_OTHERS:
                holder.mCategoryTextView.setText(array[4]);
                break;
            default:
                holder.mCategoryTextView.setText(array[0]);
                break;
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.customer_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            view.setTag(holder);

            holder.mNameTextView = (TextView) view.findViewById(R.id.name);
            holder.mCategoryTextView = (TextView) view.findViewById(R.id.category);
            holder.mDescriptionTextView = (TextView) view.findViewById(R.id.description);

            return view;
        }

    }
}
