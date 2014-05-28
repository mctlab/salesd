/**
 * @(#)ProjectMapper.java, 5/28/14.
 *
 * Copyright 2014 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.mctlab.archetype.war.storage;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.mapping.StatementType;

import cn.mctlab.archetype.war.data.Project;

/**
 * @author liqiang
 */
public interface ProjectMapper {

    @Select("CREATE TABLE IF NOT EXISTS project("
            + "serverId BIGINT NOT NULL AUTO_INCREMENT, "
            + "name TEXT NOT NULL, "
            + "PRIMARY KEY (serverId))"
    )
    void createTable();

    @Insert("INSERT INTO project(name) VALUES(#{PROJECT.name})")
    @SelectKey(before = false, resultType = Long.class, keyProperty = "PROJECT.serverId",
            statementType = StatementType.STATEMENT, statement = "SELECT LAST_INSERT_ID() AS serverId")
    long insert(final @Param("PROJECT") Project project);
}
