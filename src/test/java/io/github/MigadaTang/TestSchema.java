package io.github.MigadaTang;

import io.github.MigadaTang.exception.ERException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

public class TestSchema {

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void updateSchemaTest() {
        Schema firstSchema = ER.createSchema("first schema");
        String newSchemaName = "new schema name";
        firstSchema.updateInfo(newSchemaName);

        Schema newSchema = Schema.queryByID(firstSchema.getID());
        assertEquals(newSchema.getName(), newSchemaName);
    }

    @Test
    public void deleteSchemaTest() {
        Schema testDeleteSchema = ER.createSchema("first schema");
        testDeleteSchema = Schema.queryByID(testDeleteSchema.getID());
        assertNotNull(testDeleteSchema);

        ER.deleteSchema(testDeleteSchema);

        Schema finalTestDeleteSchema = testDeleteSchema;
        assertThrows(ERException.class, () -> Schema.queryByID(finalTestDeleteSchema.getID()));
    }

//    @Test
//    public void querySchemaTest() {
//        Schema firstSchema = ER.createSchema("first schema");
//        List<Schema> schemas = Schema.queryAll();
//        assertNotEquals(schemas.size(), 0);
//    }
}
