package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import com.ic.er.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestView {

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void createViewTest() {
        View testView = ER.createView("testView", "wt22");

        Entity teacher = testView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, true, false);
        teacher.addAttribute("name", DataType.VARCHAR, false, false);
        teacher.addAttribute("age", DataType.INT, false, false);

        Entity student = testView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, true, false);
        student.addAttribute("name", DataType.VARCHAR, false, false);
        student.addAttribute("grade", DataType.INT, false, false);

        Relationship ts = testView.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);

        View dbView = View.queryByID(testView.getID());
        Assert.assertNotNull(dbView);
        Assert.assertEquals(dbView.getEntityList().size(), 2);
        Assert.assertEquals(dbView.getRelationshipList().size(), 1);
    }

    @Test
    public void updateViewTest() {
        View firstView = ER.createView("first view", "tw");
        String newViewName = "new view name";
        firstView.updateInfo(newViewName);

        View newView = View.queryByID(firstView.getID());
        Assert.assertEquals(newView.getName(), newViewName);
    }

    @Test
    public void deleteViewTest() {
        View testDeleteView = ER.createView("first view", "tw");
        testDeleteView = View.queryByID(testDeleteView.getID());
        Assert.assertNotNull(testDeleteView);

        testDeleteView.deleteDB();

        View finalTestDeleteView = testDeleteView;
        assertThrows(ERException.class, () -> View.queryByID(finalTestDeleteView.getID()));
    }

    @Test
    public void deleteEntityTest() {
        View firstView = ER.createView("first view", "tw");
        Entity firstEntity = firstView.addEntity("teacher");
        Assert.assertNotNull(firstEntity);

        firstView.deleteEntity(firstEntity);

        View newView = View.queryByID(firstView.getID());
        Assert.assertEquals(newView.getEntityList().size(), 0);
        assertThrows(ERException.class, () -> Entity.queryByID(firstEntity.getID()));
    }

    @Test
    public void queryViewTest() {
        View firstView = ER.createView("first view", "tw");
        List<View> views = View.queryAll();
        Assert.assertNotEquals(views.size(), 0);
    }
}
