package cn.mctlab.archetype.war.data;

import cn.mctlab.archetype.war.json.IJsonable;
import cn.mctlab.archetype.war.json.JsonMapper;

public class BaseData implements IJsonable {

    @Override
    public String writeJson() {
        return JsonMapper.toJson(this);
    }
}
