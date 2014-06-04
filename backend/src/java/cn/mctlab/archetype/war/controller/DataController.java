package cn.mctlab.archetype.war.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.mctlab.archetype.war.data.Project;
import cn.mctlab.archetype.war.json.JsonMapper;
import cn.mctlab.archetype.war.service.IServer;

/**
 * Created by liqiang on 6/4/14.
 */
@Controller
@RequestMapping("/data")
public class DataController {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private IServer server;

    @RequestMapping("/request")
    @ResponseBody
    public String request(final @RequestBody String body) throws Exception {
        Project project = JsonMapper.fromJson(body, Project.class);
        LOG.debug(project.writeJson());
        LOG.debug("serverId: {}", server.insertProject(project));
        return null;
    }
}
