package io.github.MigadaTang;

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
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void createWeakEntitySchema() throws IOException {
        Schema example = ER.createSchema("weakEntity-SwipeCardForPerson", "");

        Entity person = example.addEntity("person");
        person.addAttribute("salary number", DataType.VARCHAR, true, false);

        ImmutablePair<Entity, Relationship> pair = example.addWeakEntity("swipe card", person, "for", Cardinality.OneToOne, Cardinality.ZeroToMany);
        Entity swipeCard = pair.left;
        Relationship relationship = pair.right;
        swipeCard.addAttribute("issue", DataType.INT, true, false);
        swipeCard.addAttribute("date", DataType.VARCHAR, false, false);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
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
        worksIn.addAttribute("start_date", DataType.VARCHAR, false);
        worksIn.addAttribute("end_date", DataType.VARCHAR, true);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void createSubsetSchema() throws IOException {
        Schema example = ER.createSchema("subset-ManagerPerson", "");

        Entity person = example.addEntity("person");
        person.addAttribute("salary number", DataType.VARCHAR, true, false);
        person.addAttribute("bonus", DataType.VARCHAR, false, true);
        person.addAttribute("name", DataType.VARCHAR, false, false);

        Entity manager = example.addSubset("manager", person);
        manager.addAttribute("mobile number", DataType.VARCHAR, false, false);

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

    @Test
    public void loadFromJSONTest() throws IOException {
        String jsonString = Files.readString(Path.of("src/test/java/io/github/MigadaTang/jsonExamples/vanilla-BranchAccountMovement.json"), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }
}
