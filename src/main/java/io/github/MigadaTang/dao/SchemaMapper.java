package io.github.MigadaTang.dao;

import io.github.MigadaTang.entity.SchemaDO;

import java.util.List;

public interface SchemaMapper {
    List<SchemaDO> selectAll();

    List<SchemaDO> selectBySchema(SchemaDO schemaDO);

    SchemaDO selectByID(Long ID);

    int insert(SchemaDO schemaDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(SchemaDO schemaDO);

}
