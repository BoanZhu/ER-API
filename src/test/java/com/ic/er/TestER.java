package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

public class TestER {

    @Before
    public void setUp() throws Exception {
        ER.initialize(true);
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
    public void jsonTest() throws IOException {
        View firstView = ER.createView("first view", "tw");

        Entity teacher = firstView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, true);
        teacher.addAttribute("name", DataType.VARCHAR, false);
        teacher.addAttribute("age", DataType.INT, false);

        Entity student = firstView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, true);
        student.addAttribute("name", DataType.VARCHAR, false);
        student.addAttribute("grade", DataType.INT, false);

        Relationship ts = firstView.createRelationship("teaches", teacher, student, Cardinality.OneToMany, Cardinality.OneToMany);

        String jsonString = firstView.ToJSON();
        FileWriter myWriter = new FileWriter("first view.json");
        myWriter.write(jsonString);
        myWriter.close();

        View view = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(view);
    }

    @Test
    public void getCardi() {
        System.out.println(Cardinality.getFromValue("1:N"));
    }
}
