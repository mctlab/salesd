package com.mctlab.salesd.project;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.util.LogUtil;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProjectListFragment extends ListFragment
        implements ProjectQueryHandler.OnQueryCompleteListener {

    protected ProjectQueryHandler mQueryHandler;
    protected ContentObserver mContentObserver;
    protected ListAdapter mAdapter;

    protected View mEmptyView;

    private boolean mCustomerRelated = false;
    private long mCustomerId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (mCustomerRelated) {
                    mQueryHandler.startQueryProjects(0, mCustomerId);
                } else {
                    mQueryHandler.startQueryProjects(0);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.project_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCustomerId = args.getLong(SalesDConstant.EXTRA_CUSTOMER_ID, -1);
            mCustomerRelated = true;
        }

        mEmptyView = view.findViewById(R.id.empty);

        mQueryHandler = new ProjectQueryHandler(getActivity().getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);
        mAdapter = new ListAdapter(getActivity());
        getListView().setAdapter(mAdapter);

        getActivity().getContentResolver().registerContentObserver(
                TasksProvider.PROJECTS_CONTENT_URI, true, mContentObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCustomerRelated) {
            mQueryHandler.startQueryProjects(0, mCustomerId);
        } else {
            mQueryHandler.startQueryProjects(0);
        }
    }

    @Override
    public void onDestroyView() {
        mAdapter.changeCursor(null);
        getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(SalesDConstant.ACTION_PROJECT_DETAIL);
        intent.putExtra(SalesDConstant.EXTRA_ID, id);
//      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getActivity().startActivity(intent);
    }

    class ViewHolder {
        TextView mNameTextView;
        TextView mDescriptionTextView;
        ImageView mPriorityIndicator;
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

            holder.mNameTextView.setText(mQueryHandler.getProjectName(cursor));
            holder.mDescriptionTextView.setText(mQueryHandler.getProjectDescription(cursor));

            if (mQueryHandler.getProjectPriority(cursor) == ProjectsColumns.PRIORITY_IMPORTANT) {
                holder.mPriorityIndicator.setImageResource(R.drawable.priority_indicator_1);
            } else {
                holder.mPriorityIndicator.setImageResource(R.drawable.priority_indicator_3);
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.project_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            view.setTag(holder);

            holder.mNameTextView = (TextView) view.findViewById(R.id.name);
            holder.mDescriptionTextView = (TextView) view.findViewById(R.id.description);
            holder.mPriorityIndicator = (ImageView) view.findViewById(R.id.priority_indicator);

            return view;
        }

    }

}
