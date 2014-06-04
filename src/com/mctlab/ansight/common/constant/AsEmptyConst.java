package com.mctlab.ansight.common.constant;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.HttpStatusException;
import com.mctlab.ansight.common.network.api.callback.ApiCallback;
import com.mctlab.ansight.common.network.form.BaseForm;

public interface AsEmptyConst {

    public static final String EMPTY_STRING = "";

    public static final String EMPTY_JSON_STRING = "{}";

    public static final int[] EMPTY_INT_ARRAY = new int[0];

    public static final class EMPTY_FORM extends BaseForm {}

    public static final EMPTY_FORM EMPTY_FORM_INSTANCE = new EMPTY_FORM();

    public abstract class ApiEmptyCallback<Result> implements ApiCallback<Result> {

        @Override
        public void onStart() {}

        @Override
        public void onFinish() {}

        @Override
        public void onSuccess(Result result) {}

        @Override
        public void onFailed(ApiException exception) {}

        @Override
        public boolean onHttpStatusException(HttpStatusException exception) {
            return false;
        }

        @Override
        public void afterDecode(Result result) {}
    }
}
