package com.ic.er;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

public class TestER {

    @Before
    public void setUp() throws Exception {
        ER.connectDB();
        ER.createTables();
    }
    @Test
    public void createViewTest() {
        View testView = ER.createView("testView", "wt22");
        Assert.assertEquals(ER.queryAllView().size(), 1);
    }

    @Test
    public void deleteViewTest() {
        View testView = ER.createView("testView", "wt22");
        Assert.assertEquals(ER.queryAllView().size(), 1);
        ER.deleteView(testView);
        Assert.assertEquals(ER.queryAllView().size(), 0);
    }

    @Test
    public void jsonTest() throws JsonProcessingException, FileNotFoundException {
        View firstView = ER.createView("first view", "tw");

        Entity teacher = firstView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        teacher.addAttribute("name", DataType.VARCHAR, 0, 0);
        teacher.addAttribute("age", DataType.INTEGER, 0, 0);

        Entity student = firstView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, 1, 0);
        student.addAttribute("name", DataType.VARCHAR, 0, 0);
        student.addAttribute("grade", DataType.INTEGER, 0, 0);

        Relationship ts = firstView.createRelationship("teaches", teacher, student, Cardinality.OneToMany);

        firstView.ToJSONFile();
    }

}
