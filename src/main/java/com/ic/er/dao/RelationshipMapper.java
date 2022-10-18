package com.ic.er.dao;

import com.ic.er.bean.entity.RelationshipDO;

import java.util.List;

public interface RelationshipMapper {
    RelationshipDO selectById(Long id);

    List<RelationshipDO> selectByRelationship(RelationshipDO relationshipDO);

    Long insert(RelationshipDO relationshipDO);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);
}
