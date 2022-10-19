package com.ic.er;

import com.ic.er.bean.entity.RelationshipDO;
import com.ic.er.common.Cardinality;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class TestRelationship {

    private View testView;
    private Entity teacher;
    private Entity student;

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
        teacher = testView.addEntity("teacher");
        student = testView.addEntity("student");
        Assert.assertNotNull(teacher);
        Assert.assertNotNull(student);
    }

    @Test
    public void createRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher.getID(), student.getID(), Cardinality.OneToMany);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(relationship.getID(), Long.valueOf(1L));
    }

    @Test
    public void deleteRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher.getID(), student.getID(), Cardinality.OneToMany);
        Assert.assertNotNull(relationship);
        relationship.deleteDB();
        Relationship relationship1 = Relationship.queryByID(1L);
        Assert.assertNull(relationship1);
    }

    @Test
    public void updateRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher.getID(), student.getID(), Cardinality.OneToMany);
        Assert.assertNotNull(relationship);

        String newName = "new name";
        Cardinality newCardi = Cardinality.ManyToMany;

        relationship.setName(newName);
        relationship.setCardinality(newCardi);
        relationship.updateDB();
        Relationship relationship1 = Relationship.queryByID(1L);
        Assert.assertNotNull(relationship1);
        Assert.assertEquals(relationship1.getName(), newName);
        Assert.assertEquals(relationship1.getCardinality(), newCardi);
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher.getID(), student.getID(), Cardinality.OneToMany);
        Relationship relationship2 = testView.createRelationship("teaches2", teacher.getID(), student.getID(), Cardinality.OneToMany);
        Assert.assertNotNull(relationship);
        Assert.assertNotNull(relationship2);

        Relationship relationship1 = Relationship.queryByID(1L);
        Assert.assertNotNull(relationship1);
        List<Relationship> results = Relationship.queryByRelationship(new RelationshipDO(relationship.getFirstEntityID(), relationship.getSecondEntityID()));
        Assert.assertEquals(results.size(), 2);
    }
}
