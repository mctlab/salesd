package com.mctlab.salesd.customer;

import com.mctlab.salesd.R;
import com.mctlab.salesd.constant.SalesDConstant;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ContactDetailActivity extends Activity
        implements CustomerQueryHandler.OnQueryCompleteListener {

    private long mId = -1;

    private CustomerQueryHandler mQueryHandler;

    private TextView mPhoneNumber;
    private TextView mEmail;
    private TextView mOfficeLocation;
    private TextView mTitle;
    private TextView mLeader;
    private TextView mCharacter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_activity);

        if (getIntent() != null) {
            mId = getIntent().getLongExtra(SalesDConstant.EXTRA_ID, -1);
        }

        Bundle args = new Bundle();
        args.putLong(ContactListFragment.ARG_LEADER_ID, mId);
        args.putString(ContactListFragment.ARG_HEADER_LABEL, getString(R.string.followers));
        args.putString(ContactListFragment.ARG_EMPTY_HINT, getString(R.string.no_follower));
        ContactListFragment fragment = new ContactListFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.contacts_container, fragment);
        transaction.commit();

        mQueryHandler = new CustomerQueryHandler(getContentResolver());
        mQueryHandler.setOnQueryCompleteListener(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQueryHandler.startQueryContact(0, mId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_detail_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.opt_edit:
            Intent intent = new Intent(SalesDConstant.ACTION_CONTACT_EDIT);
            intent.putExtra(SalesDConstant.EXTRA_ID, mId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQueryComplete(int token, Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                updateContactInfo(cursor);
            }
            cursor.close();
        }
    }

    private void initView() {
        mPhoneNumber = (TextView) findViewById(R.id.phone_number);
        mEmail = (TextView) findViewById(R.id.email);
        mOfficeLocation = (TextView) findViewById(R.id.office_location);
        mTitle = (TextView) findViewById(R.id.title);
        mLeader = (TextView) findViewById(R.id.leader);
        mCharacter = (TextView) findViewById(R.id.character);
    }

    private void updateContactInfo(Cursor cursor) {
        String name = mQueryHandler.getContactName(cursor);
        setTitle(name);

        String phoneNumber = mQueryHandler.getContactPhoneNumber(cursor);
        mPhoneNumber.setText(phoneNumber);
        String email = mQueryHandler.getContactEmail(cursor);
        mEmail.setText(email);
        String officeLocation = mQueryHandler.getContactOfficeLocation(cursor);
        mOfficeLocation.setText(officeLocation);
        String title = mQueryHandler.getContactTitle(cursor);
        mTitle.setText(title);
        String leader = mQueryHandler.getContactDirectLeader(cursor);
        mLeader.setText(leader);
        String character = mQueryHandler.getContactCharacters(cursor);
        mCharacter.setText(character);
    }
}
