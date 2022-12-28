package io.github.MigadaTang.dao;

import io.github.MigadaTang.entity.RelationshipDO;

import java.util.List;

public interface RelationshipMapper {
    RelationshipDO selectByID(Long ID);

    List<RelationshipDO> selectByRelationship(RelationshipDO relationshipDO);

    int insert(RelationshipDO relationshipDO);

    int deleteByID(Long ID);

    int updateByID(RelationshipDO relationshipDO);
}
