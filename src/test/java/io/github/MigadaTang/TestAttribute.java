package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ERException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAttribute {

    private Schema testSchema;
    private Entity teacher;
    private Entity student;

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Before
    public void initializeSchema() {
        testSchema = ER.createSchema("testSchema", "wt22");
        teacher = testSchema.addEntity("teacher");
        student = testSchema.addEntity("student");
    }

    @Test
    public void addAttributeTest() {
        // primary key must be mandatory
        assertThrows(ERException.class, () -> teacher.addAttribute("testPrimaryWithNullable", DataType.INT, true, AttributeType.Optional));

        String attributeName = "teacher_id";
        DataType dataType = DataType.VARCHAR;
        Attribute a1 = teacher.addAttribute(attributeName, dataType, true, AttributeType.Mandatory);
        assertNotNull(a1);

        Attribute attribute = Attribute.queryByID(a1.getID());
        assertEquals(attribute.getName(), attributeName);
        assertEquals(attribute.getDataType(), dataType);
        assertEquals(attribute.getIsPrimary(), true);
        assertEquals(attribute.getAttributeType(), AttributeType.Mandatory);
        assertEquals(attribute.getBelongObjID(), teacher.getID());
        assertEquals(attribute.getBelongObjType(), BelongObjType.ENTITY);
        assertEquals(attribute.getAimPort(), Integer.valueOf(-1));
        assertNull(attribute.getLayoutInfo());

        a1.updateAimPort(1);
        attribute = Attribute.queryByID(a1.getID());
        assertEquals(attribute.getAimPort(), Integer.valueOf(1));

        a1.updateLayoutInfo(1.2, 1.3);
        attribute = Attribute.queryByID(a1.getID());
        assertNotNull(attribute.getLayoutInfo());
        assertEquals(attribute.getLayoutInfo().getLayoutX(), Double.valueOf(1.2));
        assertEquals(attribute.getLayoutInfo().getLayoutY(), Double.valueOf(1.3));

        Attribute a2 = teacher.addAttribute("phone", dataType, false, AttributeType.Optional);
        assertEquals(a2.getAttributeType(), AttributeType.Optional);
        a2.updateInfo(null, null, null, AttributeType.Both);
        attribute = Attribute.queryByID(a2.getID());
        assertEquals(attribute.getAttributeType(), AttributeType.Both);

        // duplicate name exception test
        assertThrows(ERException.class, () -> teacher.addAttribute(attributeName, DataType.INT, false, AttributeType.Mandatory));
    }

    @Test
    public void relationshipAddAttributeTest() {
        Relationship relationship = testSchema.createRelationship("test", teacher, student, Cardinality.OneToMany, Cardinality.ZeroToMany);
        String attributeName = "teacher_id";
        DataType dataType = DataType.VARCHAR;
        Attribute a1 = relationship.addAttribute(attributeName, dataType, AttributeType.Mandatory);
        assertNotNull(a1);

        Attribute attribute = Attribute.queryByID(a1.getID());
        assertEquals(attribute.getName(), attributeName);
        assertEquals(attribute.getDataType(), dataType);
        assertEquals(attribute.getIsPrimary(), false);
        assertEquals(attribute.getAttributeType(), AttributeType.Mandatory);
        assertEquals(attribute.getBelongObjID(), relationship.getID());
        assertEquals(attribute.getBelongObjType(), BelongObjType.RELATIONSHIP);
        assertEquals(attribute.getAimPort(), Integer.valueOf(-1));
        assertNull(attribute.getLayoutInfo());

        a1.updateAimPort(1);
        attribute = Attribute.queryByID(a1.getID());
        assertEquals(attribute.getAimPort(), Integer.valueOf(1));

        a1.updateLayoutInfo(1.2, 1.3);
        attribute = Attribute.queryByID(a1.getID());
        assertNotNull(attribute.getLayoutInfo());
        assertEquals(attribute.getLayoutInfo().getLayoutX(), Double.valueOf(1.2));
        assertEquals(attribute.getLayoutInfo().getLayoutY(), Double.valueOf(1.3));

        // duplicate name exception test
        assertThrows(ERException.class, () -> relationship.addAttribute(attributeName, DataType.INT, AttributeType.Mandatory));
    }

    @Test
    public void updateTest() {
        Attribute a1 = teacher.addAttribute("teacher_id", DataType.VARCHAR, false, AttributeType.Mandatory);

        String newName = "new_teacher_id";
        DataType newDataType = DataType.BIGINT;
        a1.updateInfo(newName, newDataType, false, AttributeType.Both);
        Attribute attribute = Attribute.queryByID(a1.getID());
        assertEquals(attribute.getName(), newName);
        assertEquals(attribute.getDataType(), newDataType);
        assertEquals(attribute.getIsPrimary(), false);
        assertEquals(attribute.getAttributeType(), AttributeType.Both);

        // check update aimPort success
        a1.updateAimPort(1);
        attribute = Attribute.queryByID(a1.getID());
        assertEquals(attribute.getAimPort(), Integer.valueOf(1));

        // check update layout success
        a1.updateLayoutInfo(1.2, 1.3);
        attribute = Attribute.queryByID(a1.getID());
        assertNotNull(attribute.getLayoutInfo());
        assertEquals(attribute.getLayoutInfo().getLayoutX(), Double.valueOf(1.2));
        assertEquals(attribute.getLayoutInfo().getLayoutY(), Double.valueOf(1.3));

        // check update to duplicate name exception
        Attribute backup = teacher.addAttribute("backup", DataType.VARCHAR, false, AttributeType.Mandatory);
        assertThrows(ERException.class, () -> a1.updateInfo("backup", null, null, null));
    }

    @Test
    public void selectByIDTest() {
        Attribute a1 = teacher.addAttribute("teacher_id", DataType.VARCHAR, true, AttributeType.Mandatory);
        Attribute attribute = Attribute.queryByID(a1.getID());
        assertNotNull(attribute);
    }

    @Test
    public void deleteByIDTest() {
        Attribute a1 = teacher.addAttribute("teacher_id", DataType.VARCHAR, true, AttributeType.Mandatory);

        // delete
        teacher.deleteAttribute(a1);

        assertThrows(ERException.class, () -> Attribute.queryByID(a1.getID()));
    }
}
