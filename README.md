Amazing ER
------

[![pipeline status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/pipeline.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/-/pipelines)
[![Coverage Status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/coverage.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.MigadaTang/amazing-er/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.MigadaTang/amazing-er)
[![javadoc](https://javadoc.io/badge2/io.github.MigadaTang/amazing-er/javadoc.svg)](https://javadoc.io/doc/io.github.MigadaTang/amazing-er/)

A library providing rich features around ER modelling. With this project, you could:

1. Create ER schemas using Java
2. Export ER schemas to JSON format
3. Automatically define the layout of the ER diagram   
4. Transform ER schema to relational schema and generate Data Definition Language (DDL)
5. Reverse engineering a database into an ER schema
6. Round-trip for database modelling (synchronise the changes to database)
7. Embed this project into your own application

[comment]: <> (# Dependency)

[comment]: <> (```xml )

[comment]: <> (<dependency>)

[comment]: <> (    <groupId>io.github.MigadaTang</groupId>)

[comment]: <> (    <artifactId>amazing-er</artifactId>)

[comment]: <> (    <version>1.0.2</version>)

[comment]: <> (</dependency>)

[comment]: <> (```)

# Quick Start

## How to use

This is the one of the sub-projects, the other two projects links:

Front-end application link: https://gitlab.doc.ic.ac.uk/bz2818/er-frontend

Back-end application link: https://gitlab.doc.ic.ac.uk/bz2818/er-backend

The following diagram shows the structure of the three sub-projects:

![Architecture](https://gitlab.doc.ic.ac.uk/bz2818/er-api/-/blob/master/images/API architecture.png)

Download the ER API sub-project using the following command line:

```
git clone https://gitlab.doc.ic.ac.uk/bz2818/er-api.git
```

After downloading the ER API subproject, you can package it into a JAR file and embed it in your own application.

![Architecture](https://gitlab.doc.ic.ac.uk/bz2818/er-api/-/blob/master/images/build artifact.png)

The back-end application already contains the JAR file and can be used directly.

# Examples

## Create vanilla ER schema, export to JSON, image and DDL.

```java
import io.github.MigadaTang.ER;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.Schema;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;

import java.io.IOException;
import java.sql.SQLException;

public class Example {
    public static void main(String[] args) {
        // initialize the in-memory database to store ER schema
        ER.initialize();
        // you could also specify your own database
//        ER.initialize(RDBMSType.POSTGRESQL, "hostname", "port", "database", "user", "password");

        Schema example = ER.createSchema("Vanilla");

        Entity branch = example.addEntity("branch");
        branch.addPrimaryKey("sortcode", DataType.INT);
        branch.addAttribute("bname", DataType.VARCHAR, AttributeType.Mandatory);
        branch.addAttribute("cash", DataType.DOUBLE, AttributeType.Mandatory);

        Entity account = example.addEntity("account");
        account.addPrimaryKey("no", DataType.INT);
        account.addAttribute("type", DataType.CHAR, AttributeType.Mandatory);
        account.addAttribute("cname", DataType.VARCHAR, AttributeType.Mandatory);
        account.addAttribute("rate", DataType.DOUBLE, AttributeType.Mandatory);

        Entity movement = example.addEntity("movement");
        movement.addPrimaryKey("mid", DataType.INT);
        movement.addAttribute("amount", DataType.DOUBLE, AttributeType.Mandatory);
        movement.addAttribute("tdate", DataType.DATETIME, AttributeType.Mandatory);

        Relationship holds = example.createRelationship("holds", account, branch, Cardinality.OneToOne, Cardinality.ZeroToMany);
        Relationship has = example.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        // export the ER schema to a JSON format
        String jsonString = example.toJSON();
        
        // transform your ER schema to DDL
        String DDL = example.generateSqlStatement();
        
    }
}
```

## Reverse engineering a database example

```java

package io.github.MigadaTang;

import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import java.io.IOException;
import java.sql.SQLException;

public class Example {

  public void reverseEngineer()
      throws SQLException, ParseException, DBConnectionException, IOException {
    ER.initialize();
    Reverse reverse = new Reverse();
    Schema schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "host"
        , "port", "databaseName", "username", "password");
  }

}

```

## Round-trip case1 example:

```java

package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
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

```

## Round-trip case2 example:

```java

package io.github.MigadaTang;

import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import java.io.IOException;
import java.sql.SQLException;

public class Example {

  public void roundTripCase2()
      throws SQLException, ParseException, DBConnectionException, IOException {
    ER.initialize();
    Reverse reverse = new Reverse ();
    Schema schema = reverse .relationSchemasToERModel(RDBMSType.POSTGRESQL,
        "hostname", "portNumber", "databaseName",
        "username", "password");

    // After make some modifications in the schema
    String SQL = schema.generateSqlStatement();
    // Execute the SQL statements in the database 
    ER.connectToDatabaseAndExecuteSql("databaseType", "hostname", "portNumber",
    "databaseName", "username", "password", SQL);
  }

}


```

**ER schema to JSON**

```json
{
  "name": "Vanilla",
  "entityList": [
    {
      "name": "branch",
      "entityType": "STRONG",
      "attributeList": [
        {
          "name": "sortcode",
          "dataType": "INT",
          "isPrimary": true,
          "attributeType": "Mandatory"
        },
        {
          "name": "bname",
          "dataType": "VARCHAR",
          "isPrimary": false,
          "attributeType": "Mandatory"
        },
        {
          "name": "cash",
          "dataType": "DOUBLE",
          "isPrimary": false,
          "attributeType": "Mandatory"
        }
      ]
    },
    {
      "name": "account",
      "entityType": "STRONG",
      "attributeList": [
        {
          "name": "no",
          "dataType": "INT",
          "isPrimary": true,
          "attributeType": "Mandatory"
        },
        {
          "name": "type",
          "dataType": "CHAR",
          "isPrimary": false,
          "attributeType": "Mandatory"
        },
        {
          "name": "cname",
          "dataType": "VARCHAR",
          "isPrimary": false,
          "attributeType": "Mandatory"
        },
        {
          "name": "rate",
          "dataType": "DOUBLE",
          "isPrimary": false,
          "attributeType": "Mandatory"
        }
      ]
    },
    {
      "name": "movement",
      "entityType": "STRONG",
      "attributeList": [
        {
          "name": "mid",
          "dataType": "INT",
          "isPrimary": true,
          "attributeType": "Mandatory"
        },
        {
          "name": "amount",
          "dataType": "DOUBLE",
          "isPrimary": false,
          "attributeType": "Mandatory"
        },
        {
          "name": "tdate",
          "dataType": "DATETIME",
          "isPrimary": false,
          "attributeType": "Mandatory"
        }
      ]
    }
  ],
  "relationshipList": [
    {
      "name": "holds",
      "edgeList": [
        {
          "entity": "account",
          "cardinality": "1:1"
        },
        {
          "entity": "branch",
          "cardinality": "0:N"
        }
      ]
    },
    {
      "name": "has",
      "edgeList": [
        {
          "entity": "account",
          "cardinality": "0:N"
        },
        {
          "entity": "movement",
          "cardinality": "1:1"
        }
      ]
    }
  ]
}
```

[comment]: <> (**ER schema to Image**)

[comment]: <> (![Vanilla]&#40;https://i.postimg.cc/zGPHCxq3/Vanilla.png&#41;)

**ER schema to Data Definition Language(DDL)**

```sql
CREATE TABLE `branch` (
    `sortcode` INT NOT NULL,
    `bname` VARCHAR NOT NULL,
    `cash` DOUBLE NOT NULL,
    CONSTRAINT branch_pk PRIMARY KEY (sortcode)
)

CREATE TABLE `account` (
    `no` INT NOT NULL,
    `type` CHAR NOT NULL,
    `cname` VARCHAR NOT NULL,
    `rate` DOUBLE NOT NULL,
    `branch_sortcode` INT NOT NULL,
    CONSTRAINT account_pk PRIMARY KEY (no),
    CONSTRAINT account_fk1 FOREIGN KEY (branch_sortcode) REFERENCES branch(sortcode)
)

CREATE TABLE `movement` (
    `mid` INT NOT NULL,
    `amount` DOUBLE NOT NULL,
    `tdate` DATETIME NOT NULL,
    `account_no` INT NOT NULL,
    CONSTRAINT movement_pk PRIMARY KEY (mid),
    CONSTRAINT movement_fk1 FOREIGN KEY (account_no) REFERENCES account(no)
)
```

## Create n-ary relationship

```java
public class Example {
    public static void main(String[] args) {
        Schema example = ER.createSchema("N-ary Relationship");

        Entity person = example.addEntity("person");
        Entity manager = example.addEntity("manager");
        Entity department = example.addEntity("department");

        ArrayList<ConnObjWithCardinality> eCardList = new ArrayList<>();
        eCardList.add(new ConnObjWithCardinality(person, Cardinality.ZeroToMany));
        eCardList.add(new ConnObjWithCardinality(manager, Cardinality.ZeroToMany));
        eCardList.add(new ConnObjWithCardinality(department, Cardinality.ZeroToMany));
        Relationship worksIn = example.createNaryRelationship("works in", eCardList);
    }
}
```

## Create subset

```java
public class Example {
    public static void main(String[] args) {
        Schema example = ER.createSchema("Subset");

        Entity person = example.addEntity("person");
        person.addPrimaryKey("salary number", DataType.VARCHAR);
        person.addAttribute("bonus", DataType.VARCHAR, AttributeType.Optional);
        person.addAttribute("name", DataType.VARCHAR, AttributeType.Mandatory);

        Entity manager = example.addSubset("manager", person);
        manager.addAttribute("mobile number", DataType.VARCHAR, AttributeType.Mandatory);
    }
}

```

## More examples

see [TestQuickStartExamples](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/-/blob/master/src/test/java/io/github/MigadaTang/TestQuickStartExamples.java)
and [TestGenerateDDL](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/-/blob/master/src/test/java/io/github/MigadaTang/TestGenerateDDL.java)

# Documentation

More information about the classes and methods of this library can be found
in [Javadoc](https://www.javadoc.io/doc/io.github.MigadaTang/amazing-er/latest/index.html)

# ER Schema Extensions Supported

| ER Extensions               | Supported           |
|-----------------------------|---------------------|
| Weak entities               | :white_check_mark:  |
| N-ary relationships         | :white_check_mark:           |
| Attributes on relationships | :white_check_mark:            |
| Nested relationships        | :white_check_mark:            |
| Multi-valued Attributes       | :white_check_mark:            |

# License

This code is under the [MIT Licence](https://choosealicense.com/licenses/mit/).

# Additional Resources

[ER Modelling slides](https://www.doc.ic.ac.uk/~pjm/idb/lectures/idb-er-handout.pdf)

[GOJS ER Diagram Library](https://gojs.net/latest/samples/entityRelationship.html)