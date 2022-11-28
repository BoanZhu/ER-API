package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.entity.RelationshipDO;
import io.github.MigadaTang.exception.ERException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertThrows;

public class TestRelationship {
    private Schema testSchema;
    private Schema secondSchema;
    private Entity teacher;
    private Entity student;
    private Entity classroom;

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }


    @Before
    public void initializeSchema() throws Exception {
        testSchema = ER.createSchema("testSchema");
        teacher = testSchema.addEntity("teacher");
        student = testSchema.addEntity("student");
        classroom = testSchema.addEntity("classroom");

        secondSchema = ER.createSchema("secondSchema");
        assertNotNull(teacher);
        assertNotNull(student);
        assertNotNull(classroom);
    }

    @Test
    public void createRelationshipTest() {
        // check does not belong to this schema
        assertThrows(ERException.class, () -> secondSchema.createRelationship("teaches", student, teacher, Cardinality.ZeroToMany, Cardinality.ZeroToMany));

        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertNotNull(relationship);

        Relationship emptyRelationship = testSchema.createEmptyRelationship("empty relationship");
        RelationshipEdge link = relationship.linkObj(emptyRelationship, Cardinality.ZeroToMany);
        assertNotNull(link);
        assertEquals(link.getConnObj().getID(), emptyRelationship.getID());
        assertEquals(link.getRelationshipID(), relationship.getID());

        RelationshipEdge edge = relationship.linkObj(classroom, Cardinality.ZeroToMany);
        edge = RelationshipEdge.queryByID(edge.getID());
        assertNotNull(edge);
        assertEquals(edge.getConnObj().getID(), classroom.getID());
        assertEquals(edge.getRelationshipID(), relationship.getID());

        // check edge num equal
        Relationship queryRelationship = Relationship.queryByID(relationship.getID());
        assertEquals(queryRelationship.getEdgeList().size(), 4);

        // check duplicate link entity
        assertThrows(ERException.class, () -> relationship.linkObj(classroom, Cardinality.ZeroToMany, false));
    }

    @Test
    public void deleteRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertNotNull(relationship);
        testSchema.deleteRelationship(relationship);

        assertThrows(ERException.class, () -> Relationship.queryByID(relationship.getID()));
        for (RelationshipEdge edge : relationship.getEdgeList()) {
            assertThrows(ERException.class, () -> RelationshipEdge.queryByID(edge.getID()));
        }
    }


    @Test
    public void updateRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        Relationship tc = testSchema.createRelationship("T-C", teacher, classroom, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertNotNull(relationship);

        String newName = "new name";

        relationship.updateInfo(newName);
        relationship = Relationship.queryByID(relationship.getID());
        assertNotNull(relationship);
        assertEquals(relationship.getName(), newName);

        relationship.updateLayoutInfo(1.5, 2.5);
        relationship = Relationship.queryByID(relationship.getID());
        assertEquals(relationship.getLayoutInfo().getLayoutX(), Double.valueOf(1.5));
        assertEquals(relationship.getLayoutInfo().getLayoutY(), Double.valueOf(2.5));


        // update edge test
        RelationshipEdge edge = relationship.getEdgeList().get(0);
        Cardinality cardi = Cardinality.OneToOne;
        Entity newEntity = testSchema.addEntity("new entity");
        edge.updateInfo(cardi, newEntity, true);
        edge = RelationshipEdge.queryByID(edge.getID());
        assertEquals(edge.getCardinality(), cardi);
        assertEquals(edge.getConnObj().getID(), newEntity.getID());
        assertEquals(edge.getIsKey(), Boolean.TRUE);

        // update belong obj id to relationship id
        edge.updateInfo(cardi, tc, true);
        edge = RelationshipEdge.queryByID(edge.getID());
        assertEquals(edge.getConnObj().getID(), tc.getID());
        assertEquals(edge.getConnObjType(), BelongObjType.RELATIONSHIP);

        // update belong obj id to entity id
        edge.updateInfo(cardi, newEntity, true);
        edge = RelationshipEdge.queryByID(edge.getID());
        assertEquals(edge.getConnObj().getID(), newEntity.getID());
        assertEquals(edge.getConnObjType(), BelongObjType.ENTITY);

        // update relationshipID
        edge.migrateToAnotherRelationship(tc.getID());
        edge = RelationshipEdge.queryByID(edge.getID());
        assertEquals(edge.getRelationshipID(), tc.getID());
    }

    @Test
    public void queryRelationshipTest() {
        Relationship relationship = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
        assertNotNull(relationship);

        Relationship relationship1 = Relationship.queryByID(relationship.getID());
        assertNotNull(relationship1);
        List<Relationship> results = Relationship.query(new RelationshipDO("teaches", testSchema.getID()));
        assertEquals(results.size(), 1);
    }
}
