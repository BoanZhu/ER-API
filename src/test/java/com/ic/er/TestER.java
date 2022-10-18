package com.ic.er;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import org.junit.Before;
import org.junit.Test;

public class TestER {

    @Before
    public void setUp() {
//        ER.connectDB();
    }

    @Test
    public void testCreateViewEntityRelationship() throws JsonProcessingException {
        View firstView = View.createView("first view", "tw");
        Entity teacher = firstView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        teacher.addAttribute("name", DataType.VARCHAR, 0, 0);
        teacher.addAttribute("age", DataType.INTEGER, 0, 0);
        Entity student = firstView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, 1, 0);
        student.addAttribute("name", DataType.VARCHAR, 0, 0);
        student.addAttribute("grade", DataType.INTEGER, 0, 0);
        Relationship ts = firstView.createRelationship("teaches", teacher.getID(), student.getID(), Cardinality.OneToMany);

        for (Entity entity : firstView.getEntityList()) {
            System.out.printf("entity, id: %d, name: %s, gmtCreate: %s\n", entity.getID(), entity.getName(), entity.getGmtCreate().toString());
        }
        for (Relationship relationship : firstView.getRelationshipList()) {
            System.out.printf("relationship, id: %d, name: %s\n", relationship.getID(), relationship.getName());
        }
        System.out.println(firstView.ToJSON());
    }

    @Test
    public void testCreateAttribute() {
        View firstView = View.createView("first view", "tw");
        Entity teacher = firstView.addEntity("teacher");
        teacher.addAttribute("name", DataType.VARCHAR, 0, 0);
        teacher.addAttribute("age", DataType.INTEGER, 0, 0);
        for (Attribute attribute : teacher.getAttributeList()) {
            System.out.printf("attribute ID: %d, entityID: %d, Name: %s\n", attribute.getID(), attribute.getEntityID(), attribute.getName());
        }
    }
}
