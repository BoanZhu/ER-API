package io.github.MigadaTang;

import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestTransform {
    Schema view;

    @Test
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


        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            Assert.fail();
        }
        System.out.println(sql);
    }


    @Test
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


        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            Assert.assertTrue(true);
            System.out.println(e.getMessage());
        }
    }


    @Test
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

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            Assert.assertTrue(true);
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testERModelToRSSuccWithNaryAndSubset() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, false);

        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, false);

        Entity manager = view.addSubset("manager", person);

        List<EntityWithCardinality> entityWithCardinalityList = new ArrayList<>();
        EntityWithCardinality entity1 = new EntityWithCardinality(department, Cardinality.ZeroToMany);
        EntityWithCardinality entity2 = new EntityWithCardinality(person, Cardinality.ZeroToMany);
        EntityWithCardinality entity3 = new EntityWithCardinality(manager, Cardinality.ZeroToMany);
        entityWithCardinalityList.add(entity1);
        entityWithCardinalityList.add(entity2);
        entityWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", entityWithCardinalityList);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            Assert.fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWithNaryAndWeakEntity() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransformWeakEntity", "wd");
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, false);

        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, false);

        ImmutablePair<Entity, Relationship> managerRelPair = view.addWeakEntity("manager", person
                , "is a ", Cardinality.OneToOne, Cardinality.OneToMany);
        Entity manager = managerRelPair.left;
        manager.addAttribute("name", DataType.VARCHAR, true, false);

        List<EntityWithCardinality> entityWithCardinalityList = new ArrayList<>();
        EntityWithCardinality entity1 = new EntityWithCardinality(department, Cardinality.ZeroToMany);
        EntityWithCardinality entity2 = new EntityWithCardinality(person, Cardinality.ZeroToMany);
        EntityWithCardinality entity3 = new EntityWithCardinality(manager, Cardinality.ZeroToMany);
        entityWithCardinalityList.add(entity1);
        entityWithCardinalityList.add(entity2);
        entityWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", entityWithCardinalityList);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            Assert.fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testRSToERModel() throws IOException, SQLException {
        ER.initialize(true);
        Transform transform = new Transform();
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "jdbc:postgresql://db.doc.ic.ac.uk:5432/wh722",
//                "wh722", "4jC@A3528>0N6");
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722",
//                "wh722", "4jC@A3528>0N6");
        try {
            transform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wt22",
                    "wt22", "22V**66+C5JPu");
        } catch (ParseException | DBConnectionException e) {
            Assert.fail();
        }
    }
}
