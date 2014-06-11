package com.mctlab.salesd.customer;

import java.util.ArrayList;
import java.util.HashMap;

import com.mctlab.salesd.R;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactTplSelectorDialogFragment extends DialogFragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    public interface OnContactTemplateSelectedListener {
        public void OnContactTemplateSelected(String tplName);
    }

    private static final String TPL_NAME = "tpl_name";

    private String[] mFrom = new String[] { TPL_NAME };
    private int[] mTo = new int[] { R.id.tpl_name };

    protected ArrayList<HashMap<String, Object>> mData;

    private OnContactTemplateSelectedListener mOnContactTemplateSelectedListener;

    public static void actionSelectContactTemplate(FragmentManager fragmentManager,
            OnContactTemplateSelectedListener listener) {
        ContactTplSelectorDialogFragment dialog = new ContactTplSelectorDialogFragment();
        dialog.setOnContactTemplateSelectedListener(listener);
        dialog.show(fragmentManager, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setTitle(R.string.dlg_title_select_contact_template);

        View view = inflater.inflate(R.layout.contact_tpl_selector_dialog_fragment, container);

        view.findViewById(R.id.confirm).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);

        mData = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();
        item.put(TPL_NAME, "contacts_template_1.xml");
        mData.add(item);

        item = new HashMap<String, Object>();
        item.put(TPL_NAME, "contacts_template_2.xml");
        mData.add(item);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData,
                R.layout.contact_tpl_item, mFrom, mTo);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (mOnContactTemplateSelectedListener != null) {
            String tplName = (String) mData.get(position).get(TPL_NAME);
            mOnContactTemplateSelectedListener.OnContactTemplateSelected(tplName);
        }
        dismiss();
    }

    public void setOnContactTemplateSelectedListener(OnContactTemplateSelectedListener listener) {
        mOnContactTemplateSelectedListener = listener;
    }

}
