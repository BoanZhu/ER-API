package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import java.io.IOException;
import java.sql.SQLException;

public class Example {

  public void roundTripCase1()
      throws SQLException, ParseException, DBConnectionException, IOException {
    ER.initialize();
    Schema schema = ER. createSchema("schemaName");
    Entity entity1 = schema.addEntity("entityName1");
    Entity entity2 = schema.addEntity("entityName2");
    Attribute primaryKey1 = entity1 .addPrimaryKey("primaryKeyName1", DataType.TEXT);
    Attribute attribute = entity1.addAttribute("attributeName", DataType.VARCHAR,
        AttributeType.Mandatory ) ;

    Attribute primaryKey2 = entity2 .addPrimaryKey("primaryKeyName2", DataType.TEXT);
    Relationship relationship = schema.createRelationship("relationshipName",
        entity1, entity2, Cardinality.OneToOne, Cardinality.ZeroToMany);

    // Generate the DDL statements and execute them String
    String DDL = schema.generateSqlStatement();
    ER.connectToDatabaseAndExecuteSql("databaseType", "hostname",
        "portNumber", "databaseName", "username", "password", DDL);

    // Then continue editing the ER schema, such as adding new attribute in entity1
    Attribute attribute2 = entity1.addAttribute("attributeName2", DataType.VARCHAR, 
        AttributeType.Mandatory ) ;
    // Generate the corresponding SQL statements and execute them 
    String SQL = schema.generateSqlStatement();
    ER.connectToDatabaseAndExecuteSql("databaseType", "hostname", 
        "portNumber", "databaseName", "username", 
        "password", SQL);

  }

}
