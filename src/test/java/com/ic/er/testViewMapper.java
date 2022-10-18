package com.ic.er;

import com.ic.er.bean.entity.ViewDO;
import com.ic.er.dao.ViewMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;

public class testViewMapper {
    public static SqlSession sqlSession;
    public static Connection connection;
    public static ViewMapper viewMapper;

    @Before
    public void init() throws IOException {
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        connection = sqlSession.getConnection();
        System.out.println(connection);
        viewMapper = sqlSession.getMapper(ViewMapper.class);
    }

    @Test
    public void testQueryAllViews() {
        Assert.assertNotNull(sqlSession);
        List<ViewDO> viewDOList = viewMapper.selectAll();
        Assert.assertEquals(viewDOList.size(), 0);
    }

}
