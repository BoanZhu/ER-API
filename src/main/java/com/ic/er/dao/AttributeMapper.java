package com.ic.er.dao;

import com.ic.er.bean.entity.AttributeDO;

import java.util.List;

public interface AttributeMapper {
    AttributeDO selectById(Long id);

    List<AttributeDO> selectByAttribute(AttributeDO attributeDO);

    int insert(AttributeDO attributeDO);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(AttributeDO attributeDO);

}
