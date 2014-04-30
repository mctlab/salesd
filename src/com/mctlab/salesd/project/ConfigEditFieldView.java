package com.mctlab.salesd.project;

import com.mctlab.salesd.R;
import com.mctlab.salesd.project.ConfigEntity.ConfigValues;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ConfigColumns;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ConfigEditFieldView extends LinearLayout {

    public interface EditFieldListener {
        /**
         * Called when the given {@link Editor} is requested to be deleted by the user.
         */
        public void onDeleteRequested(ConfigEditFieldView field);

        /**
         * Called when the given {@link Editor} has a request, for example it
         * wants to select a photo.
         */
        public void onRequest(int request);

        public static final int REQUEST_PICK_PHOTO = 1;
        public static final int FIELD_CHANGED = 2;
        public static final int FIELD_TURNED_EMPTY = 3;
        public static final int FIELD_TURNED_NON_EMPTY = 4;

        // The editor has switched between different representations of the same
        // data, e.g. from full name to structured name
        public static final int EDITOR_FORM_CHANGED = 5;

    }

    private EditFieldListener mListener;

    private boolean mAttachedToWindow;
    private boolean mWasEmpty = true;
    private boolean mIsDeletable = true;

    private EditText mTypeEditText;
    private EditText mNumberEditText;
    private View mDeleteButton;

    private ConfigValues mConfigValues;

    public ConfigEditFieldView(Context context) {
        super(context);
    }

    public ConfigEditFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDeleteButton = findViewById(R.id.delete_button_container);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // defer removal of this button so that the pressed state is visible shortly
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // Don't do anything if the view is no longer attached to the window
                        // (This check is needed because when this {@link Runnable} is executed,
                        // we can't guarantee the view is still valid.
                        if (!mAttachedToWindow) {
                            return;
                        }
                        // Send the delete request to the listener (which will in turn call
                        // deleteEditor() on this view if the deletion is valid - i.e. this is not
                        // the last {@link Editor} in the section).
                        if (mListener != null) {
                            mListener.onDeleteRequested(ConfigEditFieldView.this);
                        }
                    }
                });
            }
        });

        mTypeEditText = (EditText)findViewById(R.id.type);
        mTypeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                afterChanged(ConfigColumns.TYPE, s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        });

        mNumberEditText = (EditText)findViewById(R.id.number);
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                   afterChanged(ConfigColumns.NUMBER, s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
    }

    public void setListener(EditFieldListener listener) {
        mListener = listener;
    }

    public void setValues(ConfigValues values) {
        mConfigValues = values;

        String type = mConfigValues.getAsString(ConfigColumns.TYPE);
        mTypeEditText.setText(type);

        Integer num = mConfigValues.getAsInteger(ConfigColumns.NUMBER, 0);
        String number = (num != null && num != 0) ? String.valueOf(num) : null;
        mNumberEditText.setText(number);

        // Show the delete button if we have a non-null value
        setDeleteButtonVisible(!isEmpty());
    }

    public void setDeleteButtonVisible(boolean visible) {
        if (mIsDeletable) {
            mDeleteButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    protected void afterChanged(String column, String value) {
        if (!isValueChanged(column, value)) {
            return;
        }

        saveValue(column, value);

        notifyListener();
    }

    protected boolean isValueChanged(String column, String value) {
        final String dbValue = mConfigValues.getAsString(column);
        // nullable fields (e.g. Middle Name) are usually represented as empty columns,
        // so lets treat null and empty space equivalently here
        final String dbValueNoNull = dbValue == null ? "" : dbValue;
        final String valueNoNull = value == null ? "" : value;
        return !TextUtils.equals(dbValueNoNull, valueNoNull);
    }

    protected void saveValue(String column, String value) {
        mConfigValues.put(column, value);
    }

    protected void notifyListener() {
        if (mListener != null) {
            mListener.onRequest(EditFieldListener.FIELD_CHANGED);
        }

        boolean isEmpty = isEmpty();
        if (mWasEmpty != isEmpty) {
            if (isEmpty) {
                if (mListener != null) {
                    mListener.onRequest(EditFieldListener.FIELD_TURNED_EMPTY);
                }
                setDeleteButtonVisible(false);
            } else {
                if (mListener != null) {
                    mListener.onRequest(EditFieldListener.FIELD_TURNED_NON_EMPTY);
                }
                setDeleteButtonVisible(true);
            }
            mWasEmpty = isEmpty;
        }
    }

    public boolean isEmpty() {
        if (!TextUtils.isEmpty(mTypeEditText.getText())) {
            return false;
        }
        if (!TextUtils.isEmpty(mNumberEditText.getText())) {
            return false;
        }
        return true;
    }

    public void clear() {
        mTypeEditText.setText("");
        mNumberEditText.setText("");
    }

    public void delete() {
        mConfigValues.markDeleted();

        ConfigEditorAnimator.getInstance().removeEditorView(this);
    }
}
