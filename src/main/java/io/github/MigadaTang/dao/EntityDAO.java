package io.github.MigadaTang.dao;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.entity.EntityDO;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class EntityDAO {
    public static EntityDO selectByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        EntityMapper mapper = sqlSession.getMapper(EntityMapper.class);
        EntityDO entityDO = mapper.selectByID(ID);
        sqlSession.close();
        return entityDO;
    }

    public static List<EntityDO> selectByEntity(EntityDO entityDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        EntityMapper mapper = sqlSession.getMapper(EntityMapper.class);
        List<EntityDO> doList = mapper.selectByEntity(entityDO);
        sqlSession.close();
        return doList;
    }

    public static int insert(EntityDO entityDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        EntityMapper mapper = sqlSession.getMapper(EntityMapper.class);
        int ret = mapper.insert(entityDO);
        sqlSession.close();
        return ret;
    }

    // rarely use, please use update to change is_delete to 1
    public static int deleteByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        EntityMapper mapper = sqlSession.getMapper(EntityMapper.class);
        int ret = mapper.deleteByID(ID);
        sqlSession.close();
        return ret;
    }

    public static int updateByID(EntityDO entityDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        EntityMapper mapper = sqlSession.getMapper(EntityMapper.class);
        int ret = mapper.updateByID(entityDO);
        sqlSession.close();
        return ret;
    }

}
