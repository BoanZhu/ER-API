package io.github.MigadaTang.mapper;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.TestCommon;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.entity.RelationshipDO;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


public class testRelationMapper {
    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
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
        assertEquals(ER.relationshipMapper.insert(relationshipDO), 1);
    }

    @Test
    public void testDeleteRelation() {
        RelationshipDO relationshipDO = new RelationshipDO("testCreate", 1L);
        ER.relationshipMapper.insert(relationshipDO);
        List<RelationshipDO> search = ER.relationshipMapper.selectByRelationship(relationshipDO);
        assertEquals(ER.relationshipMapper.deleteByID(search.get(0).getID()), 1);
    }

    @Test
    public void testUpdateRelation() {
        RelationshipDO relationshipDO = new RelationshipDO("testCreate", 1L);
        ER.relationshipMapper.insert(relationshipDO);
        List<RelationshipDO> search = ER.relationshipMapper.selectByRelationship(relationshipDO);
        assertEquals(ER.relationshipMapper.deleteByID(search.get(0).getID()), 1);
    }

    @Test
    public void testCountEntityNum() {
        List<Long> entityIDs = new ArrayList<>();
        entityIDs.add(438L);
        entityIDs.add(439L);
        List<CaseInsensitiveMap<String, Object>> list = ER.relationshipEdgeMapper.groupCountEntityNum(entityIDs, BelongObjType.ENTITY);

    }
}
