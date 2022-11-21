package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAttribute {

    private Schema testSchema;
    private Entity teacher;
    private Entity student;

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
        testSchema = ER.createSchema("testSchema", "wt22");
        teacher = testSchema.addEntity("teacher");
        student = testSchema.addEntity("student");
    }

    @Test
    public void addAttributeTest() {
        // primary key cannot be nullable
        assertThrows(ERException.class, () -> teacher.addAttribute("testPrimaryWithNullable", DataType.INT, true, AttributeType.Mandatory));

        String attributeName = "teacher_id";
        DataType dataType = DataType.VARCHAR;
        Attribute a1 = teacher.addAttribute(attributeName, dataType, true, AttributeType.Mandatory);
        Assert.assertNotNull(a1);

        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertEquals(attribute.getName(), attributeName);
        Assert.assertEquals(attribute.getDataType(), dataType);
        Assert.assertEquals(attribute.getIsPrimary(), true);
        Assert.assertEquals(attribute.getAttributeType(), false);
        Assert.assertEquals(attribute.getBelongObjID(), teacher.getID());
        Assert.assertEquals(attribute.getBelongObjType(), BelongObjType.ENTITY);
        Assert.assertEquals(attribute.getAimPort(), Integer.valueOf(-1));
        Assert.assertNull(attribute.getLayoutInfo());

        a1.updateAimPort(1);
        attribute = Attribute.queryByID(a1.getID());
        Assert.assertEquals(attribute.getAimPort(), Integer.valueOf(1));

        a1.updateLayoutInfo(1.2, 1.3);
        attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute.getLayoutInfo());
        Assert.assertEquals(attribute.getLayoutInfo().getLayoutX(), Double.valueOf(1.2));
        Assert.assertEquals(attribute.getLayoutInfo().getLayoutY(), Double.valueOf(1.3));

        // duplicate name exception test
        assertThrows(ERException.class, () -> teacher.addAttribute(attributeName, DataType.INT, false, AttributeType.Mandatory));
    }

    @Test
    public void relationshipAddAttributeTest() {
        Relationship relationship = testSchema.createRelationship("test", teacher, student, Cardinality.OneToMany, Cardinality.ZeroToMany);
        String attributeName = "teacher_id";
        DataType dataType = DataType.VARCHAR;
        Attribute a1 = relationship.addAttribute(attributeName, dataType, AttributeType.Mandatory);
        Assert.assertNotNull(a1);

        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertEquals(attribute.getName(), attributeName);
        Assert.assertEquals(attribute.getDataType(), dataType);
        Assert.assertEquals(attribute.getIsPrimary(), false);
        Assert.assertEquals(attribute.getAttributeType(), false);
        Assert.assertEquals(attribute.getBelongObjID(), relationship.getID());
        Assert.assertEquals(attribute.getBelongObjType(), BelongObjType.RELATIONSHIP);
        Assert.assertEquals(attribute.getAimPort(), Integer.valueOf(-1));
        Assert.assertNull(attribute.getLayoutInfo());

        a1.updateAimPort(1);
        attribute = Attribute.queryByID(a1.getID());
        Assert.assertEquals(attribute.getAimPort(), Integer.valueOf(1));

        a1.updateLayoutInfo(1.2, 1.3);
        attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute.getLayoutInfo());
        Assert.assertEquals(attribute.getLayoutInfo().getLayoutX(), Double.valueOf(1.2));
        Assert.assertEquals(attribute.getLayoutInfo().getLayoutY(), Double.valueOf(1.3));

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
        Assert.assertEquals(attribute.getName(), newName);
        Assert.assertEquals(attribute.getDataType(), newDataType);
        Assert.assertEquals(attribute.getIsPrimary(), false);
        Assert.assertEquals(attribute.getAttributeType(), AttributeType.Both);

        // check update aimPort success
        a1.updateAimPort(1);
        attribute = Attribute.queryByID(a1.getID());
        Assert.assertEquals(attribute.getAimPort(), Integer.valueOf(1));

        // check update layout success
        a1.updateLayoutInfo(1.2, 1.3);
        attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute.getLayoutInfo());
        Assert.assertEquals(attribute.getLayoutInfo().getLayoutX(), Double.valueOf(1.2));
        Assert.assertEquals(attribute.getLayoutInfo().getLayoutY(), Double.valueOf(1.3));

        // check update to duplicate name exception
        Attribute backup = teacher.addAttribute("backup", DataType.VARCHAR, false, AttributeType.Mandatory);
        assertThrows(ERException.class, () -> a1.updateInfo("backup", null, null, null));
    }

    @Test
    public void selectByIDTest() {
        Attribute a1 = teacher.addAttribute("teacher_id", DataType.VARCHAR, true, AttributeType.Mandatory);
        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute);
    }

    @Test
    public void deleteByIDTest() {
        Attribute a1 = teacher.addAttribute("teacher_id", DataType.VARCHAR, true, AttributeType.Mandatory);

        // delete
        teacher.deleteAttribute(a1);

        assertThrows(ERException.class, () -> Attribute.queryByID(a1.getID()));
    }
}
