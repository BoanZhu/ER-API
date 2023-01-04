package io.github.MigadaTang.dao;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.entity.AttributeDO;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class AttributeDAO {
    public static AttributeDO selectByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        AttributeMapper mapper = sqlSession.getMapper(AttributeMapper.class);
        AttributeDO aDo = mapper.selectByID(ID);
        sqlSession.close();
        return aDo;
    }

    public static List<AttributeDO> selectByAttribute(AttributeDO attributeDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        AttributeMapper mapper = sqlSession.getMapper(AttributeMapper.class);
        List<AttributeDO> dos = mapper.selectByAttribute(attributeDO);
        sqlSession.close();
        return dos;
    }

    public static int insert(AttributeDO attributeDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        AttributeMapper mapper = sqlSession.getMapper(AttributeMapper.class);
        int ret = mapper.insert(attributeDO);
        sqlSession.close();
        return ret;
    }

    public static int deleteByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        AttributeMapper mapper = sqlSession.getMapper(AttributeMapper.class);
        int ret = mapper.deleteByID(ID);
        sqlSession.close();
        return ret;
    }

    public static int updateByID(AttributeDO attributeDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        AttributeMapper mapper = sqlSession.getMapper(AttributeMapper.class);
        int ret = mapper.updateByID(attributeDO);
        sqlSession.close();
        return ret;
    }
}
