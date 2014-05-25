package com.mctlab.salesd.customer;

import java.util.List;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ContactListActivity extends Activity
        implements CustomerPickerDialogFragment.OnPickCustomersListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.contact_list_activity);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contacts_container, new ContactListFragment());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_list_options, menu);

        MenuItem item = menu.findItem(R.id.opt_add);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opt_add) {
            CustomerPickerDialogFragment.actionCreateNewContact(getFragmentManager(), this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnPickCustomers(List<Long> ids) {
        if (ids != null && ids.size() > 0) {
            Long id = ids.get(0);
            Intent intent = new Intent(SalesDConstant.ACTION_CONTACT_EDIT);
            intent.putExtra(SalesDConstant.EXTRA_CUSTOMER_ID, id);
            startActivity(intent);
        }
    }

}
