package com.ic.er;

import com.ic.er.Exception.ERException;
import com.ic.er.entity.RelationshipDO;
import com.ic.er.common.Cardinality;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestRelationship {

    private View testView;
    private Entity teacher;
    private Entity student;

    @Before
    public void init() throws Exception {
        ER.connectDB(true);
        testView = ER.createView("testView", "wt22");
        teacher = testView.addEntity("teacher");
        student = testView.addEntity("student");
        Assert.assertNotNull(teacher);
        Assert.assertNotNull(student);
    }

    @Test
    public void createRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        Assert.assertEquals(relationship.getID(), Long.valueOf(1L));
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
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Relationship relationship2 = testView.createRelationship("teaches2", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        Assert.assertNotNull(relationship2);

        Relationship relationship1 = Relationship.queryByID(1L);
        Assert.assertNotNull(relationship1);
        List<Relationship> results = Relationship.queryByRelationship(new RelationshipDO(relationship.getFirstEntity().getID(), relationship.getSecondEntity().getID()));
        Assert.assertEquals(results.size(), 2);
    }
}
