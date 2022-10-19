package com.ic.er;

import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import com.ic.er.dao.AttributeMapper;
import com.ic.er.dao.EntityMapper;
import com.ic.er.dao.RelationshipMapper;
import com.ic.er.dao.ViewMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ER {
    public static SqlSession sqlSession;
    public static boolean useDB = false;
    public static AttributeMapper attributeMapper;
    public static EntityMapper entityMapper;
    public static RelationshipMapper relationshipMapper;
    public static ViewMapper viewMapper;

    public static ResultState connectDB(){
        ResultState resultState = null;
        try {
            InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
            sqlSession = sqlSessionFactory.openSession(true);
            resultState = ResultState.ok();
            attributeMapper = sqlSession.getMapper(AttributeMapper.class);
            entityMapper = sqlSession.getMapper(EntityMapper.class);
            relationshipMapper = sqlSession.getMapper(RelationshipMapper.class);
            viewMapper = sqlSession.getMapper(ViewMapper.class);
            BasicConfigurator.configure();
            useDB = true;
        } catch (IOException msg) {
            resultState = ResultState.build(ResultStateCode.Failure, msg.getMessage());
            return resultState;
        }
        return resultState;
    }

    public static void createTables() throws SQLException {
        Connection conn = sqlSession.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS `attribute`;\n" +
                "CREATE TABLE attribute (\n" +
                "    id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the attribute',\n" +
                "    entity_id bigint NOT NULL COMMENT 'related entity id',\n" +
                "    view_id bigint NOT NULL COMMENT 'related view id',\n" +
                "    name varchar(255) NOT NULL COMMENT 'attribute name',\n" +
                "    data_type varchar(50) NOT NULL COMMENT 'attribute type',\n" +
                "    is_primary tinyint NOT NULL DEFAULT 0 COMMENT '0-not a primary key, 1-primary key, default 0',\n" +
                "    is_foreign tinyint NOT NULL DEFAULT 0 COMMENT '0-not a foreign key, 1-foreign key, default 0',\n" +
                "    is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',\n" +
                "    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',\n" +
                "    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',\n" +
                "    PRIMARY KEY (`id`)\n" +
                ");\n" +
                "\n" +
                "DROP TABLE IF EXISTS `entity`;\n" +
                "CREATE TABLE `entity` (\n" +
                "    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the entity',\n" +
                "    `name` varchar(255) NOT NULL COMMENT 'attribute name',\n" +
                "    `view_id` bigint NOT NULL COMMENT 'related view id',\n" +
                "    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',\n" +
                "    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',\n" +
                "    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',\n" +
                "    PRIMARY KEY (`id`)\n" +
                ");\n" +
                "\n" +
                "DROP TABLE IF EXISTS `relationship`;\n" +
                "CREATE TABLE `relationship` (\n" +
                "    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the relationship between entities',\n" +
                "    `name` varchar(50) NULL COMMENT 'the name of relation',\n" +
                "    `view_id` bigint NOT NULL COMMENT 'related view id',\n" +
                "    `first_entity_id` bigint NOT NULL COMMENT 'the first entity in the relationship',\n" +
                "    `second_entity_id` bigint NOT NULL COMMENT 'the second entity in the relationship',\n" +
                "    `first_attribute_id` bigint NOT NULL DEFAULT 0 COMMENT 'the first attribute in the relationship',\n" +
                "    `second_attribute_id` bigint NOT NULL DEFAULT 0 COMMENT 'the second attribute in the relationship',\n" +
                "    `cardinality` smallint NOT NULL COMMENT '0-one to one, 1-one to many, 2-many to many, 3-many to one',\n" +
                "    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',\n" +
                "    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create times',\n" +
                "    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',\n" +
                "    PRIMARY KEY (`id`)\n" +
                ");\n" +
                "\n" +
                "DROP TABLE IF EXISTS `view`;\n" +
                "CREATE TABLE `view` (\n" +
                "    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the ER model',\n" +
                "    `name` varchar(255) NOT NULL COMMENT 'name of the ER model',\n" +
                "    `creator` varchar(255) NULL DEFAULT NULL COMMENT 'name of the ER model',\n" +
                "    `parent_id` bigint NULL DEFAULT 0 COMMENT 'parent view id',\n" +
                "    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',\n" +
                "    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',\n" +
                "    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',\n" +
                "    PRIMARY KEY (`id`)\n" +
                ");\n" +
                "\n" +
                "DROP TABLE IF EXISTS `graph_info`;\n" +
                "CREATE TABLE `graph_info` (\n" +
                "    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'graph id',\n" +
                "    `related_obj_id` bigint NOT NULL COMMENT 'related object id',\n" +
                "    `layout_x` NUMERIC(8,3) NOT NULL COMMENT 'x position on the view',\n" +
                "    `layout_y` NUMERIC(8,3) NOT NULL COMMENT 'y position on the view',\n" +
                "    `width` NUMERIC(8,3) NOT NULL COMMENT 'the width of object',\n" +
                "    `height` NUMERIC(8,3) NOT NULL COMMENT 'the height of object',\n" +
                "    PRIMARY KEY (`id`)\n" +
                ");");
    }
}
