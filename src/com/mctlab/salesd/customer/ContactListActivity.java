package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ContactListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_list_activity);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contacts_container, new ContactListFragment());
        transaction.commit();
    }

}
