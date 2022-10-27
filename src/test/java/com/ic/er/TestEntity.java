package com.ic.er;


import com.ic.er.Exception.ERException;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEntity {

    private View testView;

    @Before
    public void init() throws Exception {
        ER.connectDB(true);
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

        teacher.updateInfo("new teacher name");

        Entity entity = Entity.queryByID(teacher.getID());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getName(), "new teacher name");
    }

    @Test(expected = ERException.class)
    public void attributeTest() {
        Entity teacher = testView.addEntity("teacher");
        Attribute teacherID = teacher.addAttribute("teacher_id", DataType.INT, 1);
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.updateInfo("new teacher name");

        teacher.deleteAttribute(teacherID);
        Assert.assertEquals(teacher.getAttributeList().size(), 0);
        Assert.assertNull(Attribute.queryByID(teacherID.getID()));
    }
}