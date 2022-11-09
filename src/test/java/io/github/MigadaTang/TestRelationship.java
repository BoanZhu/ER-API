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
        secondSchema = ER.createSchema("secondSchema", "wt22");
        teacher = testSchema.addEntity("teacher");
        student = testSchema.addEntity("student");
        classroom = testSchema.addEntity("classroom");
        secondSchemaEntity1 = secondSchema.addEntity("ent1");
        secondSchemaEntity2 = secondSchema.addEntity("ent2");
        Assert.assertNotNull(teacher);
        Assert.assertNotNull(student);
        Assert.assertNotNull(classroom);
    }

    @Test
    public void createRelationshipTest() {
        assertThrows(ERException.class, () -> secondSchema.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        assertThrows(ERException.class, () -> testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
        assertThrows(ERException.class, () -> testSchema.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));
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

    @Test
    public void updateRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);

        String newName = "new name";
        Cardinality newCardi = Cardinality.OneToMany;

        relationship.updateInfo(newName, null, null, newCardi, newCardi);
        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
        Assert.assertEquals(relationship1.getName(), newName);
        Assert.assertEquals(relationship1.getFirstCardinality(), newCardi);

        Relationship teachClassroom = testSchema.createRelationship("teaches", teacher, classroom, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, teacher, student, newCardi, newCardi));
        assertThrows(ERException.class, () -> teachClassroom.updateInfo(newName, secondSchemaEntity1, secondSchemaEntity2, newCardi, newCardi));
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Assert.assertNotNull(relationship);
        assertThrows(ERException.class, () -> testSchema.createRelationship("teaches2", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        Assert.assertNotNull(relationship1);
        List<Relationship> results = Relationship.queryByRelationship(new RelationshipDO(relationship.getFirstEntity().getID(), relationship.getSecondEntity().getID()));
        Assert.assertEquals(results.size(), 1);
    }
}
