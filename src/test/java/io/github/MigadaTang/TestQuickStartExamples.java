package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.ConnObjWithCardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ParseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;

public class TestQuickStartExamples {
    private static final String outputFormat = "src/test/java/io/github/MigadaTang/jsonExamples/%s.json";
    private static final String outputImagePath = "src/test/java/io/github/MigadaTang/renderImageExamples/%s";

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void createVanillaERSchema() throws IOException, ParseException {
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

        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }

    @Test
    public void createWeakEntitySchema() throws IOException, ParseException {
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

        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }

    @Test
    public void createNaryRelationshipSchema() throws IOException, ParseException {
        Schema example = ER.createSchema("naryRelationshipWithSubset-PersonManagerDepartment");

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

        String renderJSON = example.toRenderJSON();
        assertNotNull(renderJSON);

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        assertNotNull(schema);

        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }

    @Test
    public void createAttributeOnRelationshipSchema() throws IOException, ParseException {
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

        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }

    @Test
    public void createSubsetSchema() throws IOException, ParseException {
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

        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }

    @Test
    public void createNestedRelationship() throws IOException, ParseException {
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

        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }
}
