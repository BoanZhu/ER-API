package com.ic.er.dao;

import com.ic.er.bean.entity.AttributeDO;

import java.util.List;

public interface AttributeMapper {
    AttributeDO selectById(Long id);

    List<AttributeDO> selectByAttribute(AttributeDO attributeDO);

    int insert(AttributeDO attributeDO);

    int deleteById(Long id);

    int updateById(AttributeDO attributeDO);

}
