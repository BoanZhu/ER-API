package io.github.MigadaTang;

import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import java.sql.SQLException;

public class Test {

  public static void main(String[] args) throws DBConnectionException, ParseException, SQLException {
    System.out.println("Try to test database connection!");
    ER.initialize(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722", "wh722", "4jC@A3528>0N6");
//    testReverseEngineer();
    testSqlGeneration();
    System.out.println("Test finished!");
  }

  public static void testSqlGeneration() throws ParseException {
    Schema schema = Schema.queryByID(new Long(1244));
//    System.out.println("test schema: " + schema);
    String JSON = schema.generateSqlStatement();
//    System.out.println("JSON: " + JSON);
  }

  public static void testReverseEngineer() throws DBConnectionException, ParseException {
    Reverse reverse = new Reverse();
//    Schema schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost", "5433", "boanzhu", "boanzhu", "", "54321");
    Schema schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "localhost", "5433", "boanzhu", "boanzhu", "");
//    for (Entity entity: schema.getEntityList()) {
//      System.out.println("Entity: " + entity.getEntityType());
//    }
//    for (Relationship relationship: schema.getRelationshipList()) {
//      System.out.println("Relationship: " + relationship);
//    }
//    System.out.println("schema: " + schema.getName() + ", ID: " + schema.getID());
    String JSON = schema.toRenderJSON();
//    System.out.println("JSON: " + JSON);
  }

  public static void testDatabaseConnection() throws DBConnectionException, ParseException {
    String ddl1 = "CREATE TABLE Managers (\n"
        + "    Name TEXT NOT NULL,\n"
        + "    Dog_name TEXT NULL,\n"
        + "    CONSTRAINT Managers_pk PRIMARY KEY (Name)\n"
        + ");\n"
        + "\n"
        + "CREATE TABLE Department (\n"
        + "    Office_no TEXT NOT NULL,\n"
        + "    Dname TEXT NOT NULL,\n"
        + "    CONSTRAINT Department_pk PRIMARY KEY (Dname)\n"
        + ");\n"
        + "\n"
        + "CREATE TABLE manages_Managers_Department (\n"
        + "    Managers_Name TEXT NOT NULL,\n"
        + "    Department_Dname TEXT NOT NULL,\n"
        + "    CONSTRAINT manages_Managers_Department_pk PRIMARY KEY (Managers_Name,Department_Dname),\n"
        + "    CONSTRAINT manages_Managers_Department_fk1 FOREIGN KEY (Managers_Name) REFERENCES Managers(Name),\n"
        + "    CONSTRAINT manages_Managers_Department_fk2 FOREIGN KEY (Department_Dname) REFERENCES Department(Dname)\n"
        + ");";
    String ddl2 = "CREATE TABLE Person (    salary_number VARCHAR NOT NULL,    CONSTRAINT Person_pk PRIMARY KEY (salary_number))";
    ER.connectToDatabaseAndExecuteSql("postgresql", "localhost", "5433", "boanzhu", "boanzhu", "", ddl1);
  }

}
