package io.github.MigadaTang;

import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;

public class Test {

  public static void main(String[] args) throws DBConnectionException, ParseException {
    System.out.println("Try to test database connection!");
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
    System.out.println("Test finished!");
  }

}
