/**
 * @(#)ProjectMapper.java, 5/28/14.
 *
 * Copyright 2014 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.mctlab.archetype.war.storage;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.mctlab.archetype.war.data.Project;

/**
 * @author liqiang
 */
public interface ProjectMapper {

    @Select("CREATE TABLE IF NOT EXISTS project("
            + "serverId BIGINT NOT NULL AUTO_INCREMENT, "
            + "name TEXT NOT NULL, "
            + "priority INT, "
            + "estimatedAmount INT, "
            + "status INT, "
            + "description TEXT, "
            + "ownerId INT, "
            + "PRIMARY KEY (serverId))"
    )
    void createTable();

    @Insert("INSERT INTO project("
            + "name, "
            + "priority, "
            + "estimatedAmount, "
            + "status, "
            + "description, "
            + "ownerId"
            + ") VALUES("
            + "#{PROJECT.name}, "
            + "#{PROJECT.priority}, "
            + "#{PROJECT.estimatedAmount}, "
            + "#{PROJECT.status}, "
            + "#{PROJECT.description}, "
            + "#{PROJECT.ownerId}"
            + ")")
    @Options(useGeneratedKeys = true, keyProperty = "PROJECT.serverId", keyColumn = "serverId")
    int insert(final @Param("PROJECT") Project project);
}
