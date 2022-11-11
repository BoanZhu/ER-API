package io.github.MigadaTang;


import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.exception.ERException;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
        ImmutablePair<Entity, Relationship> pair = testSchema.addWeakEntity("card", teacher, "swipe", Cardinality.OneToMany, Cardinality.OneToMany);
        Entity card = Entity.queryByID(pair.left.getID());
        Relationship relationship = Relationship.queryByID(pair.right.getID());
        Assert.assertNotEquals(card.getID(), Long.valueOf(0));
        Assert.assertEquals(card.getBelongStrongEntityID(), teacher.getID());
        Assert.assertEquals(card.getEntityType(), EntityType.WEAK);
        Assert.assertNotEquals(relationship.getID(), Long.valueOf(0));
        Assert.assertEquals(relationship.getEdgeList().size(), 2);
    }

    @Test
    public void deleteEntityTest() {
        Entity teacher = testSchema.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        Entity seniorTeacher = testSchema.addSubset("senior_teacher", teacher);
        seniorTeacher.deleteDB();
        assertThrows(ERException.class, () -> Entity.queryByID(seniorTeacher.getID()));

        ImmutablePair<Entity, Relationship> pair = testSchema.addWeakEntity("card", teacher, "swipe", Cardinality.OneToMany, Cardinality.OneToMany);
        pair.left.deleteDB();
        assertThrows(ERException.class, () -> Entity.queryByID(pair.left.getID()));
        assertThrows(ERException.class, () -> Relationship.queryByID(pair.right.getID()));
        assertThrows(ERException.class, () -> Relationship.queryByID(pair.right.getEdgeList().get(0).getID()));

        teacher.deleteDB();
        assertThrows(ERException.class, () -> Entity.queryByID(teacher.getID()));
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

        teacher.updateLayoutInfo(1.5, 2.5);
        entity = Entity.queryByID(teacher.getID());
        Assert.assertEquals(entity.getLayoutInfo().getLayoutX(), Double.valueOf(1.5));
        Assert.assertEquals(entity.getLayoutInfo().getLayoutY(), Double.valueOf(2.5));

        teacher.updateAimPort(3);
        entity = Entity.queryByID(teacher.getID());
        Assert.assertEquals(entity.getAimPort(), Integer.valueOf(3));

        // test duplicate entity exception
        assertThrows(ERException.class, () -> teacher.updateInfo("student"));
    }

    @Test
    public void queryEntityTest() {
        Entity teacher = testSchema.addEntity("teacher");
        Assert.assertNotEquals(teacher.getID(), Long.valueOf(0));

        Assert.assertNotNull(Entity.queryByID(teacher.getID()));
    }
}