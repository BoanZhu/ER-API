package io.github.MigadaTang.mapper;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.TestCommon;
import io.github.MigadaTang.entity.SchemaDO;
import io.github.MigadaTang.util.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class testSchemaMapper {
    @Before
    public void init() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
    }


    @Test
    public void testQueryAllSchemas() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", "creator3", Long.valueOf(1), 0, new Date(), new Date());
        ER.schemaMapper.insert(schemaDO);
        List<SchemaDO> schemaDOList = ER.schemaMapper.selectAll();
        Assert.assertTrue(schemaDOList.size() > 0);
    }

    @Test
    public void testQueryByID() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", "creator3", Long.valueOf(1), 0, new Date(), new Date());
        ER.schemaMapper.insert(schemaDO);
        SchemaDO search = ER.schemaMapper.selectByID(schemaDO.getID());
        System.out.println(search);
    }

    @Test
    public void testCreateSchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", "creator3", Long.valueOf(1), 0, new Date(), new Date());
        Assert.assertEquals(ER.schemaMapper.insert(schemaDO), 1);
    }

    @Test
    public void testDeleteSchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", "creator3", Long.valueOf(1), 0, new Date(), new Date());
        ER.schemaMapper.insert(schemaDO);
        Assert.assertEquals(ER.schemaMapper.deleteByID(schemaDO.getID()), 1);
    }

    @Test
    public void testQuerySchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema1", null, null, 0, null, null);
        ER.schemaMapper.insert(schemaDO);
        List<SchemaDO> res = ER.schemaMapper.selectBySchema(schemaDO);
        System.out.println(res);
    }

    @Test
    public void testUpdateSchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3update", "creator3update", Long.valueOf(1), 0, new Date(), new Date());
        ER.schemaMapper.insert(schemaDO);
        Assert.assertEquals(ER.schemaMapper.updateByID(schemaDO), 1);
    }
}
