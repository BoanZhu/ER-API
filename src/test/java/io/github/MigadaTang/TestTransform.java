package io.github.MigadaTang;

import io.github.MigadaTang.common.*;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class TestTransform {
    Schema view;

    //    @Test
    public void testERModelToRSSucc() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
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

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.print(sql);
    }

    //    @Test
    public void testERModelToRSFail1() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
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

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Failure);
        System.out.println(resultState.getMsg());
    }

    //    @Test
    public void testERModelToRSSucc2() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
        Entity branch = view.addEntity("branch");
        Attribute sortcode = branch.addAttribute("sortcode", DataType.INT, true, false);
        Attribute bname = branch.addAttribute("bname", DataType.VARCHAR, false, false);
        Attribute cash = branch.addAttribute("cash", DataType.FLOAT, false, false);
        Entity account = view.addEntity("account");
        Attribute no = account.addAttribute("no", DataType.INT, true, false);
        Attribute type = account.addAttribute("type", DataType.VARCHAR, false, false);
        Attribute cname = account.addAttribute("cname", DataType.VARCHAR, false, false);
        Attribute rate = account.addAttribute("rate", DataType.FLOAT, false, true);
        Entity movement = view.addEntity("movement");
        Attribute mid = movement.addAttribute("mid", DataType.INT, true, false);
        Attribute tdate = movement.addAttribute("tdate", DataType.DATETIME, false, false);
        Attribute amount = movement.addAttribute("amount", DataType.FLOAT, false, false);

        Relationship bhar = view.createRelationship("holds", branch, account, Cardinality.ZeroToMany, Cardinality.OneToOne);
        Relationship ahmr = view.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.print(sql);
    }

    @Test
    public void testRSToERModel() {
        Tranform tranform = new Tranform();
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.H2, "jdbc:h2:tcp://localhost/~/test",
//                "sa", "");
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "jdbc:postgresql://db.doc.ic.ac.uk:5432/wh722",
//                "wh722", "4jC@A3528>0N6");
        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722",
                "wh722", "4jC@A3528>0N6");
        resultState.getData();
    }
}
