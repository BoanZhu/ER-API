package io.github.MigadaTang.mapper;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.TestCommon;
import io.github.MigadaTang.entity.RelationshipDO;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class testRelationMapper {
    @Before
    public void init() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
    }


    @Test
    public void testQueryRelation() {
        RelationshipDO relationshipDO = ER.relationshipMapper.selectByID(Long.valueOf(3));
        System.out.println(relationshipDO);
    }

    @Test
    public void testQueryRelationByRelation() {
        RelationshipDO relationshipDO = new RelationshipDO(null, "relation4", (long) 0,
                null, null, null);
        List<RelationshipDO> res = ER.relationshipMapper.selectByRelationship(relationshipDO);
        System.out.println(res);
    }

    @Test
    public void testCreateRelation() {
        RelationshipDO relationshipDO = new RelationshipDO("testCreate", 1L);
        Assert.assertEquals(ER.relationshipMapper.insert(relationshipDO), 1);
    }

    @Test
    public void testDeleteRelation() {
        RelationshipDO relationshipDO = new RelationshipDO("testCreate", 1L);
        ER.relationshipMapper.insert(relationshipDO);
        List<RelationshipDO> search = ER.relationshipMapper.selectByRelationship(relationshipDO);
        Assert.assertEquals(ER.relationshipMapper.deleteByID(search.get(0).getID()), 1);
    }

    @Test
    public void testUpdateRelation() {
        RelationshipDO relationshipDO = new RelationshipDO("testCreate", 1L);
        ER.relationshipMapper.insert(relationshipDO);
        List<RelationshipDO> search = ER.relationshipMapper.selectByRelationship(relationshipDO);
        Assert.assertEquals(ER.relationshipMapper.deleteByID(search.get(0).getID()), 1);
    }

    @Test
    public void testCountEntityNum() {
        List<Long> entityIDs = new ArrayList<>();
        entityIDs.add(438L);
        entityIDs.add(439L);
        List<CaseInsensitiveMap<String, Object>> list = ER.relationshipEdgeMapper.groupCountEntityNum(entityIDs);

    }
}
