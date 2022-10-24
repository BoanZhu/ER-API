package com.ic.er.mapper;

import com.ic.er.entity.EntityDO;
import com.ic.er.dao.EntityMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

/**
 *
 * @author wendi
 * @data 15/10/2022
 *
 */
public class entityMapperTest {
    public static SqlSession sqlSession;
    public static Connection connection;
    public static EntityMapper entityMapper;

    @Before
    public void init() throws IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        connection = sqlSession.getConnection();
        System.out.println(connection);
    }

    @Test
    public void insertEntity() {

        long id = Long.valueOf(456);
        String name = "b";
        long viewId = Long.valueOf(2234);
        Date create = new Date();
        Date modify = new Date();
        int isDelete = 0;

        EntityDO entity = new EntityDO(id,name,viewId,isDelete,create,modify);

        Assert.assertNotNull(sqlSession);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);
        entityMapper.insert(entity);
    }
    @Test
    public void insertEntityTest() {

        long id = 12334;
        String name = "b";
        long viewId = 789;
        Date create = new Date();
        Date modify = new Date();
        int isDelete = 0;

        EntityDO entity = new EntityDO(id,name,viewId,isDelete,create,modify);

        Assert.assertNotNull(sqlSession);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);
        entityMapper.insert(entity);
    }

    @Test
    public void selectByEntityTest() {

        long id = Long.valueOf(12334);
        String name = null;
        long viewId = Long.valueOf(789);
        Date create = null;
        Date modify = null;
        int isDelete = 0;

        EntityDO entity = new EntityDO(id,name,viewId,isDelete,create,modify);



        Assert.assertNotNull(sqlSession);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);
        entityMapper.selectByEntity(entity);
    }

    @Test
    public void selectByIdTest() {
        Long id = Long.valueOf(123);

        Assert.assertNotNull(sqlSession);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);
        entityMapper.selectByID(id);
    }


    @Test
    public void updateByIdTest() {

        long id = Long.valueOf(123);
        String name = "b";
        long viewId = Long.valueOf(456);
        Date create = new Date();
        Date modify = new Date();
        int isDelete = 0;

        EntityDO entityDo = new EntityDO(id,name,viewId,isDelete,create,modify);

        Assert.assertNotNull(sqlSession);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);
        entityMapper.updateByID(entityDo);
    }
    @Test
    public void deleteByIdTest() {

        long id = Long.valueOf(123);

        Assert.assertNotNull(sqlSession);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);
        entityMapper.deleteByID(id);
    }





}
