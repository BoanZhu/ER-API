package com.ic.er.dao;

import com.ic.er.bean.entity.Entity;

import java.util.List;

/**
 * @Desceiption entity mapper
 * @author wendi
 * @data 15/10/2022
 */
public interface EntityMapper {
    Entity selectById(Long id);

    List<Entity> selectByEntity(Entity entity);

    int insert(Entity entity);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);

}
