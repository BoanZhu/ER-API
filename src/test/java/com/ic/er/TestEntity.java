package com.ic.er;


import com.ic.er.bean.entity.EntityDO;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestEntity {

    private View testView;

    @Before
    public void init() {
        try {
            ER.connectDB();
            ER.createTables();
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        testView = ER.createView("testView", "wt22");
    }


    @Test
    public void addEntityTest() {
        Entity teacher = testView.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));
    }

    @Test
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
        teacher.updateDB();

        Entity entity = Entity.queryByID(teacher.getID());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getName(), "new teacher name");
    }
}