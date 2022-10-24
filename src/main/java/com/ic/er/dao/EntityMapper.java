package com.ic.er.dao;

import com.ic.er.dto.entity.EntityDO;

import java.util.List;

public interface EntityMapper {
    EntityDO selectByID(Long ID);

    List<EntityDO> selectByEntity(EntityDO entityDO);

    int insert(EntityDO entityDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(EntityDO entityDO);

}
