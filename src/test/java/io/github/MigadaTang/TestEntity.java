package io.github.MigadaTang;


import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEntity {

    private Schema testSchema;

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
        testSchema = ER.createSchema("testSchema", "wt22");
    }


    @Test
    public void addEntityTest() {
        // normal add strong entity
        Entity teacher = testSchema.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));
        // check duplication exception
        assertThrows(ERException.class, () -> testSchema.addEntity("teacher"));

        // add subset
        Entity seniorTeacher = testSchema.addSubset("senior_teacher", teacher);
        seniorTeacher = Entity.queryByID(seniorTeacher.getID());
        Assert.assertNotEquals(seniorTeacher.getID(), Long.valueOf(0));
        Assert.assertEquals(seniorTeacher.getBelongStrongEntityID(), teacher.getID());
        Assert.assertEquals(seniorTeacher.getEntityType(), EntityType.SUBSET);

        // add weak entity
    }

    @Test
    public void deleteEntityTest() {
        Entity teacher = testSchema.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.deleteDB();
        assertThrows(ERException.class, () -> Entity.queryByID(teacher.getID()));
    }

    @Test
    public void queryEntityTest() {
        Entity teacher = testSchema.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        Assert.assertNotNull(Entity.queryByID(teacher.getID()));
    }

    @Test
    public void updateEntityTest() {
        Entity teacher = testSchema.addEntity("teacher");
        Entity student = testSchema.addEntity("student");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.updateInfo("new teacher name");
        Entity entity = Entity.queryByID(teacher.getID());
        Assert.assertNotNull(entity);
        Assert.assertEquals(entity.getName(), "new teacher name");

        assertThrows(ERException.class, () -> teacher.updateInfo("student"));
    }

    @Test
    public void attributeTest() {
        Entity teacher = testSchema.addEntity("teacher");
        Attribute teacherID = teacher.addAttribute("teacher_id", DataType.INT, true, false);
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        teacher.updateInfo("new teacher name");

        teacher.deleteAttribute(teacherID);
        Assert.assertEquals(teacher.getAttributeList().size(), 0);
        assertThrows(ERException.class, () -> Attribute.queryByID(teacherID.getID()));
    }
}