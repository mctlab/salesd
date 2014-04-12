package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListFragment extends ListFragment
        implements CustomerQueryHandler.OnQueryCompleteListener {

    public static final String ARG_LEADER_ID = "argLeaderId";
    public static final String ARG_HEADER_LABEL = "argHeaderLabel";
    public static final String ARG_EMPTY_HINT = "argEmptyHint";

    protected CustomerQueryHandler mQueryHandler;
    protected ListAdapter mAdapter;

    protected TextView mEmptyView;

    protected long mLeaderId;

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
        Bundle args = getArguments();
        String headerLable = args == null ? null : args.getString(ARG_HEADER_LABEL);
        String emptyHint = args == null ? null : args.getString(ARG_EMPTY_HINT);

        Long id = args == null ? -1 : args.getLong(ARG_LEADER_ID, -1);
        mLeaderId = id == null ? -1 : id;

        if (!TextUtils.isEmpty(headerLable)) {
            TextView header = (TextView) view.findViewById(R.id.header);
            header.setText(headerLable);
            header.setVisibility(View.VISIBLE);

            view.findViewById(R.id.header_divider).setVisibility(View.VISIBLE);
        }

        mEmptyView = (TextView) view.findViewById(R.id.empty);
        if (!TextUtils.isEmpty(emptyHint)) {
            mEmptyView.setText(emptyHint);
        }

        mQueryHandler = new CustomerQueryHandler(getActivity().getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);
        mAdapter = new ListAdapter(getActivity());
        getListView().setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLeaderId > 0) {
            mQueryHandler.startQueryFollowers(0, -1, mLeaderId);
        } else {
            mQueryHandler.startQueryContacts(0, -1);
        }
    }

    @Override
    public void onDestroyView() {
        mAdapter.changeCursor(null);
        super.onDestroyView();
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
        Intent intent = new Intent(SalesDConstant.ACTION_CONTACT_DETAIL);
        intent.putExtra(SalesDConstant.EXTRA_ID, id);
        getActivity().startActivity(intent);
    }

    class ViewHolder {
        TextView mNameTextView;
        TextView mNumberTextView;
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

            holder.mNameTextView.setText(mQueryHandler.getContactName(cursor));
            holder.mNumberTextView.setText(mQueryHandler.getContactPhoneNumber(cursor));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.contact_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            view.setTag(holder);

            holder.mNameTextView = (TextView) view.findViewById(R.id.name);
            holder.mNumberTextView = (TextView) view.findViewById(R.id.number);

            return view;
        }

    }
}
