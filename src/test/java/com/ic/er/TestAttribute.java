package com.ic.er;

import com.ic.er.Exception.ERException;
import com.ic.er.entity.AttributeDO;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestAttribute {

    private View testView;
    private Entity testEntity;

    @Before
    public void init() throws Exception {
        ER.connectDB(true);
        testView = ER.createView("testView", "wt22");
        testEntity = testView.addEntity("teacher");
    }

    @Test
    public void addAttributeTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1);
        Attribute a2 = testEntity.addAttribute("name", DataType.VARCHAR, 0);
        Attribute a3 = testEntity.addAttribute("age", DataType.INT, 0);
        System.out.printf("a1 ID: %d\n", a1.getID());
        System.out.printf("a2 ID: %d\n", a2.getID());
        System.out.printf("a3 ID: %d\n", a3.getID());
    }

    @Test
    public void updateTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1);
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
    }

    @Test
    public void selectByIDTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1);
        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNotNull(attribute);
    }

    @Test(expected = ERException.class)
    public void deleteByIDTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1);

        // delete
        a1.deleteDB();

        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNull(attribute);
    }
}
