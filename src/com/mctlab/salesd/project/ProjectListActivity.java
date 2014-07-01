package com.mctlab.salesd.project;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mctlab.salesd.R;
import com.mctlab.salesd.activity.WaitingDialogFragment;
import com.mctlab.salesd.api.SyncApi;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.data.Project;
import com.mctlab.salesd.data.SyncData;
import com.mctlab.salesd.data.SyncProject;
import com.mctlab.salesd.util.LogUtil;

import java.util.List;

public class ProjectListActivity extends Activity {

    class SyncCallbackListener extends SyncApi.CallbackListener {

        @Override
        public void onApiStart(int token) {
            super.onApiStart(token);
            WaitingDialogFragment.actionShowProgress(getFragmentManager(),
                    getString(R.string.dlg_message_syncing_project));
        }

        @Override
        public void onApiSuccess(int token, List<SyncData> syncDatas) {
            super.onApiSuccess(token, syncDatas);
            long version = 0L;
            for (SyncData syncData : syncDatas) {
                if (syncData instanceof SyncProject) {
                    Project project = ((SyncProject) syncData).getData();
                    if (project.getVersion() > version) {
                        version = project.getVersion();
                    }
                    if (SalesDConstant.OP_DELETE.equals(syncData.getOperation())) {
                        LogUtil.d("Delete " + project.getName());
                    } else if (SalesDConstant.OP_INSERT.equals(syncData.getOperation())) {
                        LogUtil.d("Insert " + project.getName());
                    } else if (SalesDConstant.OP_UPDATE.equals(syncData.getOperation())) {
                        LogUtil.d("Update " + project.getName());
                    }
                }
            }
            LogUtil.d("Table project version: " + version);
        }

        @Override
        public void onApiFinish(int token) {
            super.onApiFinish(token);
            WaitingDialogFragment.actionDismissProgress();
        }

    }

    SyncCallbackListener mSyncCallbackListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.project_list_activity);

        mSyncCallbackListener = new SyncCallbackListener();

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.projects_container, new ProjectListFragment());
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SyncApi.syncProjects(0, mSyncCallbackListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_list_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opt_add) {
            Intent intent = new Intent(SalesDConstant.ACTION_PROJECT_EDIT);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
