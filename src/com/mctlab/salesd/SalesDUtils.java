package com.mctlab.salesd;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SalesDUtils {

    public static View findChildView(Activity parent, int childId) {
        if (parent != null) {
            return parent.findViewById(childId);
        }
        return null;
    }

    public static View findChildView(View parent, int childId) {
        if (parent != null) {
            return parent.findViewById(childId);
        }
        return null;
    }

    public static void setChildViewVisibility(View parent, int childId, int visibility) {
        View child = findChildView(parent, childId);
        if (child != null) {
            child.setVisibility(visibility);
        }
    }

    public static void setChildViewOnClickListener(Activity parent, int childId, View.OnClickListener listener) {
        View child = findChildView(parent, childId);
        if (child != null) {
            child.setOnClickListener(listener);
        }
    }

    public static void setChildViewOnClickListener(View parent, int childId, View.OnClickListener listener) {
        View child = findChildView(parent, childId);
        if (child != null) {
            child.setOnClickListener(listener);
        }
    }

    public static Spinner setupSpinner(Activity parent, int spinnerId, int valuesId) {
        Spinner spinner = (Spinner) findChildView(parent, spinnerId);
        if (spinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent, valuesId, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
        return spinner;
    }
}
