package io.github.MigadaTang;

import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.transform.ParserUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TestUtil {
    Schema view;

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void testGenRelationshipQueue() {
        view = ER.createSchema("testTransform1");
        List<Relationship> relationshipList = new ArrayList<>();
        Relationship r1 = view.createEmptyRelationship("1");
        Relationship r2 = view.createEmptyRelationship("2map1");
        Relationship r3 = view.createEmptyRelationship("3map1");
        Relationship r4 = view.createEmptyRelationship("4map2");

        r2.linkObj(r1, Cardinality.ZeroToMany);
        r3.linkObj(r1, Cardinality.ZeroToMany);
        r4.linkObj(r2, Cardinality.ZeroToMany);

        relationshipList.add(r1);
        relationshipList.add(r2);
        relationshipList.add(r3);
        relationshipList.add(r4);

        Queue<Relationship> relationshipQueue = ParserUtil.generateRelationshipTopologySeq(relationshipList);
        System.out.println(relationshipQueue);
    }


    @Test
    public void testGenRelationshipQueue2() {
        view = ER.createSchema("testTransform1");
        List<Relationship> relationshipList = new ArrayList<>();
        Relationship r1 = view.createEmptyRelationship("1map4");
        Relationship r2 = view.createEmptyRelationship("2");
        Relationship r3 = view.createEmptyRelationship("3map2");
        Relationship r4 = view.createEmptyRelationship("4map2");

        r1.linkObj(r4, Cardinality.ZeroToMany);
        r3.linkObj(r2, Cardinality.ZeroToMany);
        r4.linkObj(r2, Cardinality.ZeroToMany);

        relationshipList.add(r1);
        relationshipList.add(r2);
        relationshipList.add(r3);
        relationshipList.add(r4);

        Queue<Relationship> relationshipQueue = ParserUtil.generateRelationshipTopologySeq(relationshipList);
        System.out.println(relationshipQueue);
    }


    @Test
    public void testGenRelationshipQueueFail() {
        view = ER.createSchema("testTransform1");
        List<Relationship> relationshipList = new ArrayList<>();
        Relationship r1 = view.createEmptyRelationship("1map4");
        Relationship r2 = view.createEmptyRelationship("2");
        Relationship r3 = view.createEmptyRelationship("3map2");
        Relationship r4 = view.createEmptyRelationship("4map2");

        r1.linkObj(r2, Cardinality.ZeroToMany);
        r2.linkObj(r3, Cardinality.ZeroToMany);
        r3.linkObj(r4, Cardinality.ZeroToMany);
        r4.linkObj(r1, Cardinality.ZeroToMany);

        relationshipList.add(r1);
        relationshipList.add(r2);
        relationshipList.add(r3);
        relationshipList.add(r4);

        Queue<Relationship> relationshipQueue = ParserUtil.generateRelationshipTopologySeq(relationshipList);
        System.out.println(relationshipQueue);
        Assert.assertEquals(0, relationshipQueue.size());
    }

    @Test
    public void testGenRelationshipQueueFail2() {
        view = ER.createSchema("testTransform1");
        List<Relationship> relationshipList = new ArrayList<>();
        Relationship r1 = view.createEmptyRelationship("1map4");
        Relationship r2 = view.createEmptyRelationship("2");
        Relationship r3 = view.createEmptyRelationship("3map2");
        Relationship r4 = view.createEmptyRelationship("4map2");

        r2.linkObj(r3, Cardinality.ZeroToMany);
        r3.linkObj(r4, Cardinality.ZeroToMany);
        r4.linkObj(r2, Cardinality.ZeroToMany);
        r4.linkObj(r1, Cardinality.ZeroToMany);

        relationshipList.add(r1);
        relationshipList.add(r2);
        relationshipList.add(r3);
        relationshipList.add(r4);

        Queue<Relationship> relationshipQueue = ParserUtil.generateRelationshipTopologySeq(relationshipList);
        System.out.println(relationshipQueue);
        Assert.assertNotEquals(4, relationshipQueue.size());
    }
}
