package com.ic.er.mapper;

import com.ic.er.ER;
import com.ic.er.TestCommon;
import com.ic.er.entity.RelationshipDO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
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
//        RelationshipDO relationshipDO = new RelationshipDO(11L, "relation4", 4L);
//        Assert.assertEquals(ER.relationshipMapper.insert(relationshipDO), 1);
    }

    @Test
    public void testDeleteRelation() {
        Assert.assertEquals(ER.relationshipMapper.deleteByID(11L), 1);
    }

    @Test
    public void testUpdateRelation() {
//        RelationshipDO relationshipDO = new RelationshipDO(11L, "relation4update", 3L);
//        Assert.assertEquals(ER.relationshipMapper.updateByID(relationshipDO), 1);
    }
}
