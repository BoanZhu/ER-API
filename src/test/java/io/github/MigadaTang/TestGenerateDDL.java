package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.ConnObjWithCardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ParseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class TestGenerateDDL {
    Schema view;

    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void testERModelToRSSucc() {
        view = ER.createSchema("testTransform1");
        Entity student = view.addEntity("student");
        student.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        student.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        student.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity teacher = view.addEntity("teacher");
        teacher.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        teacher.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        teacher.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity school = view.addEntity("school");
        school.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        school.addAttribute("name", DataType.INT, false, AttributeType.Mandatory);

        view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        String sql = "";
        try {
            sql = view.generateSqlStatement();
        } catch (ParseException e) {
            fail();
        }
        System.out.println(sql);
    }


    @Test
    public void testERModelToRSFail1() {
        view = ER.createSchema("testTransform1");
        Entity student = view.addEntity("student");
        student.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        student.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        student.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity teacher = view.addEntity("teacher");
        teacher.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        teacher.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        teacher.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity school = view.addEntity("school");
        school.addAttribute("id", DataType.INT, false, AttributeType.Mandatory);
        school.addAttribute("name", DataType.INT, false, AttributeType.Mandatory);

        view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);

        try {
            view.generateSqlStatement();
        } catch (Exception e) {
            assertTrue(true);
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void testERModelToRSSucc2() {
        view = ER.createSchema("testTransform1");
        Entity branch = view.addEntity("branch");
        branch.addAttribute("sortcode", DataType.INT, true, AttributeType.Mandatory);
        branch.addAttribute("bname", DataType.VARCHAR, false, AttributeType.Mandatory);
        branch.addAttribute("cash", DataType.FLOAT, false, AttributeType.Mandatory);
        Entity account = view.addEntity("account");
        account.addAttribute("no", DataType.INT, true, AttributeType.Mandatory);
        account.addAttribute("type", DataType.VARCHAR, false, AttributeType.Mandatory);
        account.addAttribute("cname", DataType.VARCHAR, false, AttributeType.Mandatory);
        account.addAttribute("rate", DataType.FLOAT, false, AttributeType.Optional);
        Entity movement = view.addEntity("movement");
        movement.addAttribute("mid", DataType.INT, true, AttributeType.Mandatory);
        movement.addAttribute("tdate", DataType.DATETIME, false, AttributeType.Mandatory);
        movement.addAttribute("amount", DataType.FLOAT, false, AttributeType.Mandatory);

        view.createRelationship("holds", branch, account, Cardinality.ZeroToMany, Cardinality.OneToOne);
        view.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
        } catch (ParseException e) {
            fail();
        }
        System.out.println(sql);
    }


    @Test
    public void testERModelToRSSuccWithNaryAndSubset() {
        view = ER.createSchema("testTransform1");
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, AttributeType.Mandatory);

        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);

        Entity manager = view.addSubset("manager", person);

        List<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
        ConnObjWithCardinality entity1 = new ConnObjWithCardinality(department, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity2 = new ConnObjWithCardinality(person, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity3 = new ConnObjWithCardinality(manager, Cardinality.ZeroToMany);
        connObjWithCardinalityList.add(entity1);
        connObjWithCardinalityList.add(entity2);
        connObjWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", connObjWithCardinalityList);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
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
        department.addAttribute("dname", DataType.INT, true, AttributeType.Mandatory);

        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);

        ImmutablePair<Entity, Relationship> managerRelPair = view.addWeakEntity("manager", person
                , "is a ", Cardinality.OneToOne, Cardinality.OneToMany);
        Entity manager = managerRelPair.left;
        manager.addAttribute("name", DataType.VARCHAR, true, AttributeType.Mandatory);

        List<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
        ConnObjWithCardinality entity1 = new ConnObjWithCardinality(department, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity2 = new ConnObjWithCardinality(person, Cardinality.ZeroToMany);
        ConnObjWithCardinality entity3 = new ConnObjWithCardinality(manager, Cardinality.ZeroToMany);
        connObjWithCardinalityList.add(entity1);
        connObjWithCardinalityList.add(entity2);
        connObjWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", connObjWithCardinalityList);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
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
        project.addAttribute("pcode", DataType.INT, true, AttributeType.Mandatory);
        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, AttributeType.Mandatory);

        Relationship works_in = view.createEmptyRelationship("works in");
        Relationship member = view.createEmptyRelationship("member");
        works_in.linkObj(person, Cardinality.ZeroToMany);
        works_in.linkObj(department, Cardinality.ZeroToMany);
        member.linkObj(project, Cardinality.ZeroToMany);
        member.linkObj(works_in, Cardinality.ZeroToMany);
        member.addAttribute("role", DataType.TEXT, AttributeType.Mandatory);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
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
        project.addAttribute("pcode", DataType.INT, true, AttributeType.Mandatory);
        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, AttributeType.Mandatory);

        Relationship works_in = view.createEmptyRelationship("works in");
        Relationship member = view.createEmptyRelationship("member");
        works_in.linkObj(person, Cardinality.ZeroToMany);
        works_in.linkObj(department, Cardinality.ZeroToMany);
        member.linkObj(project, Cardinality.ZeroToMany);
        member.linkObj(works_in, Cardinality.OneToOne);
        member.addAttribute("role", DataType.TEXT, AttributeType.Mandatory);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
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
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);
        person.addAttribute("phone", DataType.TEXT, false, AttributeType.Multivalued);
        person.addAttribute("car", DataType.TEXT, false, AttributeType.Both);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWMultiValuedOnRelationship() {
        view = ER.createSchema("testTransformMultiValued");
        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.TEXT, true, AttributeType.Mandatory);

        Relationship worksin = view.createEmptyRelationship("works in");
        worksin.linkObj(person, Cardinality.OneToOne);
        worksin.linkObj(department, Cardinality.ZeroToMany);

        worksin.addAttribute("start_date", DataType.DATETIME, AttributeType.Mandatory);
        worksin.addAttribute("end_date", DataType.DATETIME, AttributeType.Optional);
        worksin.addAttribute("position", DataType.TEXT, AttributeType.Both);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }

    @Test
    public void testERModelToRSSuccWMultiValuedOnRelationship2() {
        view = ER.createSchema("testTransformMultiValued");
        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.TEXT, true, AttributeType.Mandatory);

        Relationship worksin = view.createEmptyRelationship("works in");
        worksin.linkObj(person, Cardinality.ZeroToMany);
        worksin.linkObj(department, Cardinality.ZeroToMany);

        worksin.addAttribute("start_date", DataType.DATETIME, AttributeType.Mandatory);
        worksin.addAttribute("end_date", DataType.DATETIME, AttributeType.Optional);
        worksin.addAttribute("position", DataType.TEXT, AttributeType.Both);

        String sql = "";
        try {
            sql = view.generateSqlStatement();
        } catch (ParseException e) {
            fail();
            System.out.println(e.getMessage());
        }
        System.out.print(sql);
    }
}
