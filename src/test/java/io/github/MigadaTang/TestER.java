package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.ERException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;

import static junit.framework.TestCase.assertEquals;
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
    public void loadCheckTest() {
        Schema example = ER.createSchema("loadcheck");

        Entity person = example.addEntity("person");
        Attribute att1 = person.addAttribute("att1", DataType.VARCHAR, true, AttributeType.Mandatory);
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
}
