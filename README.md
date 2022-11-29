Amazing ER
------

[![pipeline status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/pipeline.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/commits/master)
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
    <version>0.0.1</version>
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
    public static void main(String[] args) throws SQLException, IOException {
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

        // export the ER schema to a JSON file
        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format(outputFormat, example.getName()));
        myWriter.write(jsonString);
        myWriter.close();

        jsonString = Files.readString(Path.of(String.format(outputFormat, example.getName())), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        assertNotNull(schema);

        schema.renderAsImage(String.format(outputImagePath, example.getName()));

    }
}
```

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

```sql

```

## Create n-ary relationship

## Create nested relationship

# Documentation

More information about the classes and methods of this library can be found
in [Javadoc](https://www.javadoc.io/doc/io.github.MigadaTang/amazing-er/latest/index.html)

# ER Schema Extensions Supported

# License

This code is under the [MIT Licence](https://choosealicense.com/licenses/mit/).

# Additional Resources

[GOJS ER Diagram Library](https://gojs.net/latest/samples/entityRelationship.html)

[ER Modelling slides](https://www.doc.ic.ac.uk/~pjm/idb/lectures/idb-er-handout.pdf)