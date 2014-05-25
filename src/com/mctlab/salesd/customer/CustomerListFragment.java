package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.util.LogUtil;

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
        implements CustomerQueryHandler.OnQueryCompleteListener, View.OnClickListener {

    public interface OnIntroduceCustomersListener {
        public void OnIntroduceCustomers();
    }

    protected OnIntroduceCustomersListener mOnIntroduceCustomersListener;

    protected CustomerQueryHandler mQueryHandler;
    protected ListAdapter mAdapter;

    protected View mEmptyView;

    private boolean mProjectRelated = false;
    private long mProjectId = -1;

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
        Bundle args = getArguments();
        if (args != null) {
            mProjectId = args.getLong(SalesDConstant.EXTRA_PROJECT_ID, -1);
            mProjectRelated = true;
        }

        if (mProjectRelated) {
            View introduce = view.findViewById(R.id.introduce);
            introduce.setVisibility(View.VISIBLE);
            introduce.setOnClickListener(this);
            view.findViewById(R.id.introduce_divider).setVisibility(View.VISIBLE);
        }

        mEmptyView = view.findViewById(R.id.empty);

        mQueryHandler = new CustomerQueryHandler(getActivity());
        mQueryHandler.setOnQueryCompleteListener(this);
        mAdapter = new ListAdapter(getActivity(), mQueryHandler);
        getListView().setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onDestroyView() {
        mAdapter.changeCursor(null);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (mOnIntroduceCustomersListener != null) {
            mOnIntroduceCustomersListener.OnIntroduceCustomers();
        }
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(SalesDConstant.ACTION_CUSTOMER_DETAIL);
        intent.putExtra(SalesDConstant.EXTRA_ID, id);
//      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getActivity().startActivity(intent);
    }

    public void setOnIntroduceCustomersListener(OnIntroduceCustomersListener listener) {
        mOnIntroduceCustomersListener = listener;
    }

    public void refreshView() {
        LogUtil.d("refresh CustomerListFragment");
        if (mProjectRelated) {
            mQueryHandler.startQueryCustomers(0, mProjectId, false);
        } else {
            mQueryHandler.startQueryCustomers(0);
        }
    }

    public static class ListAdapter extends CursorAdapter {

        class ViewHolder {
            TextView mNameTextView;
            TextView mCategoryTextView;
            TextView mDescriptionTextView;
        }

        protected CustomerQueryHandler mQueryHandler;
        protected LayoutInflater mInflater;
        protected Resources mRes;

        public ListAdapter(Context context, CustomerQueryHandler handler) {
            super(context, null, false);
            mQueryHandler = handler;
            mInflater = LayoutInflater.from(context);
            mRes = context.getResources();
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            holder.mNameTextView.setText(mQueryHandler.getCustomerName(cursor));
            holder.mDescriptionTextView.setText(mQueryHandler.getCustomerDescription(cursor));

            StringBuilder builder = new StringBuilder();
            String comma = mRes.getString(R.string.comma);
            boolean needComma = false;

            if (mQueryHandler.isCustomerHostManufacturer(cursor)) {
                builder.append(mRes.getString(R.string.customer_category_host_manufacturer));
                needComma = true;
            }

            if (mQueryHandler.isCustomerInstituteOfDesign(cursor)) {
                if (needComma) builder.append(comma);
                builder.append(mRes.getString(R.string.customer_category_institute_of_design));
                needComma = true;
            }

            if (mQueryHandler.isCustomerGeneralContractor(cursor)) {
                if (needComma) builder.append(comma);
                builder.append(mRes.getString(R.string.customer_category_general_contractor));
                needComma = true;
            }

            if (mQueryHandler.isCustomerDirectOwner(cursor)) {
                if (needComma) builder.append(comma);
                builder.append(mRes.getString(R.string.customer_category_direct_owner));
                needComma = true;
            }

            if (mQueryHandler.isCustomerOthers(cursor)) {
                if (needComma) builder.append(comma);
                builder.append(mRes.getString(R.string.customer_category_others));
                needComma = true;
            }

            holder.mCategoryTextView.setText(builder.toString());
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
