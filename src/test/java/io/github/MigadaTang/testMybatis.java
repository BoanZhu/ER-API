package io.github.MigadaTang;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

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
    public void testDBConnection() throws IOException, SQLException {
        ER.initialize(false);
        Assert.assertNotNull(ER.sqlSession);
        System.out.println(ER.sqlSession.getConnection());
    }
}
