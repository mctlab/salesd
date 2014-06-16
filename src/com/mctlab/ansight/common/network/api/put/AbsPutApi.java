package com.mctlab.ansight.common.network.api.put;

import com.mctlab.ansight.common.network.api.AbstractApi;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.network.http.HttpPutTask;

public abstract class AbsPutApi<FORM extends IForm, RESULT> extends AbstractApi<FORM, RESULT> {

    protected AbsPutApi(String baseUrl, FORM form) {
        super(baseUrl, form);
    }

    @Override
    protected HttpPutTask<RESULT> onCreateTask() {
        return newHttpPutTask();
    }
}
