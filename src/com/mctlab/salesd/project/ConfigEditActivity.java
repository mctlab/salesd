package com.mctlab.salesd.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.util.LogUtil;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

public class ConfigEditActivity extends Activity
        implements ProjectQueryHandler.OnQueryCompleteListener {

    private LayoutInflater mInflater;
    private ViewGroup mCategoryContainer;

    private String[] mCategories;
    private CategoryComparator mCategoryComparator;

    protected ProjectQueryHandler mQueryHandler;
    private ConfigEntity mConfigEntity;

    private long mProjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.config_edit_activity);

        if (getIntent() != null) {
            mProjectId = getIntent().getLongExtra(SalesDConstant.EXTRA_PROJECT_ID, -1);
        }

        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mCategoryContainer = (ViewGroup)findViewById(R.id.categories);

        mCategories = getResources().getStringArray(R.array.config_category_values);
        mCategoryComparator = new CategoryComparator();

        mQueryHandler = new ProjectQueryHandler(getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);

        loadConfig(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQueryHandler.startQueryConfigs(0, mProjectId);
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                loadConfig(cursor);
            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.config_edit_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.opt_done) {
            saveConfig();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindEditor() {
        mCategoryContainer.removeAllViews();

        ArrayList<String> categories = mConfigEntity.getCategories();
        Collections.sort(categories, mCategoryComparator);
        for (String category : categories) {
            ConfigEditCategoryView categoryView = (ConfigEditCategoryView)mInflater.inflate(
                    R.layout.config_edit_category, mCategoryContainer, false);
            categoryView.setCategory(category, mConfigEntity);
            mCategoryContainer.addView(categoryView);
        }
    }

    private void loadConfig(Cursor cursor) {
        mConfigEntity = new ConfigEntity(mProjectId);
        mConfigEntity.fromCursor(cursor);

        for (String category : mCategories) {
            mConfigEntity.ensureCategoryExists(category);
        }

        bindEditor();
    }

    private void saveConfig() {
        final ContentResolver resolver = getContentResolver();

        mConfigEntity.trimEmpty();
        final ArrayList<ContentProviderOperation> diff = mConfigEntity.buildDiff();
//        ContentProviderResult[] results = null;
        LogUtil.d("Operation size: " + diff.size());
        if (!diff.isEmpty()) {
            try {
                /*results =*/resolver.applyBatch(TasksProvider.AUTHORITY, diff);
            } catch (RemoteException e) {
                LogUtil.w("RException while submitting changes of config: " + e.getMessage());
            } catch (OperationApplicationException e) {
                LogUtil.w("OAException while submitting changes of config: " + e.getMessage());
            }
        }

        finish();
    }

    class CategoryComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            int length = mCategories.length;
            int lidx = length;
            int ridx = length;
            for (int i = 0; i < length; i++) {
                if (mCategories[i].equals(lhs)) {
                    lidx = i;
                }
                if (mCategories[i].equals(rhs)) {
                    ridx = i;
                }
            }
            return lidx - ridx;
        }
    }

}
