package com.ic.er;

import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import com.ic.er.dao.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
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
    private static Map<Long, View> allViewsMap = new HashMap<>();

    public static void connectDB() throws IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        entityMapper = sqlSession.getMapper(EntityMapper.class);
        relationshipMapper = sqlSession.getMapper(RelationshipMapper.class);
        viewMapper = sqlSession.getMapper(ViewMapper.class);
        layoutInfoMapper = sqlSession.getMapper(LayoutInfoMapper.class);
        BasicConfigurator.configure();
        useDB = true;
    }

    public static void createTables() throws Exception {
        Connection conn = sqlSession.getConnection();
        Statement stmt = conn.createStatement();
        String content = Files.readString(Path.of("src/main/resources/sql/schema-v1.sql"), Charset.defaultCharset());
        stmt.execute(content);
    }

    public static View createView(String name, String creator) {
        View view = new View(0L, name, new ArrayList<>(), new ArrayList<>(), creator, new Date(), new Date());
        allViewsMap.put(view.getID(), view);
        return view;
    }


    public static void deleteView(View view) {
        view.deleteDB();
        allViewsMap.remove(view.getID());
    }

    public static List<View> queryAllView() {
        if (ER.useDB) {
            return View.queryAll();
        } else {
            return new ArrayList<>(allViewsMap.values());
        }
    }
}
