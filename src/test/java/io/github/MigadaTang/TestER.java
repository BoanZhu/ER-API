package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.EntityWithCardinality;
import io.github.MigadaTang.exception.ERException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestER {

    private static String outputFormat = "src/test/java/io/github/MigadaTang/jsonExamples/%s.json";

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
        Schema example = ER.createSchema("vanilla-BranchAccountMovement", "");

        Entity branch = example.addEntity("branch");
        branch.addAttribute("sortcode", DataType.INT, true, AttributeType.Mandatory);
        branch.addAttribute("bname", DataType.VARCHAR, false, AttributeType.Mandatory);
        branch.addAttribute("cash", DataType.DOUBLE, false, AttributeType.Mandatory);

        Entity account = example.addEntity("account");
        account.addAttribute("no", DataType.INT, true, AttributeType.Mandatory);
        account.addAttribute("type", DataType.CHAR, false, AttributeType.Mandatory);
        account.addAttribute("cname", DataType.VARCHAR, false, AttributeType.Mandatory);
        account.addAttribute("rate", DataType.DOUBLE, false, AttributeType.Mandatory);

        Entity movement = example.addEntity("movement");
        movement.addAttribute("mid", DataType.INT, true, AttributeType.Mandatory);
        movement.addAttribute("amount", DataType.DOUBLE, false, AttributeType.Mandatory);
        movement.addAttribute("tdate", DataType.DATETIME, false, AttributeType.Mandatory);

        Relationship holds = example.createRelationship("holds", account, branch, Cardinality.OneToOne, Cardinality.ZeroToMany);
        Relationship has = example.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void createWeakEntitySchema() throws IOException {
        Schema example = ER.createSchema("weakEntity-SwipeCardForPerson", "");

        Entity person = example.addEntity("person");
        person.addAttribute("salary number", DataType.VARCHAR, true, AttributeType.Mandatory);

        ImmutablePair<Entity, Relationship> pair = example.addWeakEntity("swipe card", person, "for", Cardinality.OneToOne, Cardinality.ZeroToMany);
        Entity swipeCard = pair.left;
        Relationship relationship = pair.right;
        swipeCard.addAttribute("issue", DataType.INT, true, AttributeType.Mandatory);
        swipeCard.addAttribute("date", DataType.VARCHAR, false, AttributeType.Mandatory);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat + "2", example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void createNaryRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("naryRelationship-PersonManagerDepartment", "");

        Entity person = example.addEntity("person");
        Entity manager = example.addEntity("manager");
        Entity department = example.addEntity("department");

        ArrayList<EntityWithCardinality> eCardList = new ArrayList<>();
        eCardList.add(new EntityWithCardinality(person, Cardinality.ZeroToMany));
        eCardList.add(new EntityWithCardinality(manager, Cardinality.ZeroToMany));
        eCardList.add(new EntityWithCardinality(department, Cardinality.ZeroToMany));
        Relationship worksIn = example.createNaryRelationship("works in", eCardList);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void createAttributeOnRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("attributeOnRelationship-PersonDepartment", "");

        Entity person = example.addEntity("person");
        Entity department = example.addEntity("department");

        Relationship worksIn = example.createRelationship("works in", person, department, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        worksIn.addAttribute("start_date", DataType.VARCHAR, AttributeType.Mandatory);
        worksIn.addAttribute("end_date", DataType.VARCHAR, AttributeType.Optional);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void createSubsetSchema() throws IOException {
        Schema example = ER.createSchema("subset-ManagerPerson", "");

        Entity person = example.addEntity("person");
        person.addAttribute("salary number", DataType.VARCHAR, true, AttributeType.Mandatory);
        person.addAttribute("bonus", DataType.VARCHAR, false, AttributeType.Optional);
        person.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);

        Entity manager = example.addSubset("manager", person);
        manager.addAttribute("mobile number", DataType.VARCHAR, false, AttributeType.Mandatory);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }


    // Allowing attributes on relationships
    @Test
    public void createERaTest() {
        Schema testSchema = ER.createSchema("PersonWorksDepartment", "wt22");

        Entity person = testSchema.addEntity("person");
        person.addAttribute("salary_number", DataType.VARCHAR, true, AttributeType.Mandatory);

        Entity department = testSchema.addEntity("department");
        department.addAttribute("dname", DataType.VARCHAR, true, AttributeType.Mandatory);

        Relationship ts = testSchema.createRelationship("works in", person, department, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        ts.addAttribute("start_date", DataType.VARCHAR, AttributeType.Mandatory);
        ts.addAttribute("end_date", DataType.VARCHAR, AttributeType.Optional);

        Schema dbSchema = Schema.queryByID(testSchema.getID());
        Assert.assertNotNull(dbSchema);
        Assert.assertEquals(dbSchema.getEntityList().size(), 2);
        Assert.assertEquals(dbSchema.getRelationshipList().size(), 1);
        Assert.assertEquals(dbSchema.getRelationshipList().get(0).getAttributeList().size(), 2);
    }

    @Test
    public void loadFromJSONTest() throws IOException {
        String jsonString = Files.readString(Path.of("src/test/java/io/github/MigadaTang/jsonExamples/vanilla-BranchAccountMovement.json"), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }
}
