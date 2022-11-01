package com.ic.er;


import com.ic.er.exception.ERException;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEntity {

    private View testView;

    @Before
    public void init() throws Exception {
        ER.initialize(true);
        testView = ER.createView("testView", "wt22");
    }


    @Test
    public void addEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));
        assertThrows(ERException.class, () -> testView.addEntity("teacher"));
    }

    @Test
    public void deleteEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.deleteDB();
        assertThrows(ERException.class, () -> Entity.queryByID(teacher.getID()));
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
        Entity student = testView.addEntity("student");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.updateInfo("new teacher name");
        Entity entity = Entity.queryByID(teacher.getID());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getName(), "new teacher name");

        assertThrows(ERException.class, () -> teacher.updateInfo("student"));
    }

    @Test(expected = ERException.class)
    public void attributeTest() {
        Entity teacher = testView.addEntity("teacher");
        Attribute teacherID = teacher.addAttribute("teacher_id", DataType.INT, true, false);
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.updateInfo("new teacher name");

        teacher.deleteAttribute(teacherID);
        Assert.assertEquals(teacher.getAttributeList().size(), 0);
        Assert.assertNull(Attribute.queryByID(teacherID.getID()));
    }
}