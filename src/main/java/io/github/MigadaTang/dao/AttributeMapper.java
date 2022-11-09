package io.github.MigadaTang.dao;

import io.github.MigadaTang.entity.AttributeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttributeMapper {
    AttributeDO selectByID(Long ID);

    List<AttributeDO> selectByAttribute(AttributeDO attributeDO);

    int insert(AttributeDO attributeDO);

    int deleteByID(Long ID);

    int updateByID(AttributeDO attributeDO);

}
