package com.mctlab.salesd.project;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mctlab.salesd.R;
import com.mctlab.salesd.api.SyncApi;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.data.Project;
import com.mctlab.salesd.data.Sync;
import com.mctlab.salesd.data.SyncData;
import com.mctlab.salesd.data.SyncProject;
import com.mctlab.salesd.provider.TasksDatabaseHelper.Tables;

import java.util.LinkedList;
import java.util.List;

public class ProjectListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.project_list_activity);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.projects_container, new ProjectListFragment());
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Sync> syncs = new LinkedList<Sync>();
        final Sync syncProject = new Sync(Tables.PROJECTS, 0);
        syncs.add(syncProject);
        new SyncApi(syncs.toArray(new Sync[syncs.size()])) {
            @Override
            protected void onSuccess(List<SyncData> syncDatas) {
                super.onSuccess(syncDatas);
                for (SyncData syncData : syncDatas) {
                    if (syncData instanceof SyncProject) {
                        Project project = ((SyncProject) syncData).getData();
                        if (SalesDConstant.OP_DELETE.equals(syncData.getOperation())) {

                        } else if (SalesDConstant.OP_INSERT.equals(syncData.getOperation())) {

                        } else if (SalesDConstant.OP_UPDATE.equals(syncData.getOperation())) {

                        }
                    }
                }
            }
        }.call(null);
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
