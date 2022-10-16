package com.ic.er.dao;

import com.ic.er.bean.entity.Relationship;

import java.util.List;

/**
 * @Desceiption Relationship entity
 * @author wendi
 * @data 15/10/2022
 */
public interface RelationshipMapper {
    Relationship selectById(Long id);

    List<Relationship> selectByRelationship(Relationship relationship);

    int insert(Relationship relationship);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);
}
