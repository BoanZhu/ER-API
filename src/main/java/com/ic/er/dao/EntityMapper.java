package com.ic.er.dao;

import com.ic.er.bean.entity.EntityDO;

import java.util.List;

public interface EntityMapper {
    EntityDO selectById(Long id);

    List<EntityDO> selectByEntity(EntityDO entityDO);

    Long insert(EntityDO entityDO);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);

}
