package com.ic.er;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestER {

    @Before
    public void setUp() {
//        ER.connectDB();
    }
    @Test
    public void createViewTest() {
        View testView = ER.createView("testView", "wt22");
        Assert.assertEquals(ER.queryAll().size(), 1);
    }

    @Test
    public void deleteViewTest() {
        View testView = ER.createView("testView", "wt22");
        Assert.assertEquals(ER.queryAll().size(), 1);
        ER.deleteView(testView);
        Assert.assertEquals(ER.queryAll().size(), 0);
    }


    public void jsonTest() {
        View testView = ER.createView("testView", "wt22");

        View firstView = ER.createView("first view", "tw");

        Entity teacher = firstView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        teacher.addAttribute("name", DataType.VARCHAR, 0, 0);
        teacher.addAttribute("age", DataType.INTEGER, 0, 0);

        Entity student = firstView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, 1, 0);
        student.addAttribute("name", DataType.VARCHAR, 0, 0);
        student.addAttribute("grade", DataType.INTEGER, 0, 0);

        Relationship ts = firstView.createRelationship("teaches", teacher.getID(), student.getID(), Cardinality.OneToMany);

        System.out.println(firstView);
    }

}
