package io.github.MigadaTang.dao;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RelationshipEdgeMapper {
    RelationshipEdgeDO selectByID(Long ID);

    List<RelationshipEdgeDO> selectByRelationshipEdge(RelationshipEdgeDO relationshipEdgeDO);

    List<CaseInsensitiveMap<String, Object>> groupCountEntityNum(@Param("belongObjIDList") List<Long> belongObjIDList, @Param("belongObjType") BelongObjType belongObjType);

    int insert(RelationshipEdgeDO relationshipEdgeDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(RelationshipEdgeDO relationshipEdgeDO);
}
