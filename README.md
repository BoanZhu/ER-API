Amazing ER
------

[![pipeline status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/pipeline.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/-/pipelines)
[![Coverage Status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/coverage.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.MigadaTang/amazing-er/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.MigadaTang/amazing-er)
[![javadoc](https://javadoc.io/badge2/io.github.MigadaTang/amazing-er/javadoc.svg)](https://javadoc.io/doc/io.github.MigadaTang/amazing-er/)

A library providing rich features around ER modelling. With this project, you could:

1. create ER schema using Java
2. Export ER schema to JSON format
3. Render ER schema to png image
4. Transform ER schema to Data Definition Language(DDL)
5. Reverse engineer database into a single ER schema
6. Embed this project into your own application

# Dependency

```xml 
<dependency>
    <groupId>io.github.MigadaTang</groupId>
    <artifactId>amazing-er</artifactId>
    <version>1.0.1</version>
</dependency>
```

# Quick Start

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

        // save your ER schema as image
        example.renderAsImage(String.format(outputImagePath, example.getName()));

        // transform your ER schema to DDL
        String DDL = example.generateSqlStatement();
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

**ER schema to Image**

![Vanilla](https://i.postimg.cc/zGPHCxq3/Vanilla.png)

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