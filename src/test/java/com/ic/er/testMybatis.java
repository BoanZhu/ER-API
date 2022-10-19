package com.ic.er;

import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

public class testMybatis {

    @Test
    public void testMybatisConnect() {
        try {
            InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDBConnection() {
        ResultState resultState = ER.connectDB();
        Assert.assertNotNull(ER.sqlSession);
        System.out.println(ER.sqlSession.getConnection());
        Assert.assertEquals(resultState.getStatus(), ResultStateCode.Success);
    }
}
