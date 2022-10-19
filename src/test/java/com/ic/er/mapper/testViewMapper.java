package com.ic.er.mapper;

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

import javax.swing.text.View;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Date;
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
        Assert.assertEquals(1,viewDOList.size());
    }

    @Test
    public void testQueryById(){
        Assert.assertNotNull(sqlSession);
        ViewDO viewDO = viewMapper.selectById(Long.valueOf(3));
        System.out.println(viewDO);
    }
    @Test
    public void testCreateView(){
        Assert.assertNotNull(sqlSession);
        ViewDO viewDO = new ViewDO(Long.valueOf(2),"view3","creator3", Long.valueOf(1),0, new Date(),new Date());
        Assert.assertEquals(viewMapper.insert(viewDO),1);
    }

    @Test
    public void testDeleteView(){
        Assert.assertNotNull(sqlSession);
        Assert.assertEquals(viewMapper.deleteById(Long.valueOf(2)),1);
    }

    @Test
    public void testQueryView(){
        Assert.assertNotNull(sqlSession);
        ViewDO viewDO = new ViewDO(null,"view1",null, null,0, null,null);
        List<ViewDO> res = viewMapper.selectByView(viewDO);
        System.out.println(res);

    }

    @Test
    public void testUpdateView(){
        Assert.assertNotNull(sqlSession);
        ViewDO viewDO = new ViewDO(Long.valueOf(3),"view3update","creator3update", Long.valueOf(1),0, new Date(),new Date());
        Assert.assertEquals(viewMapper.updateById(viewDO),1);
    }

}
