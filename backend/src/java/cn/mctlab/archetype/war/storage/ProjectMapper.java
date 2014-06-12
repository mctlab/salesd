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
import org.apache.ibatis.annotations.Update;

import cn.mctlab.archetype.war.data.Project;

/**
 * @author liqiang
 */
public interface ProjectMapper {

    public static final String TABLE_NAME = "projects";

    @Select("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + "serverId BIGINT NOT NULL AUTO_INCREMENT, "
            + "name TEXT NOT NULL, "
            + "priority INT, "
            + "estimatedAmount INT, "
            + "status INT, "
            + "description TEXT, "
            + "ownerId INT, "
            + "version BIGINT, "
            + "PRIMARY KEY (serverId))"
    )
    void createTable();

    @Insert("INSERT INTO " + TABLE_NAME + "("
            + "name, "
            + "priority, "
            + "estimatedAmount, "
            + "status, "
            + "description, "
            + "ownerId, "
            + "version"
            + ") VALUES("
            + "#{PROJECT.name}, "
            + "#{PROJECT.priority}, "
            + "#{PROJECT.estimatedAmount}, "
            + "#{PROJECT.status}, "
            + "#{PROJECT.description}, "
            + "#{PROJECT.ownerId}, "
            + "#{PROJECT.version}"
            + ")")
    @Options(useGeneratedKeys = true, keyProperty = "PROJECT.serverId", keyColumn = "serverId")
    int insert(final @Param("PROJECT") Project project);

    @Update("UPDATE " + TABLE_NAME + " set "
            + "name = #{PROJECT.name}, "
            + "priority = #{PROJECT.priority}, "
            + "estimatedAmount = #{PROJECT.estimatedAmount}, "
            + "status = #{PROJECT.status}, "
            + "description = #{PROJECT.description}, "
            + "version = #{PROJECT.version}"
            + " WHERE serverId = #{PROJECT.serverId}")
    void update(final @Param("PROJECT") Project project);
}
