package com.ic.er;

import com.ic.er.entity.AttributeDO;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class TestAttribute {

    private View testView;
    private Entity testEntity;

    @Before
    public void init() throws Exception {
        ER.connectDB();
        ER.createTables();
        testView = ER.createView("testView", "wt22");
        testEntity = testView.addEntity("teacher");
    }

    @Test
    public void addAttributeTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        Attribute a2 = testEntity.addAttribute("name", DataType.VARCHAR, 0, 0);
        Attribute a3 = testEntity.addAttribute("age", DataType.INTEGER, 0, 0);
        System.out.printf("a1 ID: %d\n", a1.getID());
        System.out.printf("a2 ID: %d\n", a2.getID());
        System.out.printf("a3 ID: %d\n", a3.getID());
    }
    @Test
    public void updateTest() {
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);

        String newName = "new_teacher_id";
        a1.setName(newName);
        a1.updateDB();

        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(a1.getID()));
        Assert.assertEquals(attributeList.size(), 1);
        Assert.assertEquals(attributeList.get(0).getName(), newName);
    }
    @Test
    public void selectByIdTest(){
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        AttributeDO aDo = ER.attributeMapper.selectByID(a1.getID());
        Assert.assertNotNull(aDo);
    }
    @Test
    public void deleteByIdTest(){
        Attribute a1 = testEntity.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);

        // delete
        a1.deleteDB();

        Attribute attribute = Attribute.queryByID(a1.getID());
        Assert.assertNull(attribute);
    }
}
