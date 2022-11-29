Amazing ER
------

[![pipeline status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/pipeline.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/commits/master)
[![Coverage Status](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/badges/master/coverage.svg)](https://gitlab.doc.ic.ac.uk/g226002120/AmazingProject/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.MigadaTang/amazing-er/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.MigadaTang/amazing-er)
[![javadoc](https://javadoc.io/badge2/io.github.MigadaTang/amazing-er/javadoc.svg)](https://javadoc.io/doc/io.github.MigadaTang/amazing-er/)

A library containing classes with which users could create their own ER schema and export to JSON or render as image.

# Dependency

```xml 
<dependency>
    <groupId>io.github.MigadaTang</groupId>
    <artifactId>amazing-er</artifactId>
    <version>0.0.1</version>
</dependency>
```

# Quick Start

## Create vanilla ER schema and diagram

```java
import io.github.MigadaTang.ER;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.Schema;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;

import java.io.IOException;
import java.sql.SQLException;

public class Hello {
    public static void main(String[] args) throws SQLException, IOException {
        ER.initialize(false);

        Schema firstView = ER.createSchema("first view", "tw");

        Entity teacher = firstView.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, 1, 0);
        teacher.addAttribute("name", DataType.VARCHAR, 0, 0);
        teacher.addAttribute("age", DataType.INT, 0, 0);

        Entity student = firstView.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, 1, 0);
        student.addAttribute("name", DataType.VARCHAR, 0, 0);
        student.addAttribute("grade", DataType.INT, 0, 0);

        Relationship ts = firstView.createRelationship("teaches", teacher, student, Cardinality.OneToMany);
        System.out.println(firstView.ToJSON());
    }
}
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