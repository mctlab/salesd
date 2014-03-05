package com.mctlab.salesd.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mctlab.ansight.common.annotation.ViewId;
import com.mctlab.ansight.common.util.L;
import com.mctlab.ansight.common.util.UIUtils;
import com.mctlab.salesd.R;
import com.mctlab.salesd.activity.base.BaseActivity;
import com.mctlab.salesd.api.GetClassAApi;
import com.mctlab.salesd.api.GetClassBsApi;
import com.mctlab.salesd.data.ClassA;
import com.mctlab.salesd.data.ClassB;

import java.util.List;

public class WelcomeActivity extends BaseActivity {

    @ViewId(R.id.view_info)
    private TextView infoView;
    @ViewId(R.id.btn_info)
    private Button infoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetClassAApi(9527) {
                    @Override
                    protected void onSuccess(ClassA classA) {
                        super.onSuccess(classA);
                        String jsonA = classA.writeJson();
                        L.i(getActivity(), jsonA);
                    }
                }.call(getActivity()); // FOREGROUND
                new GetClassBsApi() {
                    @Override
                    protected void onSuccess(List<ClassB> classBs) {
                        super.onSuccess(classBs);
                        for (ClassB b : classBs) {
                            String jsonB = b.writeJson();
                            L.i("BACKGROUND", jsonB);
                        }
                    }
                }.call(null); // BACKGROUND
                UIUtils.toast(getActivity(), "click");
                infoBtn.setText("请看 log!");
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

}
