package com.ic.er;

import com.ic.er.exception.ERException;
import com.ic.er.entity.AttributeDO;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class TestAttribute {

    private View testView;
    private Entity testEntity;

    @Before
    public void init() throws Exception {
        ER.initialize(true);
        testView = ER.createView("testView", "wt22");
        testEntity = testView.addEntity("teacher");
    }

    @Test
    public void addAttributeTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, true);
        Attribute a2 = testEntity.addAttribute("name", DataType.VARCHAR, false);
        Attribute a3 = testEntity.addAttribute("age", DataType.INT, false);
        Assert.assertNotNull(a1);
        Assert.assertNotNull(a2);
        Assert.assertNotNull(a3);

        assertThrows(ERException.class, () -> testEntity.addAttribute("age", DataType.INT, false));
        assertThrows(ERException.class, () -> testEntity.addAttribute("new primary", DataType.INT, true));
    }

    @Test
    public void updateTest() {
        Attribute backup = testEntity.addAttribute("backup", DataType.VARCHAR, true);
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, false);
        String newName = "new_teacher_id";
        a1.updateInfo(newName, null, null);
        a1.updateLayoutInfo(1.0, 2.0, 3.0, 4.0);

        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(a1.getID()));
        Assert.assertEquals(attributeList.size(), 1);
        Assert.assertEquals(attributeList.get(0).getName(), newName);
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getLayoutX(), Double.valueOf(1.0));
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getLayoutY(), Double.valueOf(2.0));
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getHeight(), Double.valueOf(3.0));
        Assert.assertEquals(attributeList.get(0).getLayoutInfo().getWidth(), Double.valueOf(4.0));

        assertThrows(ERException.class, () -> a1.updateInfo("backup", null, null));
        assertThrows(ERException.class, () -> a1.updateInfo(null, null, true));
    }

    @Test
    public void selectByIDTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, true);
        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute);
    }

    @Test(expected = ERException.class)
    public void deleteByIDTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, true);

        // delete
        a1.deleteDB();

        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNull(attribute);
    }
}
