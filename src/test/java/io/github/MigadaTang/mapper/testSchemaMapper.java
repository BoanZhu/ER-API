package io.github.MigadaTang.mapper;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.TestCommon;
import io.github.MigadaTang.entity.SchemaDO;
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
        List<SchemaDO> schemaDOList = ER.schemaMapper.selectAll();
        Assert.assertEquals(1, schemaDOList.size());
    }

    @Test
    public void testQueryByID() {
        SchemaDO schemaDO = ER.schemaMapper.selectByID(Long.valueOf(3));
        System.out.println(schemaDO);
    }

    @Test
    public void testCreateSchema() {
        SchemaDO schemaDO = new SchemaDO(Long.valueOf(2), "schema3", "creator3", Long.valueOf(1), 0, new Date(), new Date());
        Assert.assertEquals(ER.schemaMapper.insert(schemaDO), 1);
    }

    @Test
    public void testDeleteSchema() {
        Assert.assertEquals(ER.schemaMapper.deleteByID(Long.valueOf(2)), 1);
    }

    @Test
    public void testQuerySchema() {
        SchemaDO schemaDO = new SchemaDO(null, "schema1", null, null, 0, null, null);
        List<SchemaDO> res = ER.schemaMapper.selectBySchema(schemaDO);
        System.out.println(res);

    }

    @Test
    public void testUpdateSchema() {
        SchemaDO schemaDO = new SchemaDO(Long.valueOf(3), "schema3update", "creator3update", Long.valueOf(1), 0, new Date(), new Date());
        Assert.assertEquals(ER.schemaMapper.updateByID(schemaDO), 1);
    }
}
