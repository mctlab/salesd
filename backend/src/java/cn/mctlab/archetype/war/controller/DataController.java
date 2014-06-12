package cn.mctlab.archetype.war.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.mctlab.archetype.war.constant.DbConsts;
import cn.mctlab.archetype.war.data.Project;
import cn.mctlab.archetype.war.json.JsonMapper;
import cn.mctlab.archetype.war.service.IServer;
import cn.mctlab.archetype.war.storage.ProjectMapper;

/**
 * Created by liqiang on 6/4/14.
 */
@Controller
@RequestMapping("/data")
public class DataController {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    private static final String KEY_TABLE = "table";
    private static final String KEY_OPERATION = "operation";
    private static final String KEY_DATA = "data";
    private static final String KEY_SERVER_ID = "serverId";
    private static final String KEY_INFO = "info";

    @Autowired
    private IServer server;

    @RequestMapping("/request")
    @ResponseBody
    public String request(final @RequestBody String body) throws Exception {
        LOG.debug(body);
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(body).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject json = jsonArray.get(i).getAsJsonObject();
            String table = json.get(KEY_TABLE).getAsString();
            String operation = json.get(KEY_OPERATION).getAsString();
            if (ProjectMapper.TABLE_NAME.equals(table)) {
                if (DbConsts.OP_DELETE.equals(operation)) {
                    long serverId = json.get(KEY_SERVER_ID).getAsLong();
                    LOG.debug("{} {} status: {}", table, operation, server.deleteProject(serverId));
                } else {
                    Project project = JsonMapper.getSerializer().fromJson(json.get(KEY_DATA), Project.class);
                    LOG.debug("{}: {}", table, project.writeJson());
                    if (DbConsts.OP_INSERT.equals(operation)) {
                        LOG.debug("{} {} serverId: {}", table, operation, server.insertProject(project));
                    } else if (DbConsts.OP_UPDATE.equals(operation)) {
                        project.setVersion(System.currentTimeMillis());
                        LOG.debug("{} {} status: {}", table, operation, server.updateProject(project));
                    }
                }
            }
        }
        return null;
    }
}
