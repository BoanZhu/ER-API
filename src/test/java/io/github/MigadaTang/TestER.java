package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.ConnObjWithCardinality;
import io.github.MigadaTang.common.DataType;
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

public class TestER {

    private static String outputFormat = "src/test/java/io/github/MigadaTang/jsonExamples/%s.json";

    @Before
    public void setUp() throws Exception {
        ER.initialize(TestCommon.usePostgre);
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

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
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

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }

    @Test
    public void createNaryRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("naryRelationship-PersonManagerDepartment", "");

        Entity person = example.addEntity("person");
        Entity manager = example.addEntity("manager");
        Entity department = example.addEntity("department");

        ArrayList<ConnObjWithCardinality> eCardList = new ArrayList<>();
        eCardList.add(new ConnObjWithCardinality(person, Cardinality.ZeroToMany));
        eCardList.add(new ConnObjWithCardinality(manager, Cardinality.ZeroToMany));
        eCardList.add(new ConnObjWithCardinality(department, Cardinality.ZeroToMany));
        Relationship worksIn = example.createNaryRelationship("works in", eCardList);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
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

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
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

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }

    @Test
    public void createNestedRelationship() throws IOException {
        Schema example = ER.createSchema("nested-Person_Department_Project", "");

        Entity person = example.addEntity("person");
        person.addAttribute("salary number", DataType.VARCHAR, true, AttributeType.Mandatory);

        Entity department = example.addEntity("department");
        department.addAttribute("dname", DataType.VARCHAR, false, AttributeType.Mandatory);

        Entity project = example.addEntity("project");
        project.addAttribute("pcode", DataType.VARCHAR, false, AttributeType.Mandatory);

        Relationship worksIn = example.createRelationship("works in", person, department, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Relationship member = example.createRelationship("member", worksIn, project, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        member.addAttribute("role", DataType.VARCHAR, AttributeType.Mandatory);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }


    @Test
    public void loadFromJSONTest() throws IOException {
        String jsonString = Files.readString(Path.of("src/test/java/io/github/MigadaTang/jsonExamples/nested-Person_Department_Project.json"), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }
}
