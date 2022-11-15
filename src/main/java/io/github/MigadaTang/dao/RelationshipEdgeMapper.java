package io.github.MigadaTang.dao;

import io.github.MigadaTang.entity.RelationshipEdgeDO;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;

public interface RelationshipEdgeMapper {
    RelationshipEdgeDO selectByID(Long ID);

    List<RelationshipEdgeDO> selectByRelationshipEdge(RelationshipEdgeDO relationshipEdgeDO);

    List<CaseInsensitiveMap<String, Object>> groupCountEntityNum(List<Long> entityIDs);

    int insert(RelationshipEdgeDO relationshipEdgeDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(RelationshipEdgeDO relationshipEdgeDO);
}
