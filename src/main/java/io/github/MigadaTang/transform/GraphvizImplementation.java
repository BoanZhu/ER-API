package io.github.MigadaTang.transform;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.to;

import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.LayoutInfo;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.RelationshipEdge;
import io.github.MigadaTang.Schema;
import io.github.MigadaTang.common.BelongObjType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class GraphvizImplementation {

//  public static void useGraphviz(Schema schema) throws IOException {
//    Graph g = graph(schema.getName())
//        .nodeAttr().with(Font.name("arial"))
//        .linkAttr().with("class", "link-class");
//
//    List<Node> nodeList = new ArrayList<>();
//    for (Entity entity: schema.getEntityList()) {
//      Node node = node(entity.getName()).with(Shape.RECTANGLE).with(attr("height", 1));
//      nodeList.add(node);
//    }
//    for (Relationship relationship: schema.getRelationshipList()) {
//      Node node = node(relationship.getName()).with(Shape.DIAMOND);
//      nodeList.add(node);
//    }
//
//    for (Node node: nodeList) {
//      System.out.println(node);
//    }
//
//    for (Relationship relationship: schema.getRelationshipList()) {
//      Node relationshipNode = findNode(relationship.getName(), nodeList);
//      for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
//        String nodeName = relationshipEdge.getConnObj().getName();
//        Node node = findNode(nodeName, nodeList);
//        System.out.println("relationshipNode: " + relationshipNode + ", node: " + node);
//        g = g.with(node.link(to(relationshipNode)));
//      }
//    }
//
//    // render the graph into png.
////    Graphviz.fromGraph(g).engine(Engine.NEATO).width(1200).height(1200).render(
////        Format.PNG).toFile(new File("example/ex2.png"));
//
//    // render the graph into Json format so that we can extract the position information.
//    Graphviz viz = Graphviz.fromGraph(g);
//    Graphviz.useEngine(new GraphvizV8Engine());
//    String jsonString = viz.engine(Engine.NEATO).width(1200).height(1200).render(Format.JSON).toString();
//    JSONObject jsonObject = new JSONObject(jsonString);
//    System.out.println("-----------------");
//
//    // Extract the layout information from the Json object.
//    for (Object node: (JSONArray) jsonObject.get("objects")) {
////      System.out.println("name: " + ((JSONObject ) node).get("name") + ", pos: " + ((JSONObject ) node).get("pos"));
//      String[] pos = ((JSONObject ) node).get("pos").toString().split(",");
//
//      for (Entity entity: schema.getEntityList()) {
//        if (entity.getName().equals(((JSONObject ) node).get("name"))) {
//          entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(),
//              BelongObjType.ENTITY, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
////          System.out.println(entity.getName() + " " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
//        }
//      }
//
//      for (Relationship relationship: schema.getRelationshipList()) {
//        if (relationship.getName().equals(((JSONObject ) node).get("name"))) {
//          relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(),
//              BelongObjType.RELATIONSHIP, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
////          System.out.println(relationship.getName() + " " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
//        }
//      }
//
//    }
//
//  }

  public static void useGraphviz(Schema schema) throws IOException {

    Graph g = graph(schema.getName())
        .nodeAttr().with(Font.name("arial"))
        .linkAttr().with("class", "link-class");

    List<Node> nodeList = new ArrayList<>();
    for (Entity entity: schema.getEntityList()) {
      Node node = node(entity.getName()).with(Shape.RECTANGLE);
      nodeList.add(node);
    }
    for (Relationship relationship: schema.getRelationshipList()) {
      Node node = node(relationship.getName()).with(Shape.DIAMOND);
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
        g = g.with(node.link(to(relationshipNode)));
      }
    }

    g.with(node("abcde").link("12345"));
    System.out.println(g);

    // render the graph into png.
    Graphviz.useEngine(new GraphvizV8Engine());
//    Graphviz.fromGraph(g).engine(Engine.NEATO).width(1200).height(1200).render(Format.PNG).toFile(new File("example/ex2.png"));

    // render the graph into Json format so that we can extract the position information.
    String jsonString = Graphviz.fromGraph(g).engine(Engine.NEATO).width(1200).height(1200).render(Format.JSON).toString();
    JSONObject jsonObject = new JSONObject(jsonString);
    System.out.println("-----------------");

    // Extract the layout information from the Json object.
    for (Object node: (JSONArray) jsonObject.get("objects")) {
      System.out.println("name: " + ((JSONObject ) node).get("name") + ", pos: " + ((JSONObject ) node).get("pos"));
      String[] pos = ((JSONObject ) node).get("pos").toString().split(",");
      for (Entity entity: schema.getEntityList()) {
        if (entity.getName().equals(((JSONObject ) node).get("name"))) {
          entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(),
              BelongObjType.ENTITY, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
          System.out.println(entity.getName() + " " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
        }
      }
      for (Relationship relationship: schema.getRelationshipList()) {
        if (relationship.getName().equals(((JSONObject ) node).get("name"))) {
          relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(),
              BelongObjType.RELATIONSHIP, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
          System.out.println(relationship.getName() + " " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
        }
      }

    }

    // Firstly, initialise the grid with zero.
    int numOfEntities = schema.getEntityList().size() - 1;
    String[][] grid = new String[numOfEntities][numOfEntities];
    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        grid[i][j] = "*";
      }
    }

    // e.g largestX = 709, so finalWidth = ((709 / 100) + 1) * 100 = 800.
    double finalWidth = findWidth(schema.getEntityList());
    double finalHeight = findHeight(schema.getEntityList());

    // Secondly, for each entities and relationships, we need to find its nearest grid point.
    List<int[]> points = new ArrayList<>();
    for (Relationship relationship: schema.getRelationshipList()) {
      LayoutInfo layoutInfo = relationship.getLayoutInfo();
      double x = layoutInfo.getLayoutX();
      double y = layoutInfo.getLayoutY();
      int[] point = findNearestGridPoint(grid, x, y, finalWidth, finalHeight, relationship.getName());
      points.add(point);
    }

    for (Entity entity: schema.getEntityList()) {
      LayoutInfo layoutInfo = entity.getLayoutInfo();
      double x = layoutInfo.getLayoutX();
      double y = layoutInfo.getLayoutY();
      int[] point = findNearestGridPoint(grid, x, y, finalWidth, finalHeight, entity.getName());
      points.add(point);
    }

    for (int[] point: points) {
      System.out.println(point[0] + ", " + point[1]);
    }

    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        System.out.print(grid[i][j] + ", ");
      }
      System.out.println();
    }

    reverseByY(grid);
//    leftRotate(grid);
    System.out.println("----------------------");
    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        System.out.print(grid[i][j] + ", ");
      }
      System.out.println();
    }

    // Thirdly, we need to map the grid point into actual position layouts.
    String[][] finalGridPositions = new String[numOfEntities][numOfEntities];
    int gridWidth = 1000;
    int gridHeight = 1000;
    int widthPerGrid = gridWidth / (numOfEntities + 2);
    int heightPerGrid = gridHeight / (numOfEntities + 2);
    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        finalGridPositions[i][j] = widthPerGrid * (i + 1) + "," + heightPerGrid * (j + 1);
      }
    }
//    mapGridPoints(points, schema.getEntityList());

    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        System.out.print(finalGridPositions[i][j] + ", ");
      }
      System.out.println();
    }

    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        if (!grid[i][j].equals("*")) {
          String name = grid[i][j];
          for (Entity entity: schema.getEntityList()) {
            if (entity.getName().equals(name)) {
              String[] position = finalGridPositions[i][j].split(",");
              double x = Double.parseDouble(position[0]);
              double y = Double.parseDouble(position[1]);
              entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(), BelongObjType.ENTITY,
                  x, y));
              break;
            }
          }

          for (Relationship relationship: schema.getRelationshipList()) {
            if (relationship.getName().equals(name)) {
              String[] position = finalGridPositions[i][j].split(",");
              double x = Double.parseDouble(position[0]);
              double y = Double.parseDouble(position[1]);
              relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(), BelongObjType.RELATIONSHIP,
                  x, y));
              break;
            }
          }
        }
      }
    }

//    for (Entity entity: schema.getEntityList()) {
//      System.out.println(entity.getName() + ": " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
//    }
//
//    for (Relationship relationship: schema.getRelationshipList()) {
//      System.out.println(relationship.getName() + ": " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
//    }
  }

  public static Node findNode(String nodeName, List<Node> nodeList) {
    for (Node node: nodeList) {
      if (node.name().toString().equals(nodeName)) {
        return node;
      }
    }
    return null;
  }

  public static double findWidth(List<Entity> entities) {
    double largestX = 0;
    for (Entity entity: entities) {
      if (entity.getLayoutInfo().getLayoutX() > largestX) {
        largestX = entity.getLayoutInfo().getLayoutX();
      }
    }
    return ((largestX / 100) + 1) * 100;
  }

  public static double findHeight(List<Entity> entities) {
    double largestY = 0;
    for (Entity entity: entities) {
      if (entity.getLayoutInfo().getLayoutY() > largestY) {
        largestY = entity.getLayoutInfo().getLayoutY();
      }
    }
    return ((largestY / 100) + 1) * 100;
  }

  public static int[] findNearestGridPoint(String[][] grid, double x, double y, double width,
      double height, String name) {
    int[] point = new int[2];
    double widthPerGrid = width / (grid.length - 1);
    double heightPerGrid = height / (grid.length - 1);
    double gridX = x / widthPerGrid;
    double gridY = y / heightPerGrid;

    double smallestDistance = 100; // magic number here, need to be improved.
    int finalX = 0;
    int finalY = 0;
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (!grid[i][j].equals("*")) {
          continue;
        }
        double distance = Math.sqrt(Math.pow((gridX - i), 2) + Math.pow((gridY - j), 2));
        if (distance < smallestDistance) {
          smallestDistance = distance;
          finalX = i;
          finalY = j;
        }
      }
    }

    point[0] = finalX;
    point[1] = finalY;
    grid[finalX][finalY] = name;

    return point;
  }

  public static void reverseByY(String[][] grid) {
    for (int i = 0; i < grid.length; i++) {
      int low = 0;
      int high = grid.length - 1;
      while (low < high) {
        String temp = grid[i][low];
        grid[i][low] = grid[i][high];
        grid[i][high] = temp;
        low++;
        high--;
      }
    }
  }

}

