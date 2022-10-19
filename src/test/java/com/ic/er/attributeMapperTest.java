package com.ic.er;

import com.ic.er.bean.entity.AttributeDO;
import com.ic.er.bean.entity.EntityDO;
import com.ic.er.common.DataType;
import com.ic.er.dao.AttributeMapper;
import com.ic.er.dao.EntityMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;


/**
 *
 * @author jie
 * @data 18/10/2022
 *

 *
 */
public class attributeMapperTest {
    public static SqlSession sqlSession;
    public static Connection connection;
    public static AttributeMapper attributeMapper;

    @Before
    public void init() throws IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        connection = sqlSession.getConnection();
        System.out.println(connection);
        try {
            ER.connectDB();
            ER.createTables();
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void insertAttribute() {

        Long id = 123L;

        Long entityId = Long.valueOf(456);

        Long viewId = Long.valueOf(789);

        String name = "a";

        String dataType = "int";

        int isPrimary = 0;

        int isForeign = 1;

        int isDelete = 0;

        Date gmtCreate = new Date();

        Date gmtModified = new Date();


        AttributeDO attributeDO = new AttributeDO(id, entityId, viewId,
                name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);

        Assert.assertNotNull(sqlSession);
        AttributeMapper attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        int ret = attributeMapper.insert(attributeDO);
        attributeDO.setId(555L);
        int ret2 = attributeMapper.insert(attributeDO);
        System.out.printf("ret: %d\n", ret);
        System.out.printf("ret2: %d\n", ret2);
    }
    @Test
    public void selectByAttributeTest() {

        Long id = Long.valueOf(123);

        Long entityId = Long.valueOf(456);

        Long viewId = Long.valueOf(789);

        String name = "a";

        String dataType = "int";

        int isPrimary = 0;

        int isForeign = 1;

        int isDelete = 0;

        Date gmtCreate = null;

        Date gmtModified = null;


        AttributeDO attributeDO = new AttributeDO(id, entityId, viewId,
                name, DataType.VARCHAR, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);



        Assert.assertNotNull(sqlSession);
        AttributeMapper attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        attributeMapper.selectByAttribute(attributeDO);
    }
    @Test
    public void selectByIdTest(){
        Long id = Long.valueOf(123);

        Assert.assertNotNull(sqlSession);
        AttributeMapper attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        attributeMapper.selectById(id);
    }
    @Test
    public void updateByIdTest(){
        Long id = Long.valueOf(123);

        Long entityId = Long.valueOf(456);

        Long viewId = Long.valueOf(789);

        String name = "b";

        String dataType = "varchar";

        int isPrimary = 0;

        int isForeign = 1;

        int isDelete = 0;

        Date gmtCreate = null;

        Date gmtModified = null;


        AttributeDO attributeDO = new AttributeDO(id, entityId, viewId,
                name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);

        Assert.assertNotNull(sqlSession);
        AttributeMapper attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        attributeMapper.updateById(attributeDO);



    }
    @Test
    public void deleteByIdTest(){
        Long id = Long.valueOf(123);
        Assert.assertNotNull(sqlSession);
        AttributeMapper attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        attributeMapper.deleteById(id);

    }

}


