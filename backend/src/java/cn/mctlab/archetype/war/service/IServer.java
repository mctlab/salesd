/**
 * Created by IntelliJ IDEA
 * User: Uraka.Lee
 * Date: 2013-07-01
 *
 * Copyleft 2000 MCTLab.cn
 */
package cn.mctlab.archetype.war.service;

import cn.mctlab.archetype.war.data.Project;

/**
 *
 */
public interface IServer {

    String response();

    long insertProject(Project project);

    boolean updateProject(Project project);

    boolean deleteProject(long serverId);
}
