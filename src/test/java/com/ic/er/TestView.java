package com.ic.er;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ic.er.Exception.ERException;
import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class TestView {

    @Before
    public void init() throws Exception {
        ER.connectDB();
        ER.createTables();
    }

    @Test
    public void createViewTest() {
        View testView = ER.createView("testView", "wt22");

        Entity teacher = testView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        teacher.addAttribute("name", DataType.VARCHAR, 0, 0);
        teacher.addAttribute("age", DataType.INTEGER, 0, 0);

        Entity student = testView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, 1, 0);
        student.addAttribute("name", DataType.VARCHAR, 0, 0);
        student.addAttribute("grade", DataType.INTEGER, 0, 0);

        Relationship ts = testView.createRelationship("teaches", teacher, student, Cardinality.OneToMany);

        View dbView = View.queryByID(testView.getID());
        Assert.assertNotNull(dbView);
        Assert.assertEquals(dbView.getEntityList().size(), 2);
        Assert.assertEquals(dbView.getRelationshipList().size(), 1);
    }

    @Test
    public void updateViewTest() {
        View firstView = ER.createView("first view", "tw");
        String newViewName = "new view name";
        firstView.setName(newViewName);
        firstView.update();

        View newView = View.queryByID(1L);
        Assert.assertEquals(newView.getName(), newViewName);
    }

    @Test(expected = ERException.class)
    public void deleteViewTest() {
        View firstView = ER.createView("first view", "tw");
        firstView = View.queryByID(1L);
        Assert.assertNotNull(firstView);

        firstView.deleteDB();

        View newView = View.queryByID(1L);
        Assert.assertNull(newView);
    }

    @Test(expected = ERException.class)
    public void deleteEntityTest() {
        View firstView = ER.createView("first view", "tw");
        Entity firstEntity = firstView.addEntity("teacher");
        Assert.assertNotNull(firstEntity);

        firstView.deleteEntity(firstEntity);

        View newView = View.queryByID(1L);
        Assert.assertEquals(newView.getEntityList().size(), 0);
        Assert.assertNull(Entity.queryByID(firstEntity.getID()));
    }

    @Test
    public void queryViewTest() {
        View firstView = ER.createView("first view", "tw");
        View secondView = ER.createView("first view", "tw");

        List<View> views = View.queryAll();
        Assert.assertEquals(views.size(), 2);

        ER.useDB = false;
        views = View.queryAll();
        Assert.assertEquals(views.size(), 2);
    }

    @Test(expected = ERException.class)
    public void relationshipTest() {
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
        Assert.assertNotNull(ts);

        ts.setName("new relationship name");
        ts.update();
        Assert.assertEquals(Relationship.queryByID(ts.getID()).getName(), "new relationship name");


        firstView.deleteRelationship(ts);
        Assert.assertEquals(firstView.getRelationshipList().size(), 0);
        Assert.assertNull(Relationship.queryByID(ts.getID()));
    }

    @Test
    public void loadFromJSONTest() throws IOException {
        String content = Files.readString(Path.of("first view.json"), Charset.defaultCharset());
        View view = View.loadFromJSON(content);
        System.out.println(view);
    }
}
