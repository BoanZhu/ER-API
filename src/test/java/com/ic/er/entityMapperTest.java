package com.ic.er;

import com.ic.er.bean.entity.EntityDO;
import com.ic.er.dao.EntityMapper;
import com.ic.er.dao.ViewMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import java.time.LocalDateTime;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

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

        long id = 12334;
        String name = "b";
        long viewId = 789;
        Date create = new Date();
        Date modify = new Date();
        int isDelete = 0;

        EntityDO entity = new EntityDO(id,name,viewId,isDelete,create,modify);
        EntityMapper entityMapper = sqlSession.getMapper(EntityMapper.class);


        Assert.assertNotNull(sqlSession);
        entityMapper.insert(entity);
        sqlSession.close();
    }

}
