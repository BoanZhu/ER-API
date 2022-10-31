package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestTransform {
    View view;

    @Test
    public void testERModelToRSSucc() throws IOException, SQLException {
        ER.initialize(true);
        view = ER.createView("testTransform1", "wd");
        Entity student = view.addEntity("student");
        Attribute studentId = student.addAttribute("id", DataType.INT, true);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, false);
        Attribute studentAge = student.addAttribute("age", DataType.INT, false);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addAttribute("id", DataType.INT, true);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, false);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, false);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, true);
        Attribute schoolName = school.addAttribute("name", DataType.INT, false);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.OneToMany, Cardinality.ZeroToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.print(sql);
    }

    @Test
    public void testERModelToRSFail1() throws IOException, SQLException {
        ER.initialize(true);
        view = ER.createView("testTransform1", "wd");
        Entity student = view.addEntity("student");
        Attribute studentId = student.addAttribute("id", DataType.INT, true);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, false);
        Attribute studentAge = student.addAttribute("age", DataType.INT, false);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addAttribute("id", DataType.INT, true);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, false);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, false);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, false);
        Attribute schoolName = school.addAttribute("name", DataType.INT, false);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.OneToMany, Cardinality.ZeroToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Failure);
        System.out.println(resultState.getMsg());
    }

    @Test
    public void testRSToERModel() {
        Tranform tranform = new Tranform();
        ResultState resultState = tranform.relationSchemasToERModel("org.h2.Driver", "jdbc:h2:mem:test",
                "sa", "");
        resultState.getData();
    }
}
