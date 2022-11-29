package io.github.MigadaTang;

import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;


public class TestTransform {
    Schema view;

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void testERModelToRSSucc() {
        view = ER.createSchema("testTransform1");
        Entity student = view.addEntity("student");
        Attribute studentId = student.addPrimaryKey("id", DataType.INT);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute studentAge = student.addAttribute("age", DataType.INT, AttributeType.Mandatory);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addPrimaryKey("id", DataType.INT);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, AttributeType.Mandatory);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addPrimaryKey("id", DataType.INT);
        Attribute schoolName = school.addAttribute("name", DataType.INT, AttributeType.Mandatory);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
        }
        System.out.println(sql);
    }


    @Test
    public void testERModelToRSFail1() {
        view = ER.createSchema("testTransform1");
        Entity student = view.addEntity("student");
        Attribute studentId = student.addPrimaryKey("id", DataType.INT);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute studentAge = student.addAttribute("age", DataType.INT, AttributeType.Mandatory);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addPrimaryKey("id", DataType.INT);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, AttributeType.Mandatory);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, AttributeType.Mandatory);
        Attribute schoolName = school.addAttribute("name", DataType.INT, AttributeType.Mandatory);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            assertTrue(true);
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testERModelToRSSucc2() {
        view = ER.createSchema("testTransform1");
        Entity branch = view.addEntity("branch");
        Attribute sortcode = branch.addPrimaryKey("sortcode", DataType.INT);
        Attribute bname = branch.addAttribute("bname", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute cash = branch.addAttribute("cash", DataType.FLOAT, AttributeType.Mandatory);
        Entity account = view.addEntity("account");
        Attribute no = account.addPrimaryKey("no", DataType.INT);
        Attribute type = account.addAttribute("type", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute cname = account.addAttribute("cname", DataType.VARCHAR, AttributeType.Mandatory);
        Attribute rate = account.addAttribute("rate", DataType.FLOAT, AttributeType.Optional);
        Entity movement = view.addEntity("movement");
        Attribute mid = movement.addPrimaryKey("mid", DataType.INT);
        Attribute tdate = movement.addAttribute("tdate", DataType.DATETIME, AttributeType.Mandatory);
        Attribute amount = movement.addAttribute("amount", DataType.FLOAT, AttributeType.Mandatory);

        Relationship bhar = view.createRelationship("holds", branch, account, Cardinality.ZeroToMany, Cardinality.OneToOne);
        Relationship ahmr = view.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
        }
        System.out.println(sql);
    }


    @Test
    public void testERModelToRSSuccWithNaryAndSubset() {
        view = ER.createSchema("testTransform1");
        Entity department = view.addEntity("department");
        department.addPrimaryKey("dname", DataType.INT);

        Entity person = view.addEntity("person");
        person.addPrimaryKey("salary_number", DataType.INT);

        Entity manager = view.addSubset("manager", person);

        List<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
        ConnObjWithCardinality entity1 = new ConnObjWithCardinality(department, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity2 = new ConnObjWithCardinality(person, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity3 = new ConnObjWithCardinality(manager, Cardinality.ZeroToMany);
        connObjWithCardinalityList.add(entity1);
        connObjWithCardinalityList.add(entity2);
        connObjWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", connObjWithCardinalityList);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWithNaryAndWeakEntity() {
        view = ER.createSchema("testTransformWeakEntity");
        Entity department = view.addEntity("department");
        department.addPrimaryKey("dname", DataType.INT);

        Entity person = view.addEntity("person");
        person.addPrimaryKey("salary_number", DataType.INT);

        ImmutablePair<Entity, Relationship> managerRelPair = view.addWeakEntity("manager", person
                , "is a ", Cardinality.OneToOne, Cardinality.OneToMany);
        Entity manager = managerRelPair.left;
        manager.addPrimaryKey("name", DataType.VARCHAR);

        List<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
        ConnObjWithCardinality entity1 = new ConnObjWithCardinality(department, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity2 = new ConnObjWithCardinality(person, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity3 = new ConnObjWithCardinality(manager, Cardinality.ZeroToMany);
        connObjWithCardinalityList.add(entity1);
        connObjWithCardinalityList.add(entity2);
        connObjWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", connObjWithCardinalityList);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWithNestedRelationship() {
        view = ER.createSchema("testTransformNestedRelationship");
        Entity project = view.addEntity("project");
        project.addPrimaryKey("pcode", DataType.INT);
        Entity person = view.addEntity("person");
        person.addPrimaryKey("salary_number", DataType.INT);
        Entity department = view.addEntity("department");
        department.addPrimaryKey("dname", DataType.INT);

        Relationship works_in = view.createEmptyRelationship("works in");
        Relationship member = view.createEmptyRelationship("member");
        works_in.linkObj(person, Cardinality.ZeroToMany);
        works_in.linkObj(department, Cardinality.ZeroToMany);
        member.linkObj(project, Cardinality.ZeroToMany);
        member.linkObj(works_in, Cardinality.ZeroToMany);
        member.addAttribute("role", DataType.TEXT, AttributeType.Mandatory);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWithNestedRelationship2() {
        view = ER.createSchema("testTransformNestedRelationship2");
        Entity project = view.addEntity("project");
        project.addPrimaryKey("pcode", DataType.INT);
        Entity person = view.addEntity("person");
        person.addPrimaryKey("salary_number", DataType.INT);
        Entity department = view.addEntity("department");
        department.addPrimaryKey("dname", DataType.INT);

        Relationship works_in = view.createEmptyRelationship("works in");
        Relationship member = view.createEmptyRelationship("member");
        works_in.linkObj(person, Cardinality.ZeroToMany);
        works_in.linkObj(department, Cardinality.ZeroToMany);
        member.linkObj(project, Cardinality.ZeroToMany);
        member.linkObj(works_in, Cardinality.OneToOne);
        member.addAttribute("role", DataType.TEXT, AttributeType.Mandatory);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWMultiValued() {
        view = ER.createSchema("testTransformMultiValued");
        Entity person = view.addEntity("person");
        person.addPrimaryKey("salary_number", DataType.INT);
        person.addAttribute("phone", DataType.TEXT, AttributeType.Multivalued);
        person.addAttribute("car", DataType.TEXT, AttributeType.Both);

        Transform transform = new Transform();
        String sql = "";
        try {
            sql = transform.ERModelToSql(view.getID());
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testRSToERModel() throws IOException, SQLException {
        Transform transform = new Transform();
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "jdbc:postgresql://db.doc.ic.ac.uk:5432/wh722",
//                "wh722", "4jC@A3528>0N6");
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722",
//                "wh722", "4jC@A3528>0N6");
        try {
            transform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wt22",
                    "wt22", "22V**66+C5JPu", "image");
        } catch (ParseException | DBConnectionException e) {
            fail();
        }
    }
}
