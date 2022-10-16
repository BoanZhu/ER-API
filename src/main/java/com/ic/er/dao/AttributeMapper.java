package com.ic.er.dao;

import com.ic.er.bean.entity.Attribute;

import java.util.List;

public interface AttributeMapper {
    Attribute selectById(Long id);

    List<Attribute> selectByAttribute(Attribute attribute);

    int insert(Attribute attribute);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);

}
