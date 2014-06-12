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

import java.util.List;

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
    }

    //-- destructors --//
    //-- implements --//

    @Override
    public String response() {
        return "Server:" + storage.response();
    }

    @Override
    public long insertProject(Project project) {
        project.setVersion(System.currentTimeMillis());
        LOG.debug("count: {}", projectMapper.insert(project));
        return project.getServerId();
    }

    @Override
    public boolean updateProject(Project project) {
        project.setVersion(System.currentTimeMillis());
        try {
            projectMapper.update(project);
            return true;
        } catch (Exception e) {
            LOG.error("deleteProject failed: ", e);
            return false;
        }
    }

    @Override
    public boolean deleteProject(long serverId) {
        Project mock = Project.newDeleteProject(serverId);
        try {
//            projectMapper.delete(mock);
            return true;
        } catch (Exception e) {
            LOG.error("deleteProject failed: ", e);
            return false;
        }
    }

    @Override
    public List<Project> sync(long version) {
        return projectMapper.sync(version);
    }

    //-- un-implements --//
    //-- methods --//
    //-- functions --//
    //-- utils --//
    //-- getters & setters --//
    //-- inner classes --//
}
