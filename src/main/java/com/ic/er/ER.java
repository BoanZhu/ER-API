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

import java.io.IOException;
import java.io.InputStream;

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
            useDB = true;
        } catch (IOException msg) {
            resultState = ResultState.build(ResultStateCode.FAILRESULTCODE, msg.getMessage());
            return resultState;
        }
        return resultState;
    }
}
