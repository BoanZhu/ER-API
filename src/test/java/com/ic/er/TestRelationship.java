package com.ic.er;

import com.ic.er.exception.ERException;
import com.ic.er.entity.RelationshipDO;
import com.ic.er.common.Cardinality;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class TestRelationship {

    private View testView;
    private Entity teacher;
    private Entity student;
    private Entity classroom;

    @Before
    public void init() throws Exception {
        ER.initialize(true);
        testView = ER.createView("testView", "wt22");
        teacher = testView.addEntity("teacher");
        student = testView.addEntity("student");
        classroom = testView.addEntity("classroom");
        Assert.assertNotNull(teacher);
        Assert.assertNotNull(student);
        Assert.assertNotNull(classroom);
    }

    @Test
    public void createRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(relationship.getID(), Long.valueOf(1L));

        assertThrows(ERException.class, () -> testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
        assertThrows(ERException.class, () -> testView.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
    }

    @Test(expected = ERException.class)
    public void deleteRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        relationship.deleteDB();
        Relationship relationship1 = Relationship.queryByID(1L);
    }

    @Test
    public void updateRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        String newName = "new name";
        Cardinality newCardi = Cardinality.OneToMany;

        relationship.updateInfo(newName, null, null, newCardi, newCardi);
        Relationship relationship1 = Relationship.queryByID(1L);
        Assert.assertNotNull(relationship1);
        Assert.assertEquals(relationship1.getName(), newName);
        Assert.assertEquals(relationship1.getFirstCardinality(), newCardi);

        Relationship teachClassroom = testView.createRelationship("teaches", teacher, classroom, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, teacher, student, newCardi, newCardi));
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        assertThrows(ERException.class, () -> testView.createRelationship("teaches2", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship1 = Relationship.queryByID(1L);
        Assert.assertNotNull(relationship1);
        List<Relationship> results = Relationship.queryByRelationship(new RelationshipDO(relationship.getFirstEntity().getID(), relationship.getSecondEntity().getID()));
        Assert.assertEquals(results.size(), 1);
    }
}
