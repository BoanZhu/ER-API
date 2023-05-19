package io.github.MigadaTang;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Link.to;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {

  public static void main(String[] args)
      throws DBConnectionException, ParseException, SQLException, IOException {
    System.out.println("Try to test database connection!");
    ER.initialize(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722", "wh722", "4jC@A3528>0N6");
//    testReverseEngineer();
//    testSqlGeneration();
//    testSql();
//    testLayout();

    Schema example = ER.createSchema("TestSQL");
    Entity student = example.addEntity("A");
    student.addPrimaryKey("name", DataType.TEXT);

    Entity project = example.addEntity("B");
    project.addPrimaryKey("pname", DataType.TEXT);

    Entity course = example.addEntity("C");
    Entity D = example.addEntity("D");
    Entity E = example.addEntity("E");

    Relationship AB = example.createRelationship("AB", student, project, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    Relationship AC = example.createRelationship("AC", student, course, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    Relationship AD = example.createRelationship("AD", student, D, Cardinality.ZeroToMany, Cardinality.ZeroToMany);
    Relationship BE = example.createRelationship("BE", course, E, Cardinality.ZeroToMany, Cardinality.ZeroToMany);

    useGraphviz(example);
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

    List<GraphNode> graphNodes = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();

    for (Entity entity: entities) {
      GraphNode graphNode = GraphNode.transformNode(entity);
      System.out.println("node: " + graphNode.getX() + " " + graphNode.getY());
      graphNodes.add(graphNode);
    }

    for (Relationship relationship: relationships) {
      Edge edge = Edge.transformEdge(relationship, graphNodes);
      edges.add(edge);
    }

    // The entities and relationships can be used to get a connected graph
    // We can use this graph as the input and use Force-Directed layout algorithm to iterate a lot of
    // times to get a appropriate position.
    // And the last step is to send these information to the frontend to render the diagram.

    forceDirected(graphNodes, edges, 100, 200, 0.98, 100);

    for (GraphNode graphNode : graphNodes) {
      System.out.println(graphNode.getName() + " x: " + graphNode.getX() + ", y: " + graphNode.getY());
    }
  }

  public static void forceDirected(List<GraphNode> graphNodes, List<Edge> edges, int numOfIterations, int lengthOfSpring, double attenuation, int epsilon) {
    int currentIterations = 0;
    while (currentIterations < numOfIterations) {
      List<Vector> Fs = new ArrayList<>();
      // The first step is to calculate all of the displacement vector
      for (GraphNode u: graphNodes) {
        Vector fu = new Vector(u.getId(), 0, 0);
        for (GraphNode v: graphNodes) {
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
          if (edge.getSourceGraphNode().getId().equals(u.getId())) {
            GraphNode v = edge.getTargetGraphNode();
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
          } else if (edge.getTargetGraphNode().getId().equals(u.getId())) {
            GraphNode v = edge.getSourceGraphNode();
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
      for (int i = 0; i < graphNodes.size(); i++) {
        GraphNode graphNode = graphNodes.get(i);
        Vector vector = Fs.get(i);
        graphNode.setX(graphNode.getX() + attenuation * vector.getX());
        graphNode.setY(graphNode.getY() + attenuation * vector.getY());
      }

      for (GraphNode graphNode : graphNodes) {
        System.out.println(currentIterations);
        System.out.println(graphNode.getName() + " x: " + graphNode.getX() + ", y: " + graphNode.getY());
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

  public static void useGraphviz(Schema schema) throws IOException {

    Graph g = graph(schema.getName()).directed()
        .nodeAttr().with(Font.name("arial"))
        .linkAttr().with("class", "link-class");

    List<Node> nodeList = new ArrayList<>();
    for (Entity entity: schema.getEntityList()) {
      Node node = node(entity.getName());
      nodeList.add(node);
    }
    for (Relationship relationship: schema.getRelationshipList()) {
      Node node = node(relationship.getName());
      nodeList.add(node);
    }

    for (Node node: nodeList) {
      System.out.println(node);
    }

    for (Relationship relationship: schema.getRelationshipList()) {
      Node relationshipNode = findNode(relationship.getName(), nodeList);
      for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
        String nodeName = relationshipEdge.getConnObj().getName();
        Node node = findNode(nodeName, nodeList);
        System.out.println("relationshipNode: " + relationshipNode + ", node: " + node);
        g.with(node.link(to(relationshipNode)));
      }
    }

    System.out.println(g);

    Graphviz.fromGraph(g).width(900).height(900).render(Format.PNG).toFile(new File("example/ex2.png"));

    Graph gg = graph("example1").directed()
//        .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
        .nodeAttr().with(Font.name("arial"))
        .linkAttr().with("class", "link-class")
        .with(
            node("a").with(Color.RED).link(node("b")),
            node("b").link(
                to(node("c")).with(attr("weight", 5), Style.DASHED)
            )
        );

    System.out.println(gg);
    Graphviz.fromGraph(gg).height(100).render(Format.PNG).toFile(new File("example/ex1.png"));
  }

  public static Node findNode(String nodeName, List<Node> nodeList) {
    for (Node node: nodeList) {
      if (node.name().toString().equals(nodeName)) {
        return node;
      }
    }
    return null;
  }

}
