package io.github.MigadaTang;

import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.ERException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertThrows;

public class TestER {

    private static String outputFormat = "src/test/java/io/github/MigadaTang/jsonExamples/%s.json";

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void initializeTest() throws Exception {
        ER.initialize();
        Connection connection = ER.sqlSession.getConnection();
        assertNotNull(connection);
        ER.initialize(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722", "wh722", "4jC@A3528>0N6");
        connection = ER.sqlSession.getConnection();
        assertNotNull(connection);
    }

    @Test
    public void createVanillaERSchema() throws IOException {
        Schema example = ER.createSchema("vanilla-BranchAccountMovement");

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
        assertNotNull(schema);
    }

    @Test
    public void createWeakEntitySchema() throws IOException {
        Schema example = ER.createSchema("weakEntity-SwipeCardForPerson");

        Entity person = example.addEntity("person");
        person.addAttribute("salary number", DataType.VARCHAR, true, AttributeType.Mandatory);

        ImmutablePair<Entity, Relationship> pair = example.addWeakEntity("swipe card", person, "for", Cardinality.OneToOne, Cardinality.ZeroToMany);
        Entity swipeCard = pair.left;
        Relationship relationship = pair.right;
        swipeCard.addAttribute("issue", DataType.INT, true, AttributeType.Mandatory);
        swipeCard.addAttribute("date", DataType.VARCHAR, false, AttributeType.Mandatory);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        assertNotNull(schema);
    }

    @Test
    public void createNaryRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("naryRelationship-PersonManagerDepartment");

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
        assertNotNull(schema);
    }

    @Test
    public void createAttributeOnRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("attributeOnRelationship-PersonDepartment");

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
        assertNotNull(schema);
    }

    @Test
    public void createSubsetSchema() throws IOException {
        Schema example = ER.createSchema("subset-ManagerPerson");

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
        assertNotNull(schema);
    }

    @Test
    public void createNestedRelationship() throws IOException {
        Schema example = ER.createSchema("nested-PersonDepartmentProject");

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
        assertNotNull(schema);
    }

    @Test
    public void duplicatedRelationship() throws IOException {
        Schema example = ER.createSchema("nested-PersonDepartmentProject");

        Entity person = example.addEntity("person");
        Entity department = example.addEntity("department");

        Relationship worksIn = example.createRelationship("works in", person, department, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Relationship worksIn2 = example.createRelationship("works in 2", person, department, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertThrows(ERException.class, () -> example.toJSON());

        Schema example2 = ER.createSchema("nested-PersonDepartmentProject2");
        Entity project = example2.addEntity("project");
        Entity project2 = example2.addEntity("project2");

        Relationship empty1 = example2.createEmptyRelationship("empty1");
        Relationship empty2 = example2.createEmptyRelationship("empty2");
        empty1.linkObj(project, Cardinality.ZeroToMany);
        empty1.linkObj(project2, Cardinality.ZeroToMany);
        empty2.linkObj(project, Cardinality.ZeroToMany);
        empty2.linkObj(project2, Cardinality.ZeroToMany);
        assertThrows(ERException.class, () -> example2.toJSON());
    }


    @Test
    public void loadFromJSONTest() throws IOException {
        String jsonString = Files.readString(Path.of("src/test/java/io/github/MigadaTang/jsonExamples/nested-PersonDepartmentProject.json"), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        assertNotNull(schema);
    }
}
