package com.mctlab.salesd.project;

import java.util.ArrayList;
import java.util.List;

import com.mctlab.salesd.R;
import com.mctlab.salesd.project.ConfigEntity.ConfigValues;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfigEditCategoryView extends LinearLayout
        implements ConfigEditFieldView.EditFieldListener {

    private LayoutInflater mInflater;

    private TextView mHeader;
    private ViewGroup mFieldContainer;
    private View mAddFieldFooter;

    private String mCategory;
    private ConfigEntity mConfigEntity;

    public ConfigEditCategoryView(Context context) {
        super(context);
    }

    public ConfigEditCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mInflater = (LayoutInflater)getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mHeader = (TextView)findViewById(R.id.header);
        mFieldContainer = (ViewGroup)findViewById(R.id.fields);
        mAddFieldFooter = findViewById(R.id.add_field);
        mAddFieldFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddFieldFooter.setVisibility(View.GONE);
                addNewField();
            }
        });
    }

    @Override
    public void onRequest(int request) {
        // If a field has become empty or non-empty, then check if another row
        // can be added dynamically.
        if (request == FIELD_TURNED_EMPTY || request == FIELD_TURNED_NON_EMPTY) {
            updateAddFieldFooterVisible(true);
        }
    }

    @Override
    public void onDeleteRequested(ConfigEditFieldView field) {
        // If there is only 1 editor in the section, then don't allow the user to delete it.
        // Just clear the fields in the editor.
        if (getFieldCount() == 1) {
            field.clear();
        } else {
            field.delete();
        }
    }

    public void setCategory(String category, ConfigEntity entity) {
        mConfigEntity = entity;
        mCategory = category;

        mFieldContainer.removeAllViews();
        mHeader.setText(mCategory);

        if (mConfigEntity.hasCategoryEntries(mCategory)) {
            ArrayList<ConfigValues> entries = mConfigEntity.getCategoryEntries(category);
            for (ConfigValues entry : entries) {
                createEditFieldView(entry);
            }
        }

        updateAddFieldFooterVisible(false);
    }

    public void addNewField() {
        ConfigValues values = mConfigEntity.insertEntry(mCategory);

        final View newField = createEditFieldView(values);
        post(new Runnable() {
            @Override
            public void run() {
                newField.requestFocus();
            }
        });

        mAddFieldFooter.setVisibility(View.GONE);
    }

    private View createEditFieldView(ConfigValues entry) {
        ConfigEditFieldView fieldView = (ConfigEditFieldView)mInflater.inflate(
                R.layout.config_edit_field, this, false);
        fieldView.setListener(this);
        fieldView.setValues(entry);
        mFieldContainer.addView(fieldView);

        return fieldView;
    }

    public int getFieldCount() {
        return mFieldContainer.getChildCount();
    }

    protected void updateAddFieldFooterVisible(boolean animate) {
        if (!hasEmptyEditor() && mConfigEntity.canInsert(mCategory)) {
            if (animate) {
                ConfigEditorAnimator.getInstance().showAddFieldFooter(mAddFieldFooter);
            } else {
                mAddFieldFooter.setVisibility(View.VISIBLE);
            }
        } else {
            if (animate) {
                ConfigEditorAnimator.getInstance().hideAddFieldFooter(mAddFieldFooter);
            } else {
                mAddFieldFooter.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Updates the editors being displayed to the user removing extra empty
     * {@link Editor}s, so there is only max 1 empty {@link Editor} view at a time.
     */
    @SuppressWarnings("unused")
	private void updateEmptyEditors() {
        List<View> emptyEditors = getEmptyEditors();

        // If there is more than 1 empty editor, then remove it from the list of editors.
        if (emptyEditors.size() > 1) {
            for (View emptyEditorView : emptyEditors) {
                // If no child {@link View}s are being focused on within
                // this {@link View}, then remove this empty editor.
                if (emptyEditorView.findFocus() == null) {
                    mFieldContainer.removeView(emptyEditorView);
                }
            }
        }
    }

    /**
     * Returns a list of empty editor views in this section.
     */
    private List<View> getEmptyEditors() {
        List<View> emptyEditorViews = new ArrayList<View>();
        for (int i = 0; i < mFieldContainer.getChildCount(); i++) {
            View view = mFieldContainer.getChildAt(i);
            if (((ConfigEditFieldView) view).isEmpty()) {
                emptyEditorViews.add(view);
            }
        }
        return emptyEditorViews;
    }

    /**
     * Returns true if one of the editors has all of its fields empty, or false
     * otherwise.
     */
    private boolean hasEmptyEditor() {
        return getEmptyEditors().size() > 0;
    }
}
