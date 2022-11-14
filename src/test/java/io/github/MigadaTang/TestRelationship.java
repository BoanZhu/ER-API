package io.github.MigadaTang;

import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.entity.RelationshipDO;
import io.github.MigadaTang.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        relationship.deleteDB();

        assertThrows(ERException.class, () -> Relationship.queryByID(relationship.getID()));
        for (RelationshipEdge edge : relationship.getEdgeList()) {
            assertThrows(ERException.class, () -> RelationshipEdge.queryByID(edge.getID()));
        }
    }


    @Test
    public void updateRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        String newName = "new name";

        relationship.updateInfo(newName);
        relationship = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship);
        Assert.assertEquals(relationship.getName(), newName);

        // update edge test
        RelationshipEdge edge = relationship.getEdgeList().get(0);
        Cardinality cardi = Cardinality.OneToOne;
        Entity newEntity = testSchema.addEntity("new entity");
        edge.updateInfo(cardi, newEntity);
        edge = RelationshipEdge.queryByID(edge.getID());
        Assert.assertEquals(edge.getCardinality(), cardi);
        Assert.assertEquals(edge.getEntity().getID(), newEntity.getID());
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
        List<Relationship> results = Relationship.query(new RelationshipDO("teaches", testSchema.getID()));
        Assert.assertEquals(results.size(), 1);
    }
}
