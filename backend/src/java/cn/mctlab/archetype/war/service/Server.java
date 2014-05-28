/**
 * Created by IntelliJ IDEA
 * User: Uraka.Lee
 * Date: 2013-07-01
 *
 * Copyleft 2000 MCTLab.cn
 */
package cn.mctlab.archetype.war.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.mctlab.archetype.war.data.Project;
import cn.mctlab.archetype.war.storage.ProjectMapper;

/**
 *
 */
@Service
public class Server implements IServer, InitializingBean {

    //-- public finals --//

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    //-- private finals --//
    //-- properties --//

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private IStorage storage;

    //-- constructors --//

    @Override
    public void afterPropertiesSet() throws Exception {
        projectMapper.createTable();
        Project project = new Project();
        project.setName("xxxx");
        LOG.debug("serverId: {}", projectMapper.insert(project));
    }

    //-- destructors --//
    //-- implements --//

    @Override
    public String response() {
        return "Server:" + storage.response();
    }

    //-- un-implements --//
    //-- methods --//
    //-- functions --//
    //-- utils --//
    //-- getters & setters --//
    //-- inner classes --//
}
