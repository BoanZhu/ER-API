package io.github.MigadaTang.mapper;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.TestCommon;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.entity.RelationshipDO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
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
        RelationshipDO relationshipDO = new RelationshipDO(null, "relation4",
                null, null, null,
                Cardinality.ZeroToMany, Cardinality.ZeroToMany, 0, null, null);
        List<RelationshipDO> res = ER.relationshipMapper.selectByRelationship(relationshipDO);
        System.out.println(res);
    }

    @Test
    public void testCreateRelation() {
        RelationshipDO relationshipDO = new RelationshipDO(Long.valueOf(11), "relation4",
                Long.valueOf(4), Long.valueOf(4), Long.valueOf(3),
                Cardinality.ZeroToMany, Cardinality.ZeroToMany, 0, new Date(), new Date());
        Assert.assertEquals(ER.relationshipMapper.insert(relationshipDO), 1);
    }

    @Test
    public void testDeleteRelation() {
        Assert.assertEquals(ER.relationshipMapper.deleteByID(Long.valueOf(11)), 1);
    }

    @Test
    public void testUpdateRelation() {
        RelationshipDO relationshipDO = new RelationshipDO(Long.valueOf(4), "relation4update",
                Long.valueOf(3), Long.valueOf(4), Long.valueOf(3),
                Cardinality.ZeroToMany, Cardinality.ZeroToMany, 0, new Date(), new Date());
        Assert.assertEquals(ER.relationshipMapper.updateByID(relationshipDO), 1);
    }
}
