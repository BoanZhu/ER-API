package io.github.MigadaTang;

import io.github.MigadaTang.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSchema {

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void updateSchemaTest() {
        Schema firstSchema = ER.createSchema("first schema", "tw");
        String newSchemaName = "new schema name";
        firstSchema.updateInfo(newSchemaName);

        Schema newSchema = Schema.queryByID(firstSchema.getID());
        Assert.assertEquals(newSchema.getName(), newSchemaName);
    }

    @Test
    public void deleteSchemaTest() {
        Schema testDeleteSchema = ER.createSchema("first schema", "tw");
        testDeleteSchema = Schema.queryByID(testDeleteSchema.getID());
        Assert.assertNotNull(testDeleteSchema);

        ER.deleteSchema(testDeleteSchema);

        Schema finalTestDeleteSchema = testDeleteSchema;
        assertThrows(ERException.class, () -> Schema.queryByID(finalTestDeleteSchema.getID()));
    }

    @Test
    public void querySchemaTest() {
        Schema firstSchema = ER.createSchema("first schema", "tw");
        List<Schema> schemas = Schema.queryAll();
        Assert.assertNotEquals(schemas.size(), 0);
    }
}
