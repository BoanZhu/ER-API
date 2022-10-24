package com.ic.er.dao;

import com.ic.er.dto.entity.AttributeDO;

import java.util.List;

public interface AttributeMapper {
    AttributeDO selectByID(Long ID);

    List<AttributeDO> selectByAttribute(AttributeDO attributeDO);

    int insert(AttributeDO attributeDO);

    int deleteByID(Long ID);

    int updateByID(AttributeDO attributeDO);

}
