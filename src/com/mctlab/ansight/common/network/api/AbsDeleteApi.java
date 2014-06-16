package com.mctlab.ansight.common.network.api;

import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.network.http.HttpDeleteTask;

public abstract class AbsDeleteApi<FORM extends IForm, RESULT> extends AbstractApi<FORM, RESULT> {

    protected AbsDeleteApi(String baseUrl, FORM form) {
        super(baseUrl, form);
    }

    @Override
    protected HttpDeleteTask<RESULT> onCreateTask() {
        return newHttpDeleteTask();
    }
}