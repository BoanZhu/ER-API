package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.ConnObjWithCardinality;
import io.github.MigadaTang.common.DataType;
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
    public void createVanillaERSchema() throws IOException {
        Schema example = ER.createSchema("Vanilla");

        Entity branch = example.addEntity("branch");
        branch.addPrimaryKey("sortcode", DataType.INT);
        branch.addAttribute("bname", DataType.VARCHAR, AttributeType.Mandatory);
        branch.addAttribute("cash", DataType.DOUBLE, AttributeType.Mandatory);

        Entity account = example.addEntity("account");
        account.addPrimaryKey("no", DataType.INT);
        account.addAttribute("type", DataType.CHAR, AttributeType.Mandatory);
        account.addAttribute("cname", DataType.VARCHAR, AttributeType.Mandatory);
        account.addAttribute("rate", DataType.DOUBLE, AttributeType.Mandatory);

        Entity movement = example.addEntity("movement");
        movement.addPrimaryKey("mid", DataType.INT);
        movement.addAttribute("amount", DataType.DOUBLE, AttributeType.Mandatory);
        movement.addAttribute("tdate", DataType.DATETIME, AttributeType.Mandatory);

        Relationship holds = example.createRelationship("holds", account, branch, Cardinality.OneToOne, Cardinality.ZeroToMany);
        Relationship has = example.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);
        // export the ER schema to a JSON file
        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        assertNotNull(schema);

        // save your ER schema as image
        schema.renderAsImage(String.format(outputImagePath, example.getName()));
    }

    @Test
    public void createWeakEntitySchema() throws IOException {
        Schema example = ER.createSchema("Weak entity");

        Entity person = example.addEntity("person");
        person.addPrimaryKey("salary number", DataType.VARCHAR);

        ImmutablePair<Entity, Relationship> pair = example.addWeakEntity("swipe card", person, "for", Cardinality.OneToOne, Cardinality.ZeroToMany);
        Entity swipeCard = pair.left;
        Relationship relationship = pair.right;
        swipeCard.addPrimaryKey("issue", DataType.INT);
        swipeCard.addAttribute("date", DataType.VARCHAR, AttributeType.Mandatory);

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
    public void createNaryRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("N-ary Relationship");

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
    public void createAttributeOnRelationshipSchema() throws IOException {
        Schema example = ER.createSchema("Attributes on relationship");

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
    public void createSubsetSchema() throws IOException {
        Schema example = ER.createSchema("Subset");

        Entity person = example.addEntity("person");
        person.addPrimaryKey("salary number", DataType.VARCHAR);
        person.addAttribute("bonus", DataType.VARCHAR, AttributeType.Optional);
        person.addAttribute("name", DataType.VARCHAR, AttributeType.Mandatory);

        Entity manager = example.addSubset("manager", person);
        manager.addAttribute("mobile number", DataType.VARCHAR, AttributeType.Mandatory);

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
    public void createNestedRelationship() throws IOException {
        Schema example = ER.createSchema("Nested relationship");

        Entity person = example.addEntity("person");
        person.addPrimaryKey("salary number", DataType.VARCHAR);

        Entity department = example.addEntity("department");
        department.addAttribute("dname", DataType.VARCHAR, AttributeType.Mandatory);

        Entity project = example.addEntity("project");
        project.addAttribute("pcode", DataType.VARCHAR, AttributeType.Mandatory);

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
