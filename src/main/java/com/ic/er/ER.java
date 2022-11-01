package com.ic.er;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ic.er.exception.ERException;
import com.ic.er.dao.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ER {
    public static SqlSession sqlSession;
    public static boolean useDB = false;
    public static AttributeMapper attributeMapper;
    public static EntityMapper entityMapper;
    public static RelationshipMapper relationshipMapper;
    public static ViewMapper viewMapper;
    public static LayoutInfoMapper layoutInfoMapper;

    public static void initialize(boolean useDBLog) throws SQLException, IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        entityMapper = sqlSession.getMapper(EntityMapper.class);
        relationshipMapper = sqlSession.getMapper(RelationshipMapper.class);
        viewMapper = sqlSession.getMapper(ViewMapper.class);
        layoutInfoMapper = sqlSession.getMapper(LayoutInfoMapper.class);
        createTables();
        useDB = true;
    }

    private static void createTables() throws SQLException, IOException {
        Connection conn = sqlSession.getConnection();
        Statement stmt = conn.createStatement();
        String sql = new String(Resources.getResourceAsStream("mysql.sql").readAllBytes(), StandardCharsets.UTF_8);
        stmt.execute(sql);
    }

    public static View createView(String name, String creator) {
        return new View(0L, name, new ArrayList<>(), new ArrayList<>(), creator, new Date(), new Date());
    }


    public static void deleteView(View view) {
        view.deleteDB();
    }

    public static List<View> queryAllView() {
        return View.queryAll();
    }

    public static View queryViewByID(Long ID) {
        return View.queryByID(ID);
    }

    public static View loadFromJSON(String json) throws ERException {
        try {
            return new ObjectMapper().readValue(json, View.class);
        } catch (JsonProcessingException e) {
            throw new ERException(String.format("loadFromJSON fail, error: %s", e.getMessage()));
        }
    }
}
