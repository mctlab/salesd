package com.mctlab.ansight.common.data;

import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.json.JsonMapper;

public class BaseData implements IJsonable {

    @Override
    public String writeJson() {
        return JsonMapper.toJson(this);
    }
}
