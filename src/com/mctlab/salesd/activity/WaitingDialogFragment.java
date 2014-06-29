package com.mctlab.salesd.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;

public class WaitingDialogFragment extends DialogFragment {

    private static WaitingDialogFragment mDialog;
    private String mMessage;

    public static DialogFragment actionShowProgress(FragmentManager fragmentManager,
            String message) {
        actionDismissProgress();
        mDialog = new WaitingDialogFragment();
        mDialog.mMessage = message;
        mDialog.show(fragmentManager, null);
        return mDialog;
    }

    public static void actionDismissProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(mMessage);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

}
