package com.mctlab.salesd.project;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ConfigListFragment extends ListFragment
        implements ProjectQueryHandler.OnQueryCompleteListener, View.OnClickListener {

    protected ProjectQueryHandler mQueryHandler;
    protected ListAdapter mAdapter;

    protected View mEmptyView;

    private long mProjectId = -1;

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
        Bundle args = getArguments();
        if (args != null) {
            mProjectId = args.getLong(SalesDConstant.EXTRA_PROJECT_ID, -1);
        }

        mEmptyView = view.findViewById(R.id.empty);

        view.findViewById(R.id.add).setOnClickListener(this);

        mQueryHandler = new ProjectQueryHandler(getActivity().getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);

        mAdapter = new ListAdapter(getActivity());
        getListView().setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mQueryHandler.startQueryConfig(0, mProjectId);
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
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SalesDConstant.ACTION_CONFIG_EDIT);
        intent.putExtra(SalesDConstant.EXTRA_PROJECT_ID, mProjectId);
        getActivity().startActivity(intent);
    }

    class ViewHolder {
        TextView mTypeTextView;
        TextView mCategoryTextView;
        TextView mNumberTextView;
    }

    class ListAdapter extends CursorAdapter {
        protected LayoutInflater mInflater;

        public ListAdapter(Context context) {
            super(context, null, false);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            holder.mTypeTextView.setText(mQueryHandler.getConfigType(cursor));
            holder.mCategoryTextView.setText(mQueryHandler.getConfigCategory(cursor));
            holder.mNumberTextView.setText(String.valueOf(mQueryHandler.getConfigNumber(cursor)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.config_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            view.setTag(holder);

            holder.mTypeTextView = (TextView) view.findViewById(R.id.type);
            holder.mCategoryTextView = (TextView) view.findViewById(R.id.category);
            holder.mNumberTextView = (TextView) view.findViewById(R.id.number);

            return view;
        }

    }

}
