package com.ic.er.dao;

import com.ic.er.entity.RelationshipEdgeDO;

import java.util.List;

public interface RelationshipEdgeMapper {
    RelationshipEdgeDO selectByID(Long ID);

    List<RelationshipEdgeDO> selectByRelationshipEdge(RelationshipEdgeDO relationshipEdgeDO);

    int insert(RelationshipEdgeDO relationshipEdgeDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(RelationshipEdgeDO relationshipEdgeDO);
}