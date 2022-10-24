package com.ic.er.dao;

import com.ic.er.dto.entity.RelationshipDO;

import java.util.List;

public interface RelationshipMapper {
    RelationshipDO selectByID(Long ID);

    List<RelationshipDO> selectByRelationship(RelationshipDO relationshipDO);

    int insert(RelationshipDO relationshipDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(RelationshipDO relationshipDO);
}
