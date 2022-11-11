package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestRelationship {

    private Schema testSchema;
    private Schema secondSchema;
    private Entity teacher;
    private Entity student;
    private Entity classroom;
    private Entity secondSchemaEntity1;
    private Entity secondSchemaEntity2;

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
        testSchema = ER.createSchema("testSchema", "wt22");
        teacher = testSchema.addEntity("teacher");
        student = testSchema.addEntity("student");
        classroom = testSchema.addEntity("classroom");

        secondSchema = ER.createSchema("secondSchema", "wt22");
        secondSchemaEntity1 = secondSchema.addEntity("ent1");
        secondSchemaEntity2 = secondSchema.addEntity("ent2");
        Assert.assertNotNull(teacher);
        Assert.assertNotNull(student);
        Assert.assertNotNull(classroom);
    }

    @Test
    public void createRelationshipTest() {
        // check does not belong to this schema
        assertThrows(ERException.class, () -> secondSchema.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        RelationshipEdge edge = relationship.linkEntity(classroom, Cardinality.ZeroToMany);
        edge = RelationshipEdge.queryByID(edge.getID());
        Assert.assertNotNull(edge);
        Assert.assertEquals(edge.getEntity().getID(), classroom.getID());
        Assert.assertEquals(edge.getRelationshipID(), relationship.getID());

        // check edge num equal
        Relationship queryRelationship = Relationship.queryByID(relationship.getID());
        Assert.assertEquals(queryRelationship.getEdgeList().size(), 3);

        // check duplicate link entity
        assertThrows(ERException.class, () -> relationship.linkEntity(classroom, Cardinality.ZeroToMany));
        // todo finish check duplicate
//        assertThrows(ERException.class, () -> testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
    }

    @Test
    public void deleteRelationshipTest() {
        Relationship testEntityCascadeDelete = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(testEntityCascadeDelete);
        testSchema.deleteEntity(teacher);
        assertThrows(ERException.class, () -> Entity.queryByID(teacher.getID()));
        assertThrows(ERException.class, () -> Relationship.queryByID(testEntityCascadeDelete.getID()));

        teacher = testSchema.addEntity("teacher");
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        relationship.deleteDB();
        assertThrows(ERException.class, () -> Relationship.queryByID(relationship.getID()));
    }

    public void updateRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        String newName = "new name";
        Cardinality newCardi = Cardinality.OneToMany;

//        relationship.updateInfo(newName, null, null, newCardi, newCardi);
        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
        Assert.assertEquals(relationship1.getName(), newName);
//        Assert.assertEquals(relationship1.getFirstCardinality(), newCardi);

        Relationship teachClassroom = testSchema.createRelationship("teaches", teacher, classroom, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
//        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, teacher, student, newCardi, newCardi));
//        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, secondSchemaEntity1, secondSchemaEntity2, newCardi, newCardi));
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
//        List<Relationship> results = Relationship.queryByRelationship(new RelationshipDO(relationship.getFirstEntity().getID(), relationship.getSecondEntity().getID()));
//        Assert.assertEquals(results.size(), 1);
    }
}
