package com.ic.er.mapper;

import com.ic.er.entity.RelationshipDO;
import com.ic.er.common.Cardinality;
import com.ic.er.dao.RelationshipMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class testRelationMapper {
    public static SqlSession sqlSession;
    public static Connection connection;
    public static RelationshipMapper relationMapper;

    @Before
    public void init() throws IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        connection = sqlSession.getConnection();
        System.out.println(connection);
        relationMapper = sqlSession.getMapper(RelationshipMapper.class);
    }

    @Test
    public void testQueryRelation() {
        Assert.assertNotNull(sqlSession);
        RelationshipDO relationshipDO = relationMapper.selectByID(Long.valueOf(3));
        System.out.println(relationshipDO);
    }

    @Test
    public void testQueryRelationByRelation(){
        Assert.assertNotNull(sqlSession);
        RelationshipDO relationshipDO = new RelationshipDO(null,"relation4",
                null,null,null,
                Cardinality.OneToMany,0,null,null);
        List<RelationshipDO> res = relationMapper.selectByRelationship(relationshipDO);
        System.out.println(res);
    }

    @Test
    public void testCreateRelation(){
        Assert.assertNotNull(sqlSession);
        RelationshipDO relationshipDO = new RelationshipDO(Long.valueOf(11),"relation4",
                Long.valueOf(4),Long.valueOf(4),Long.valueOf(3),
                Cardinality.OneToMany,0,new Date(),new Date());
        Assert.assertEquals(relationMapper.insert(relationshipDO),1);
    }

    @Test
    public void testDeleteRelation(){
        Assert.assertNotNull(sqlSession);
        Assert.assertEquals(relationMapper.deleteByID(Long.valueOf(11)),1);
    }

    @Test
    public void testUpdateRelation(){
        Assert.assertNotNull(sqlSession);
        RelationshipDO relationshipDO = new RelationshipDO(Long.valueOf(4),"relation4update",
                Long.valueOf(3),Long.valueOf(4),Long.valueOf(3),
                Cardinality.OneToMany,0,new Date(),new Date());
        Assert.assertEquals(relationMapper.updateByID(relationshipDO),1);
    }
}
