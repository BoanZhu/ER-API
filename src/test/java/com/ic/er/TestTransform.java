package com.ic.er;

import com.ic.er.common.*;
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
        Attribute studentId = student.addAttribute("id", DataType.INT, true, false);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, false, false);
        Attribute studentAge = student.addAttribute("age", DataType.INT, false, false);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addAttribute("id", DataType.INT, true, false);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, false, false);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, false, true);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, true, false);
        Attribute schoolName = school.addAttribute("name", DataType.INT, false, false);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToMany, Cardinality.OneToOne);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.OneToMany, Cardinality.ZeroToOne);
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
        Attribute studentId = student.addAttribute("id", DataType.INT, true, false);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, false, false);
        Attribute studentAge = student.addAttribute("age", DataType.INT, false, false);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addAttribute("id", DataType.INT, true, false);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, false, false);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, false, true);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, false, false);
        Attribute schoolName = school.addAttribute("name", DataType.INT, false, false);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToMany, Cardinality.OneToOne);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.OneToMany, Cardinality.ZeroToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.OneToMany, Cardinality.ZeroToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Failure);
        System.out.println(resultState.getMsg());
    }

    @Test
    public void testRSToERModel() {
        Tranform tranform = new Tranform();
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.H2, "jdbc:h2:tcp://localhost/~/test",
//                "sa", "");
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "jdbc:postgresql://db.doc.ic.ac.uk:5432/wh722",
//                "wh722", "4jC@A3528>0N6");
        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432" , "wh722",
                "wh722", "4jC@A3528>0N6");
        resultState.getData();
    }
}
