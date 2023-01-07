package io.github.MigadaTang.dao;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class RelationshipEdgeDAO {
    public static RelationshipEdgeDO selectByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipEdgeMapper mapper = sqlSession.getMapper(RelationshipEdgeMapper.class);
        RelationshipEdgeDO edgeDO = mapper.selectByID(ID);
        sqlSession.close();
        return edgeDO;
    }

    public static List<RelationshipEdgeDO> selectByRelationshipEdge(RelationshipEdgeDO relationshipEdgeDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipEdgeMapper mapper = sqlSession.getMapper(RelationshipEdgeMapper.class);
        List<RelationshipEdgeDO> edgeDOS = mapper.selectByRelationshipEdge(relationshipEdgeDO);
        sqlSession.close();
        return edgeDOS;
    }

    public static List<CaseInsensitiveMap<String, Object>> groupCountEntityNum(List<Long> belongObjIDList, BelongObjType belongObjType) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipEdgeMapper mapper = sqlSession.getMapper(RelationshipEdgeMapper.class);
        List<CaseInsensitiveMap<String, Object>> maps = mapper.groupCountEntityNum(belongObjIDList, belongObjType);
        sqlSession.close();
        return maps;
    }

    public static int insert(RelationshipEdgeDO relationshipEdgeDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipEdgeMapper mapper = sqlSession.getMapper(RelationshipEdgeMapper.class);
        int ret = mapper.insert(relationshipEdgeDO);
        sqlSession.close();
        return ret;
    }

    public static int deleteByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipEdgeMapper mapper = sqlSession.getMapper(RelationshipEdgeMapper.class);
        int ret = mapper.deleteByID(ID);
        sqlSession.close();
        return ret;
    }

    public static int updateByID(RelationshipEdgeDO relationshipEdgeDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        RelationshipEdgeMapper mapper = sqlSession.getMapper(RelationshipEdgeMapper.class);
        int ret = mapper.updateByID(relationshipEdgeDO);
        sqlSession.close();
        return ret;
    }
}
