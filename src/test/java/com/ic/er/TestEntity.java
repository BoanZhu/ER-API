package com.ic.er;


import com.ic.er.Exception.ERException;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class TestEntity {

    private View testView;

    @Before
    public void init() throws Exception {
        ER.connectDB();
        ER.createTables();
        testView = ER.createView("testView", "wt22");
    }


    @Test
    public void addEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));
    }

    @Test(expected = ERException.class)
    public void deleteEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.deleteDB();
        Assert.assertNull(Entity.queryByID(teacher.getID()));
    }

    @Test
    public void queryEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        Assert.assertNotNull(Entity.queryByID(teacher.getID()));
    }

    @Test
    public void updateEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.setName("new teacher name");
        teacher.update();

        Entity entity = Entity.queryByID(teacher.getID());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getName(), "new teacher name");
    }

    @Test(expected = ERException.class)
    public void attributeTest() {
        Entity teacher = testView.addEntity("teacher");
        Attribute teacherID = teacher.addAttribute("teacher_id", DataType.INTEGER, 1, 0);
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.setName("new teacher name");
        teacher.update();

        teacher.deleteAttribute(teacherID);
        Assert.assertEquals(teacher.getAttributeList().size(), 0);
        Assert.assertNull(Attribute.queryByID(teacherID.getID()));
    }
}