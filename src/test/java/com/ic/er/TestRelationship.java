package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.entity.RelationshipDO;
import com.ic.er.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestRelationship {

    private View testView;
    private View secondView;
    private Entity teacher;
    private Entity student;
    private Entity classroom;
    private Entity secondViewEntity1;
    private Entity secondViewEntity2;

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
        testView = ER.createView("testView", "wt22");
        secondView = ER.createView("secondView", "wt22");
        teacher = testView.addEntity("teacher");
        student = testView.addEntity("student");
        classroom = testView.addEntity("classroom");
        secondViewEntity1 = secondView.addEntity("ent1");
        secondViewEntity2 = secondView.addEntity("ent2");
        Assert.assertNotNull(teacher);
        Assert.assertNotNull(student);
        Assert.assertNotNull(classroom);
    }

    @Test
    public void createRelationshipTest() {
        assertThrows(ERException.class, () -> secondView.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        assertThrows(ERException.class, () -> testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
        assertThrows(ERException.class, () -> testView.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
    }

    @Test
    public void deleteRelationshipTest() {
        Relationship testEntityCascadeDelete = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(testEntityCascadeDelete);
        testView.deleteEntity(teacher);
        assertThrows(ERException.class, () -> Entity.queryByID(teacher.getID()));
        assertThrows(ERException.class, () -> Relationship.queryByID(testEntityCascadeDelete.getID()));

        teacher = testView.addEntity("teacher");
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        relationship.deleteDB();
        assertThrows(ERException.class, () -> Relationship.queryByID(relationship.getID()));
    }

    @Test
    public void updateRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        String newName = "new name";
        Cardinality newCardi = Cardinality.OneToMany;

        relationship.updateInfo(newName, null, null, newCardi, newCardi);
        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
        Assert.assertEquals(relationship1.getName(), newName);
        Assert.assertEquals(relationship1.getFirstCardinality(), newCardi);

        Relationship teachClassroom = testView.createRelationship("teaches", teacher, classroom, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, teacher, student, newCardi, newCardi));
        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, secondViewEntity1, secondViewEntity2, newCardi, newCardi));
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        assertThrows(ERException.class, () -> testView.createRelationship("teaches2", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
        List<Relationship> results = Relationship.queryByRelationship(new RelationshipDO(relationship.getFirstEntity().getID(), relationship.getSecondEntity().getID()));
        Assert.assertEquals(results.size(), 1);
    }
}
