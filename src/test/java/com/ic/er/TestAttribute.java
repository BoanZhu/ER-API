package com.ic.er;

import com.ic.er.common.DataType;
import com.ic.er.entity.AttributeDO;
import com.ic.er.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAttribute {

    private Schema testSchema;
    private Entity testEntity;

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
        testSchema = ER.createSchema("testSchema", "wt22");
        testEntity = testSchema.addEntity("teacher");
    }

    @Test
    public void addAttributeTest() {
        assertThrows(ERException.class, () -> testEntity.addAttribute("testPrimaryWithNullable", DataType.INT, true, true));

        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, true, false);
        Attribute a2 = testEntity.addAttribute("name", DataType.VARCHAR, false, false);
        Attribute a3 = testEntity.addAttribute("age", DataType.INT, false, true);
        Assert.assertNotNull(a1);
        Assert.assertNotNull(a2);
        Assert.assertNotNull(a3);

        assertThrows(ERException.class, () -> testEntity.addAttribute("age", DataType.INT, false, false));
        assertThrows(ERException.class, () -> testEntity.addAttribute("new primary", DataType.INT, true, false));
    }

    @Test
    public void updateTest() {
        Attribute backup = testEntity.addAttribute("backup", DataType.VARCHAR, true, false);
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, false, false);
        String newName = "new_teacher_id";
        a1.updateInfo(newName, null, null, null);
        a1.updateLayoutInfo(1.0, 2.0, 3.0, 4.0);

        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(a1.getID()));
        Assert.assertEquals(attributeList.size(), 1);
        Assert.assertEquals(attributeList.get(0).getName(), newName);
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getLayoutX(), Double.valueOf(1.0));
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getLayoutY(), Double.valueOf(2.0));
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getHeight(), Double.valueOf(3.0));
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getWidth(), Double.valueOf(4.0));

        assertThrows(ERException.class, () -> a1.updateInfo("backup", null, null, null));
        assertThrows(ERException.class, () -> a1.updateInfo(null, null, true, null));
        assertThrows(ERException.class, () -> backup.updateInfo(null, null, null, true));
    }

    @Test
    public void selectByIDTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, true, false);
        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute);
    }

    @Test
    public void deleteByIDTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, true, false);

        // delete
        a1.deleteDB();

        assertThrows(ERException.class, () -> Attribute.queryByID(a1.getID()));
    }
}
