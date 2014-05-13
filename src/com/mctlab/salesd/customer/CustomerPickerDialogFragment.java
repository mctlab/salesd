package com.mctlab.salesd.customer;

import java.util.ArrayList;
import java.util.List;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.customer.CustomerListFragment.ListAdapter;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CustomerPickerDialogFragment extends DialogFragment
        implements CustomerQueryHandler.OnQueryCompleteListener, View.OnClickListener,
            AdapterView.OnItemClickListener {

    protected static final int MODE_CREATE_NEW_CONTACT = 0;
    protected static final int MODE_JOIN_PROJECT = 1;

    public interface OnPickCustomersListener {
        public void OnPickCustomers(List<Long> ids);
    }

    private TextView mMessage;
    private View mProgressRow;
    private View mProgress;
    private View mCancel;
    private View mConfirm;

    protected long mProjectId;
    protected int mMode;

    protected OnPickCustomersListener mOnPickCustomersListener;
    protected ListAdapter mAdapter;

    CustomerQueryHandler mQueryHandler;

    public static void actionCreateNewContact(FragmentManager fragmentManager) {
        CustomerPickerDialogFragment dialog = new CustomerPickerDialogFragment();
        dialog.mMode = MODE_CREATE_NEW_CONTACT;
        dialog.show(fragmentManager, null);
    }

    public static void actionJoinProject(FragmentManager fragmentManager, long projectId,
            OnPickCustomersListener listener) {
        CustomerPickerDialogFragment dialog = new CustomerPickerDialogFragment();
        dialog.mProjectId = projectId;
        dialog.mMode = MODE_JOIN_PROJECT;
        dialog.setOnPickCustomersListener(listener);
        dialog.show(fragmentManager, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueryHandler = new CustomerQueryHandler(getActivity().getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);
        if (mMode == MODE_JOIN_PROJECT) {
            mQueryHandler.startQueryCustomers(0, mProjectId, true);
        } else {
            mQueryHandler.startQueryCustomers(0);
        }

        mAdapter = new ListAdapter(getActivity(), mQueryHandler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setTitle(R.string.dlg_title_pick_customer);

        View view = inflater.inflate(R.layout.customer_picker_dialog_fragment, container);

        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(this);

        mMessage = (TextView) view.findViewById(R.id.message);
        mProgressRow = view.findViewById(R.id.progress_row);
        mProgress = view.findViewById(R.id.progress_bar);

        mCancel = view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

        mConfirm = view.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.changeCursor(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            mQueryHandler.cancelOperation(0);
        } else if (id == R.id.confirm) {
            Intent intent = new Intent(SalesDConstant.ACTION_CUSTOMER_EDIT);
            startActivity(intent);
        }
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (mMode == MODE_CREATE_NEW_CONTACT) {
            Intent intent = new Intent(SalesDConstant.ACTION_CONTACT_EDIT);
            intent.putExtra(SalesDConstant.EXTRA_CUSTOMER_ID, id);
            startActivity(intent);
        } else if (mMode == MODE_JOIN_PROJECT) {
            if (mOnPickCustomersListener != null) {
                ArrayList<Long> ids = new ArrayList<Long>();
                ids.add(id);
                mOnPickCustomersListener.OnPickCustomers(ids);
            }
        }
        dismiss();
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mProgressRow.setVisibility(View.GONE);
        } else {
            mMessage.setText(R.string.dlg_message_create_customer);
            mConfirm.setVisibility(View.VISIBLE);
        }
        mProgress.setVisibility(View.GONE);
        mAdapter.changeCursor(cursor);
    }

    protected void setOnPickCustomersListener(OnPickCustomersListener listener) {
        mOnPickCustomersListener = listener;
    }
}
