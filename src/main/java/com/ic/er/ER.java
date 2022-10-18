package com.ic.er.util;

import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class boot {
    public static SqlSession sqlSession;

    public static ResultState connectDB(){
        ResultState resultState = null;
        try {
            InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
                    sqlSession = sqlSessionFactory.openSession(true);
                    resultState = ResultState.ok();
        } catch (IOException msg) {
            resultState = ResultState.build(ResultStateCode.FAILRESULTCODE, msg.getMessage());
            return resultState;
        }
        return resultState;
    }
}
