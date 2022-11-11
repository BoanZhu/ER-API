package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import com.ic.er.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestER {

    @Before
    public void setUp() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void createSchemaTest() {
        Schema testSchema = ER.createSchema("testSchema", "wt22");
        Assert.assertNotNull(ER.querySchemaByID(testSchema.getID()));
    }

    @Test
    public void deleteSchemaTest() {
        Schema testSchema = ER.createSchema("testSchema", "wt22");
        Assert.assertNotEquals(ER.queryAllSchema().size(), 0);
        ER.deleteSchema(testSchema);
        assertThrows(ERException.class, () -> ER.querySchemaByID(testSchema.getID()));
    }

    @Test
    public void createVanillaERSchema() throws IOException {
        Schema example = ER.createSchema("BranchAccountMovement", "");

        Entity branch = example.addEntity("branch");
        branch.addAttribute("sortcode", DataType.INT, true, false);
        branch.addAttribute("bname", DataType.VARCHAR, false, false);
        branch.addAttribute("cash", DataType.DOUBLE, false, false);

        Entity account = example.addEntity("account");
        account.addAttribute("no", DataType.INT, true, false);
        account.addAttribute("type", DataType.CHAR, false, false);
        account.addAttribute("cname", DataType.VARCHAR, false, false);
        account.addAttribute("rate", DataType.DOUBLE, false, true);

        Entity movement = example.addEntity("movement");
        movement.addAttribute("mid", DataType.INT, true, false);
        movement.addAttribute("amount", DataType.DOUBLE, false, false);
        movement.addAttribute("tdate", DataType.DATETIME, false, false);

        Relationship holds = example.createRelationship("holds", account, branch, Cardinality.OneToOne, Cardinality.ZeroToMany);
        Relationship has = example.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format("%s.json", example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }


    // Allowing attributes on relationships
    @Test
    public void createERaTest() {
        Schema testSchema = ER.createSchema("PersonWorksDepartment", "wt22");

        Entity person = testSchema.addEntity("person");
        person.addAttribute("salary_number", DataType.VARCHAR, true, false);

        Entity department = testSchema.addEntity("department");
        department.addAttribute("dname", DataType.VARCHAR, true, false);

        Relationship ts = testSchema.createRelationship("works in", person, department, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        ts.addAttribute("start_date", DataType.VARCHAR, false);
        ts.addAttribute("end_date", DataType.VARCHAR, true);

        Schema dbSchema = Schema.queryByID(testSchema.getID());
        Assert.assertNotNull(dbSchema);
        Assert.assertEquals(dbSchema.getEntityList().size(), 2);
        Assert.assertEquals(dbSchema.getRelationshipList().size(), 1);
        Assert.assertEquals(dbSchema.getRelationshipList().get(0).getAttributeList().size(), 2);
    }

    //    @Test
    public void loadFromJSONTest() throws IOException {
        String jsonString = Files.readString(Path.of("BranchAccountMovement.json"), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }
}
