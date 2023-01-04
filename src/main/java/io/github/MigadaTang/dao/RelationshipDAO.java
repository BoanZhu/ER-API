package io.github.MigadaTang.dao;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.entity.RelationshipDO;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class RelationshipDAO {
    public static RelationshipDO selectByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipMapper mapper = sqlSession.getMapper(RelationshipMapper.class);
        RelationshipDO edgeDO = mapper.selectByID(ID);
        sqlSession.close();
        return edgeDO;
    }

    public static List<RelationshipDO> selectByRelationship(RelationshipDO relationshipDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipMapper mapper = sqlSession.getMapper(RelationshipMapper.class);
        List<RelationshipDO> dos = mapper.selectByRelationship(relationshipDO);
        sqlSession.close();
        return dos;
    }

    public static int insert(RelationshipDO relationshipDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipMapper mapper = sqlSession.getMapper(RelationshipMapper.class);
        int ret = mapper.insert(relationshipDO);
        sqlSession.close();
        return ret;
    }

    public static int deleteByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipMapper mapper = sqlSession.getMapper(RelationshipMapper.class);
        int ret = mapper.deleteByID(ID);
        sqlSession.close();
        return ret;
    }

    public static int updateByID(RelationshipDO relationshipDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipMapper mapper = sqlSession.getMapper(RelationshipMapper.class);
        int ret = mapper.updateByID(relationshipDO);
        sqlSession.close();
        return ret;
    }
}
