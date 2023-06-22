package io.github.MigadaTang;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import io.github.MigadaTang.transform.Table;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRoundTrip {
  Schema schema;

  @BeforeClass
  public static void init() throws Exception {
    TestCommon.setUp();
  }

  @Test
  public void testRoundTripOnPersonProject() throws DBConnectionException, ParseException,
      IOException {

//    Reverse reverse = new Reverse();
//    schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost",
//        "5433", "boanzhu", "boanzhu", "");
//
//    for (Table table: schema.getOldTables()) {
//      System.out.println(table.getName());
//    }
//
//    assertEquals(schema.getEntityList().size(), 2);
//    assertEquals(schema.getRelationshipList().size(), 1);
//
//    Relationship works_in = schema.getRelationshipList().get(0);
//    RelationshipEdge edge1 = works_in.getEdgeList().get(0);
//    RelationshipEdge edge2 = works_in.getEdgeList().get(1);
//    assertEquals(edge1.getCardinality(), Cardinality.ZeroToMany);
//    assertEquals(edge2.getCardinality(), Cardinality.ZeroToMany);
//
//    Entity person;
//    if (schema.getEntityList().get(0).getName().equals("person")) {
//      person = schema.getEntityList().get(0);
//    } else {
//      person = schema.getEntityList().get(1);
//    }
//
//    Attribute name = person.addAttribute("name", DataType.TEXT,
//        AttributeType.Mandatory);
//    String sql1 = schema.generateSqlStatement();
//    assertEquals(sql1, "ALTER TABLE person\n" + "ADD COLUMN name TEXT NOT NULL;");
//
//    Entity manager = schema.addSubset("manager", person);
//    manager.addPrimaryKey("office", DataType.TEXT);
//    String sql2 = schema.generateSqlStatement();
//    assertEquals(sql2, "CREATE TABLE manager (\n"
//        + "    name TEXT NOT NULL,\n"
//        + "    office TEXT NOT NULL,\n"
//        + "    CONSTRAINT manager_pk PRIMARY KEY (name,office),\n"
//        + "    CONSTRAINT manager_fk_person FOREIGN KEY (name) REFERENCES "
//        + "person(name)\n);");
//
//    String sql = sql1 + sql2;
//    ER.connectToDatabaseAndExecuteSql("postgresql", "localhost",
//        "5433", "boanzhu", "boanzhu", "", sql);
  }

  @Test
  public void testRoundTripAddAttributeOnWeakEntity()
      throws DBConnectionException, ParseException, IOException {
//    Reverse reverse = new Reverse();
//    schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost",
//        "5433", "boanzhu", "boanzhu", "");
//
//    for (Entity entity: schema.getEntityList()) {
//      if (entity.getName().equals("city")) {
//        entity.addAttribute("code", DataType.VARCHAR, AttributeType.Mandatory);
//      }
//    }
//
//    String sql = schema.generateSqlStatement();
//    assertEquals(sql, "ALTER TABLE city\n"
//        + "ADD COLUMN code varchar NOT NULL DEFAULT 'N/A';");
  }

  @Test
  public void testRoundTripAddAttributeOnReflexiveRelationship()
      throws DBConnectionException, ParseException, IOException {
//    Reverse reverse = new Reverse();
//    schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost",
//        "5433", "boanzhu", "boanzhu", "");
//
//    for (Entity entity: schema.getEntityList()) {
//      if (entity.getName().equals("merge_with")) {
//        entity.addAttribute("name", DataType.TEXT, AttributeType.Mandatory);
//      }
//    }
//
//    String sql = schema.generateSqlStatement();
//    assertEquals(sql, "ALTER TABLE merges_with\n"
//        + "ADD COLUMN name text NOT NULL DEFAULT 'N/A';");
  }

  @Test
  public void testRoundTripAddAttributeOnOneManyRelationship()
      throws DBConnectionException, ParseException, IOException {
//    Reverse reverse = new Reverse();
//    schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost",
//        "5433", "boanzhu", "boanzhu", "");
//
//    for (Relationship relationship: schema.getRelationshipList()) {
//      if (relationship.getName().equals("airport_island")) {
//        relationship.addAttribute("name", DataType.TEXT, AttributeType.Mandatory);
//      }
//    }
//
//    String sql = schema.generateSqlStatement();
//    assertEquals(sql, "ALTER TABLE airport\n"
//        + "ADD COLUMN name text NOT NULL DEFAULT 'N/A';");
  }

  @Test
  public void testRoundTripAddAttributeOnManyManyRelationship()
      throws DBConnectionException, ParseException, IOException {
//    Reverse reverse = new Reverse();
//    schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost",
//        "5433", "boanzhu", "boanzhu", "");
//
//    for (Relationship relationship: schema.getRelationshipList()) {
//      if (relationship.getName().equals("located_by")) {
//        relationship.addAttribute("name", DataType.TEXT, AttributeType.Mandatory);
//      }
//    }
//
//    String sql = schema.generateSqlStatement();
//    assertEquals(sql, "ALTER TABLE located_by\n"
//        + "ADD COLUMN name text NOT NULL DEFAULT 'N/A';");
  }

  @Test
  public void testRoundTripDeleteWeakEntity()
      throws DBConnectionException, ParseException, IOException {
//    Reverse reverse = new Reverse();
//    schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost",
//        "5433", "boanzhu", "boanzhu", "");
//
//    for (Entity entity: schema.getEntityList()) {
//      if (entity.getName().equals("city_population")) {
//        schema.deleteEntity(entity);
//      }
//    }
//
//    String sql = schema.generateSqlStatement();
//    assertEquals(sql, "DROP TABLE city_population;");
  }

  @Test
  public void testERModelToRSSucc2() throws ParseException {
//    Schema schema = ER.createSchema("schemaName");
//
//    Entity entity1 = schema.addEntity("entityName1");
//    Entity entity2 = schema.addEntity("entityName2");
//
//    Attribute primaryKey1 = entity1.addPrimaryKey("primaryKeyName1", DataType.TEXT);
//    Attribute attribute = entity1.addAttribute("attributeName", DataType.VARCHAR, AttributeType.Mandatory);
//    Attribute primaryKey2 = entity2.addPrimaryKey("primaryKeyName2", DataType.TEXT);
//
//    Relationship relationship = schema.createRelationship("relationshipName", entity1, entity2, Cardinality.OneToOne, Cardinality.ZeroToMany);
//    String DDL = schema.generateSqlStatement();
//
//    Attribute attribute2 = entity1.addAttribute("attributeName2", DataType.VARCHAR, AttributeType.Mandatory);
  }

}
