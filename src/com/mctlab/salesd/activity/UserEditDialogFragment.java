package com.mctlab.salesd.activity;

import com.mctlab.salesd.R;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.provider.TasksDatabaseHelper.UsersColumns;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class UserEditDialogFragment extends DialogFragment
        implements UserQueryHandler.OnQueryCompleteListener, View.OnClickListener {

    public interface OnUserInfoUpdatedListener {
        public void OnUserInfoUpdated();
    }

    private UserQueryHandler mQueryHandler;
    private OnUserInfoUpdatedListener mOnUserInfoUpdatedListener;

    private EditText mUserName;
    private EditText mUserAnnualTarget;
    private EditText mUserCompleted;

    private long mUserId = 0L;

    public static void actionUpdateUserInfo(FragmentManager fragmentManager, long userId,
            OnUserInfoUpdatedListener listener) {
        UserEditDialogFragment dialog = new UserEditDialogFragment();
        dialog.mUserId = userId;
        dialog.setOnUserInfoUpdatedListener(listener);
        dialog.show(fragmentManager, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQueryHandler = new UserQueryHandler(getActivity().getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setTitle(R.string.dlg_title_user_info);

        View view = inflater.inflate(R.layout.user_edit_dialog_fragment, container);

        mUserName = (EditText) view.findViewById(R.id.user_name);
        mUserAnnualTarget = (EditText) view.findViewById(R.id.user_annual_target);
        mUserCompleted = (EditText) view.findViewById(R.id.user_completed);

        view.findViewById(R.id.confirm).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mQueryHandler.startQueryUser(0, mUserId);
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            updateUserInfo(cursor);
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.confirm:
            saveUserInfo();
            break;
        case R.id.cancel:
            mQueryHandler.cancelOperation(0);
            dismiss();
            break;
        }
    }

    public void setOnUserInfoUpdatedListener(OnUserInfoUpdatedListener listener) {
        mOnUserInfoUpdatedListener = listener;
    }

    private void updateUserInfo(Cursor cursor) {
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            mUserName.setText(mQueryHandler.getUserName(cursor));
            mUserAnnualTarget.setText(String.valueOf(mQueryHandler.getUserAnnualTarget(cursor)));
            mUserCompleted.setText(String.valueOf(mQueryHandler.getUserCompleted(cursor)));
        }
    }

    private void saveUserInfo() {
        Context context = getActivity();

        String userName = mUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            String field = getString(R.string.hint_user_name);
            String message = getString(R.string.tip_field_could_not_be_null, field);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }

        String userAnnualTargetStr = mUserAnnualTarget.getText().toString().trim();
        int userAnnualTarget = 0;
        try {
            userAnnualTarget = Integer.parseInt(userAnnualTargetStr);
        } catch (NumberFormatException e) {
        }

        String userCompletedStr = mUserCompleted.getText().toString().trim();
        int userCompleted = 0;
        try {
            userCompleted = Integer.parseInt(userCompletedStr);
        } catch (NumberFormatException e) {
        }

        if (userCompleted > userAnnualTarget) {
            String message = getString(R.string.tip_invalid_annual_target_or_completed);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(UsersColumns.NAME, userName);
        values.put(UsersColumns.ANNUAL_TARGET, userAnnualTarget);
        values.put(UsersColumns.COMPLETED, userCompleted);

        ContentResolver resolver = context.getContentResolver();
        boolean failed = true;
        if (mUserId > 0) {
            Uri uri = ContentUris.withAppendedId(TasksProvider.USERS_CONTENT_URI, mUserId);
            int count = resolver.update(uri, values, null, null);
            failed = count <= 0;
        } else {
            Uri uri = resolver.insert(TasksProvider.USERS_CONTENT_URI, values);
            failed = uri == null;
        }

        if (failed) {
            Toast.makeText(context, R.string.tip_save_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, R.string.tip_save_succeed, Toast.LENGTH_SHORT).show();
        mOnUserInfoUpdatedListener.OnUserInfoUpdated();
        dismiss();
    }
}
