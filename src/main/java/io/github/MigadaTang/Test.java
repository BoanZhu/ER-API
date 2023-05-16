package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import io.github.MigadaTang.transform.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {

  public static void main(String[] args) throws DBConnectionException, ParseException, SQLException {
    System.out.println("Try to test database connection!");
    ER.initialize(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722", "wh722", "4jC@A3528>0N6");
//    testReverseEngineer();
//    testSqlGeneration();
    testSql();
//    testLayout();
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

    generateLayoutInformation(schema);
    String JSON = schema.toRenderJSON();
//    System.out.println("JSON: " + JSON);
  }

  public static void testLayout() {
    Schema example = ER.createSchema("TestSQL");
    Entity student = example.addEntity("A");
    student.addPrimaryKey("name", DataType.TEXT);

    Entity project = example.addEntity("B");
    project.addPrimaryKey("pname", DataType.TEXT);

    Entity course = example.addEntity("C");
    Entity D = example.addEntity("D");
    Entity E = example.addEntity("E");

    Relationship works_in = example.createRelationship("works_in", student, project, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    Relationship joins = example.createRelationship("joins", student, course, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    Relationship AD = example.createRelationship("AD", student, D, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    Relationship BE = example.createRelationship("BE", course, E, Cardinality.ZeroToMany, Cardinality.ZeroToMany);

    generateLayoutInformation(example);
  }

  public static void generateLayoutInformation(Schema schema) {
    List<Entity> entities = schema.getEntityList();
    List<Relationship> relationships = schema.getRelationshipList();

    List<Node> nodes = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();

    for (Entity entity: entities) {
      Node node = Node.transformNode(entity);
      nodes.add(node);
    }

    for (Relationship relationship: relationships) {
      Edge edge = Edge.transformEdge(relationship, schema.getEntityList());
      edges.add(edge);
    }

    // The entities and relationships can be used to get a connected graph
    // We can use this graph as the input and use Force-Directed layout algorithm to iterate a lot of
    // times to get a appropriate position.
    // And the last step is to send these information to the frontend to render the diagram.

    forceDirected(nodes, edges, 100, 800, 0.5, 100);

    for (Node node: nodes) {
      System.out.println(node.getName() + " x: " + node.getX() + ", y: " + node.getY());
    }
  }

  public static void forceDirected(List<Node> nodes, List<Edge> edges, int numOfIterations, int lengthOfSpring, double attenuation, int epsilon) {
    int currentIterations = 0;
    while (currentIterations < numOfIterations) {
      List<Vector> Fs = new ArrayList<>();
      // The first step is to calculate all of the displacement vector
      for (Node u: nodes) {
        Vector fu = new Vector(u.getId(), 0, 0);
        for (Node v: nodes) {
          if (u.getId().equals(v.getId())) {
            continue;
          }
          double euclideanD = Math.sqrt(Math.pow(v.getX() - u.getX(), 2) + Math.pow(v.getY() - u.getY(), 2));
          double xd = u.getX() - v.getX();
          double xy = u.getY() - v.getY();
          double root = Math.sqrt(Math.pow(xd, 2) + Math.pow(xy, 2));
          double unitVectorX = xd / root;
          double unitVectorY = xy / root;
          double leftPart = Math.pow(lengthOfSpring, 2) / euclideanD;
          double fx = leftPart * unitVectorX;
          double fy = leftPart * unitVectorY;
          fu.setX(fu.getX() + fx);
          fu.setY(fu.getY() + fy);
        }

        for (Edge edge: edges) {
          if (edge.getSourceNode().getId().equals(u.getId())) {
            Node v = edge.getTargetNode();
            double euclideanD = Math.sqrt(Math.pow(v.getX() - u.getX(), 2) + Math.pow(v.getY() - u.getY(), 2));
            double leftPart = Math.pow(euclideanD, 2) / lengthOfSpring;
            double xd = v.getX() - u.getX();
            double xy = v.getY() - u.getY();
            double root = Math.sqrt(Math.pow(xd, 2) + Math.pow(xy, 2));
            double unitVectorX = xd / root;
            double unitVectorY = xy / root;
            double fx = unitVectorX / root;
            double fy = unitVectorY / root;
            fu.setX(fu.getX() + fx);
            fu.setY(fu.getY() + fy);
          } else if (edge.getTargetNode().getId().equals(u.getId())) {
            Node v = edge.getSourceNode();
            double euclideanD = Math.sqrt(Math.pow(v.getX() - u.getX(), 2) + Math.pow(v.getY() - u.getY(), 2));
            double leftPart = Math.pow(euclideanD, 2) / lengthOfSpring;
            double xd = v.getX() - u.getX();
            double xy = v.getY() - u.getY();
            double root = Math.sqrt(Math.pow(xd, 2) + Math.pow(xy, 2));
            double unitVectorX = xd / root;
            double unitVectorY = xy / root;
            double fx = unitVectorX / root;
            double fy = unitVectorY / root;
            fu.setX(fu.getX() + fx);
            fu.setY(fu.getY() + fy);
          }
        }

        Fs.add(fu);
      }

      // Then update all nodes' position
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        Vector vector = Fs.get(i);
        node.setX(node.getX() + attenuation * vector.getX());
        node.setY(node.getY() + attenuation * vector.getY());
      }

      for (Node node: nodes) {
        System.out.println(currentIterations);
        System.out.println(node.getName() + " x: " + node.getX() + ", y: " + node.getY());
      }
      currentIterations++;
    }
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

  public static void testSql() throws ParseException {
    Schema example = ER.createSchema("TestSQL");
    Entity student = example.addEntity("student");
    Attribute name = student.addPrimaryKey("name", DataType.TEXT);

    Entity project = example.addEntity("project");
    project.addPrimaryKey("pname", DataType.TEXT);

    Relationship works_in = example.createRelationship("works_in", student, project, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    String ddl = example.generateSqlStatement();

    System.out.println(ddl);
//
//    student.deleteAttribute(name);
//    student.addPrimaryKey("age", DataType.TEXT);

//    for (Table table: example.getOldTables()) {
//      System.out.println("OldTable: " + table);
//    }

    example.deleteEntity(student);
    Entity person = example.addEntity("person");
    person.addPrimaryKey("pname", DataType.TEXT);

    for (RelationshipEdge relationshipEdge: works_in.getEdgeList()) {
      if (relationshipEdge.getConnObj().getID().equals(student.getID())) {
        works_in.deleteEdge(relationshipEdge);
        break;
      }
    }
    works_in.linkObj(person, Cardinality.ZeroToMany);

    String ddl2 = example.generateSqlStatement();

    System.out.println(ddl2);
  }

}
