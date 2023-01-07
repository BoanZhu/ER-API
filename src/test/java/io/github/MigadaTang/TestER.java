package io.github.MigadaTang;

import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ERException;
import io.github.MigadaTang.exception.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertThrows;

public class TestER {

    private static String outputFormat = "src/test/java/io/github/MigadaTang/jsonExamples/%s.json";

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void duplicatedRelationship() throws IOException {
        Schema example = ER.createSchema("nested-PersonDepartmentProject");

        Entity person = example.addEntity("person");
        Entity department = example.addEntity("department");

        person.updateLayoutInfo(1.1, 2.2);
        //layoutInfo test
        String exampleJSON = example.toJSON();
        assertNotNull(exampleJSON);


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
    public void CyclicRelationshipCheckTest() {
        Schema example = ER.createSchema("cyclic relationship");

        Entity e1 = example.addEntity("E1");
        Entity e2 = example.addEntity("E2");
        Entity e3 = example.addEntity("E3");
        Entity e4 = example.addEntity("E4");
        Entity e5 = example.addEntity("E5");

        e1.addPrimaryKey("e1", DataType.VARCHAR);
        e2.addPrimaryKey("e2", DataType.VARCHAR);
        e3.addPrimaryKey("e3", DataType.VARCHAR);
        e4.addPrimaryKey("e4", DataType.VARCHAR);
        e5.addPrimaryKey("e5", DataType.VARCHAR);

        Relationship r1 = example.createEmptyRelationship("R1");
        r1.linkObj(e1, Cardinality.OneToOne);
        Relationship r2 = example.createEmptyRelationship("R2");
        r2.linkObj(e2, Cardinality.OneToOne);
        Relationship r3 = example.createEmptyRelationship("R3");
        r3.linkObj(e3, Cardinality.OneToOne);
        Relationship r4 = example.createEmptyRelationship("R4");
        r4.linkObj(e4, Cardinality.OneToOne);
        r4.linkObj(e5, Cardinality.OneToOne);

        r1.linkObj(r2, Cardinality.OneToOne);
        r2.linkObj(r3, Cardinality.OneToOne);
        r3.linkObj(r1, Cardinality.OneToOne);
        r3.linkObj(r4, Cardinality.OneToOne);

        assertThrows(ERException.class, () -> example.comprehensiveCheck());
    }

    @Test
    public void loadCheckTest() {
        Schema example = ER.createSchema("loadcheck");

        Entity person = example.addEntity("person");
        Attribute att1 = person.addPrimaryKey("att1", DataType.VARCHAR);
        att1.updateAimPort(2);
        att1.updateLayoutInfo(3.3, 4.4);
        Entity department = example.addEntity("department");

        person.updateLayoutInfo(1.1, 2.2);
        //layoutInfo test
        String exampleJSON = example.toJSON();
        assertNotNull(exampleJSON);

        Schema loadSchema = ER.loadFromJSON(exampleJSON);
        for (Entity entity : loadSchema.getEntityList()) {
            if (entity.getName().equals("person")) {
                assertEquals(1.1, entity.getLayoutInfo().getLayoutX());
                assertEquals(2.2, entity.getLayoutInfo().getLayoutY());
                for (Attribute attribute : entity.getAttributeList()) {
                    if (attribute.getName().equals("att1")) {
                        assertEquals(3.3, att1.getLayoutInfo().getLayoutX());
                        assertEquals(4.4, att1.getLayoutInfo().getLayoutY());
                    }
                }
            }
        }
    }

    @Test
    public void emptyEntityGenerateDDLErrorTest() {
        Schema example = ER.createSchema("loadcheck");

        Entity person = example.addEntity("person");
        try {
            example.generateSqlStatement();
        } catch (ParseException e) {
            assertTrue(true);
            System.out.println(e.getMessage());
        }
    }

}
