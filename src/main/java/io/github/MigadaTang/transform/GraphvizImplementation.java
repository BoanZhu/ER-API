package io.github.MigadaTang.transform;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.model.Link.to;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.GraphAttr;
import guru.nidi.graphviz.attribute.GraphAttr.SplineMode;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.NodeAttr;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Size;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.model.Node;
import guru.nidi.graphviz.parse.Position;
import io.github.MigadaTang.Attribute;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.LayoutInfo;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.RelationshipEdge;
import io.github.MigadaTang.Schema;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.EntityType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class GraphvizImplementation {

  public static void useGraphviz(Schema schema) throws IOException {

//    Graph g = graph(schema.getName())
////        .graphAttr().with(GraphAttr.sizePreferred())
////        .graphAttr().with(GraphAttr.sizePreferred(1000))
////        .graphAttr().with()
//        .nodeAttr().with(Font.name("arial"))
////        .nodeAttr().with(new Position("123"))
//        .nodeAttr().with(Font.size(7));
////        .linkAttr().with("class", "link-class");
    MutableGraph g = mutGraph(schema.getName()).setDirected(false)
        .graphAttrs().add("size", "15,15!")
        .graphAttrs().add("overlap", "scale");

    List<MutableNode> nodeList = new ArrayList<>();
    List<MutableNode> subsetList = new ArrayList<>();
    List<Entity> subsets = new ArrayList<>();
    List<Entity> generalisations = new ArrayList<>();

    for (Entity entity: schema.getEntityList()) {
      if (entity.getEntityType() == EntityType.SUBSET) {
        subsets.add(entity);
      }
    }

    for (Entity subset: subsets) {
      for (Entity otherSubset: subsets) {
        if (otherSubset.equals(subset)) {
          continue;
        }
        if (otherSubset.getBelongStrongEntity().equals(subset.getBelongStrongEntity())) {
          generalisations.add(subset);
          break;
        }
      }
    }

//    for (Entity subset: subsets) {
//      System.out.println("Subset: " + subset.getName());
//    }
//
//    for (Entity generalisation: generalisations) {
//      System.out.println("generalisation: " + generalisation.getName());
//    }

    List<List<Integer>> positionsForSuperEntity = new ArrayList<>();
    List<Integer> position1 = new ArrayList<>();
    position1.add(6);
    position1.add(4);
    positionsForSuperEntity.add(position1);
    List<Integer> position2 = new ArrayList<>();
    position2.add(2);
    position2.add(6);
    positionsForSuperEntity.add(position2);

    Map<String, List<Integer>> superEntityMap = new HashMap<>();

    int n = 0;
    while (generalisations.size() > 0) {
      List<Entity> tempList = new ArrayList<>();
      for (Entity entity: generalisations) {
        if (superEntityMap.get(entity.getName()) != null) {
          continue;
        }

        Entity relyOnEntity = entity.getBelongStrongEntity();

        if (superEntityMap.get(relyOnEntity.getName()) == null) {
          if (relyOnEntity.getEntityType() == EntityType.SUBSET) {
            tempList.add(entity);
            continue;
          } else {

            List<Integer> position = positionsForSuperEntity.get(n);
            superEntityMap.put(relyOnEntity.getName(), position);
            n++;
            int x = position.get(0);
            int y = position.get(1);

            List<Entity> children = new ArrayList<>();
            for (Entity generalisation: generalisations) {
              if (generalisation.getBelongStrongEntity().getName().equals(relyOnEntity.getName())) {
                children.add(generalisation);
              }
            }

            int numOfChildren = children.size();

            boolean whetherOdd = numOfChildren % 2 != 0;
            int startPointX;
            if (whetherOdd) {
              startPointX = x - numOfChildren / 2 * 2;
            } else {
              startPointX = x - numOfChildren / 2 * 2 + 2;
            }
            int startPointY = y - 1;

            for (int i = 0; i < numOfChildren; i++) {
              Entity generalisation = children.get(i);
              int newX = startPointX + i * 2;
              int newY = startPointY;
              List<Integer> newPosition = new ArrayList<>();
              newPosition.add(newX);
              newPosition.add(newY);
              superEntityMap.put(generalisation.getName(), newPosition);
            }

          }
        } else {
          // in this case, the rely on generalisation entity has the position.
          List<Integer> position = superEntityMap.get(relyOnEntity.getName());
          int x = position.get(0);
          int y = position.get(1);

          List<Entity> children = new ArrayList<>();
          for (Entity generalisation: generalisations) {
            if (generalisation.getBelongStrongEntity().getName().equals(relyOnEntity.getName())) {
              children.add(generalisation);
            }
          }

          int numOfChildren = children.size();

          boolean whetherOdd = numOfChildren % 2 != 0;
          int startPointX;
          if (whetherOdd) {
            startPointX = x - numOfChildren / 2 * 2;
          } else {
            startPointX = x - numOfChildren / 2 * 2 + 2;
          }
          int startPointY = y - 1;

          for (int i = 0; i < numOfChildren; i++) {
            Entity generalisation = children.get(i);
            int newX = startPointX + i * 2;
            int newY = startPointY;
            List<Integer> newPosition = new ArrayList<>();
            newPosition.add(newX);
            newPosition.add(newY);
            superEntityMap.put(generalisation.getName(), newPosition);
          }

        }

      }
      generalisations = tempList;
    }


//    for (String str: superEntityMap.keySet()) {
//      System.out.println("name: " + str + ", " + superEntityMap.get(str));
//    }

    for (Entity entity: schema.getEntityList()) {

      // In this case, the superclass entity only has one subset
      if (subsets.contains(entity) && !superEntityMap.containsKey(entity.getName())) {
        // We will not put this kind of subset in the graphviz graph, they will be put at the bottom
        // of its relying on entity.
        continue;
      }

      // In this case, the superclass entity has multiple subsets (generalisations)
//      if (generalisations.contains(entity)) {
//        if (superEntityMap.get(entity.getBelongStrongEntity().getName()) == null) {
//          superEntityMap.put(entity.getBelongStrongEntity().getName(), positionsForSuperEntity.get(n));
//          n++;
//        }
//      }
      if (superEntityMap.containsKey(entity.getName())) {
//        System.out.println("123: " + entity.getName());
        List<Integer> position = superEntityMap.get(entity.getName());
        int x = position.get(0);
        int y = position.get(1);
        MutableNode node = mutNode(entity.getName()).add(Shape.RECTANGLE).add(Font.size(7)).add("pos", x + "," + y + "!");
        nodeList.add(node);
        subsetList.add(node);
      } else {
        MutableNode node = mutNode(entity.getName()).add(Shape.RECTANGLE).add(Font.size(7));
        nodeList.add(node);
      }
//      MutableNode node = mutNode(entity.getName()).add(Shape.RECTANGLE).add(Font.size(7));
//      nodeList.add(node);
    }

    for (Relationship relationship: schema.getRelationshipList()) {
//      Node node = node(relationship.getName()).with(Shape.DIAMOND);
      MutableNode node = mutNode(relationship.getName()).add(Shape.DIAMOND).add(Font.size(7));
      nodeList.add(node);
    }

//    for (MutableNode node: nodeList) {
//      System.out.println("Node: " + node);
//    }

    for (Relationship relationship: schema.getRelationshipList()) {
//      Node relationshipNode = findNode(relationship.getName(), nodeList);
      MutableNode relationshipNode = findNode(relationship.getName(), nodeList);
      List<RelationshipEdge> edges = relationship.getEdgeList();
      for (RelationshipEdge relationshipEdge: edges) {
        // todo: check whether the relationship is used for connecting two subsets.
        if (relationship.getEdgeList().size() == 2) {
          RelationshipEdge edge1 = edges.get(0);
          RelationshipEdge edge2 = edges.get(1);
          MutableNode node1 = findNode(edge1.getConnObj().getName(), subsetList);
          MutableNode node2 = findNode(edge2.getConnObj().getName(), subsetList);
          if (node1 != null && node2 != null && node1 != node2) {
            break;
          }
        }

        String nodeName = relationshipEdge.getConnObj().getName();
//        Node node = findNode(nodeName, nodeList);
        MutableNode node = findNode(nodeName, nodeList);
//        g = g.with(node.link(to(relationshipNode)));

        g.add(relationshipNode.addLink(node));
      }
    }

    for (Entity entity: schema.getEntityList()) {
      if (entity.getEntityType() == EntityType.SUBSET) {
//        Node subsetNode = findNode(entity.getName(), nodeList);
//        Node relyOnNode = findNode(entity.getBelongStrongEntity().getName(), nodeList);
//        g = g.with(subsetNode.link(to(relyOnNode)));
        MutableNode subsetNode = findNode(entity.getName(), nodeList);
        MutableNode relyOnNode = findNode(entity.getBelongStrongEntity().getName(), nodeList);
        g.add(subsetNode.addLink(relyOnNode));
      }
    }
//    g = g.graphAttr().with(GraphAttr.splines(SplineMode.ORTHO));

    // render the graph into png.
    Graphviz.useEngine(new GraphvizV8Engine());
//    Graphviz.fromGraph(g).engine(Engine.FDP).width(3000).render(Format.PNG).toFile(new File("example/ex45.png"));

    // render the graph into Json format so that we can extract the position information.
//    Graphviz.fromGraph(g).engine(Engine.NEATO).render(Format.PNG).toFile(new File("example/ex1.png"));

//     render the graph into Json format so that we can extract the position information.
    String jsonString = Graphviz.fromGraph(g).engine(Engine.FDP).width(3000).render(Format.JSON).toString();
    JSONObject jsonObject = new JSONObject(jsonString);
//    System.out.println("--------------------------- g:");
//    System.out.println(g);
//    System.out.println("json: -------------");
//    System.out.println(jsonObject);

    // Extract the layout information from the Json object.
    for (Object node: (JSONArray) jsonObject.get("objects")) {
      String[] pos = ((JSONObject ) node).get("pos").toString().split(",");
      boolean find = false;

      String nodeName = (String) ((JSONObject ) node).get("name");

      for (Entity entity: schema.getEntityList()) {
        String entityName = entity.getName();
        if (entityName.equals(nodeName)) {
          find = true;

          entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(),
              BelongObjType.ENTITY, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
          break;
        }
      }

      if (find) {
        continue;
      }
      for (Relationship relationship: schema.getRelationshipList()) {
        String relationshipName = relationship.getName();

        if (relationshipName.equals(nodeName)) {

          relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(),
              BelongObjType.RELATIONSHIP, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
          break;
        }
      }
    }

//    System.out.println("---------------------------------");
//    System.out.println("entities size: " + schema.getEntityList().size());
//    System.out.println("relationships size: " + schema.getRelationshipList().size());
//    System.out.println("nodes size: " + nodeList.size());

    // Firstly, initialise the grid with zero.
    int numOfEntities = schema.getEntityList().size() - 1;

    int gridWidth = ((numOfEntities / 10) + 2) * 1000;
    int gridHeight = ((numOfEntities / 10) + 2) * 1000;

    if (numOfEntities < 3) {
      numOfEntities = 3;
    } else if (3 < numOfEntities && numOfEntities <= 5) {
      numOfEntities = 5;
    } else if (5 < numOfEntities && numOfEntities <= 7) {
      numOfEntities = 7;
    } else if (7 < numOfEntities && numOfEntities <= 10){
      numOfEntities = 9;
    } else {
      numOfEntities = schema.getEntityList().size() / 2 + 6; /// 从1改成了6，先试试
    }

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

//    for (Entity entity: schema.getEntityList()) {
//      if (subsets.contains(entity) && !superEntityMap.containsKey(entity.getName())) {
//
//      }
//    }

    for (Entity entity: schema.getEntityList()) {
      LayoutInfo layoutInfo = entity.getLayoutInfo();
      if (layoutInfo != null) {
        double x = layoutInfo != null ? layoutInfo.getLayoutX() : 100;
        double y = layoutInfo != null ? layoutInfo.getLayoutY() : 100;
        int[] point = findNearestGridPoint(grid, x, y, finalWidth, finalHeight, entity.getName());
        points.add(point);
      }
    }

    for (Entity entity: schema.getEntityList()) {
      LayoutInfo layoutInfo = entity.getLayoutInfo();
      if (layoutInfo == null) {
        // in this case, this is a subset entity.
        System.out.println("find a single subset!!!" + entity.getName());
        if (entity.getEntityType() != EntityType.SUBSET) {
//          throw new IOException("Non-subset found...");
          break;
        }
        Entity relyOnEntity = entity.getBelongStrongEntity();
        for (int i = 0; i < numOfEntities; i++) {
          for (int j = 0; j < numOfEntities; j++) {
            if (grid[i][j].equals(relyOnEntity.getName())) {
              grid[i][j - 1] = entity.getName();
              break;
            }
          }
        }
      }
    }

    for (Relationship relationship: schema.getRelationshipList()) {
      LayoutInfo layoutInfo = relationship.getLayoutInfo();

      if (layoutInfo == null) {
        continue;
      }
//      double x = layoutInfo != null ? layoutInfo.getLayoutX() : 100;
//      double y = layoutInfo != null ? layoutInfo.getLayoutY() : 100;
      double x = layoutInfo.getLayoutX();
      double y = layoutInfo.getLayoutY();
      int[] point = findNearestGridPoint(grid, x, y, finalWidth, finalHeight, relationship.getName());
      points.add(point);

//      System.out.println("RELATIONSHIP: " + relationship.getName() + ", edges size: " + relationship.getEdgeList().size());
//      for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
//        System.out.println("edge: " + relationshipEdge.getConnObj().getName());
//      }

    }

    reverseByY(grid);

    // Thirdly, we need to map the grid point into actual position layouts.
    String[][] finalGridPositions = new String[numOfEntities][numOfEntities];
//    int gridWidth = numOfEntities <= 10 ? 1000 : 2000;
//    gridWidth = 5000;
//    int gridHeight = numOfEntities <= 10 ? 1000 : 2000;
//    gridHeight = 5000;
    int widthPerGrid = gridWidth / (numOfEntities + 2);
    int heightPerGrid = gridHeight / (numOfEntities + 2);
    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        finalGridPositions[i][j] = widthPerGrid * (i + 1) + "," + heightPerGrid * (j + 1);
      }
    }

    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        if (!grid[i][j].equals("*")) {
          String name = grid[i][j];
          boolean find = false;
          for (Entity entity: schema.getEntityList()) {
            if (entity.getName().equals(name)) {
              find = true;
              String[] position = finalGridPositions[i][j].split(",");
              double x = Double.parseDouble(position[0]);
              double y = Double.parseDouble(position[1]);
              entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(), BelongObjType.ENTITY,
                  x, y));
//              System.out.println("layoutInfo entity: " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
              break;
            }
          }

          if (find) {
            continue;
          }

          for (Relationship relationship: schema.getRelationshipList()) {
            if (relationship.getName().equals(name)) {
              String[] position = finalGridPositions[i][j].split(",");
              double x = Double.parseDouble(position[0]);
              double y = Double.parseDouble(position[1]);
              relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(), BelongObjType.RELATIONSHIP,
                  x, y));
//              System.out.println("layoutInfo relationship: " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
              break;
            }
          }
        }
      }
    }

    for (Relationship relationship: schema.getRelationshipList()) {
      if (relationship.getLayoutInfo() == null) {
        List<RelationshipEdge> edges = relationship.getEdgeList();
        Entity subset1 = findSubset(edges.get(0).getConnObj().getName(), schema.getEntityList());
        Entity subset2 = findSubset(edges.get(1).getConnObj().getName(), schema.getEntityList());
        if (subset1 == null || subset2 == null) {
          throw new IOException("Subset issue happens.");
        }
        double x = (subset1.getLayoutInfo().getLayoutX() + subset2.getLayoutInfo().getLayoutX()) / 2;
        double y = (subset1.getLayoutInfo().getLayoutY() + subset2.getLayoutInfo().getLayoutY()) / 2;
        relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(), BelongObjType.RELATIONSHIP,
            x, y));
      }
    }

    // At the end, we need to set the positions of all the attributes.
    for (Entity entity: schema.getEntityList()) {

//      boolean whetherOnTheSides = false;
      boolean onTheLeft = false;
      boolean onTheRight = false;
      for (int i = 0; i < numOfEntities; i++) {
        for (int j = 0; j < numOfEntities; j++) {
          if (grid[i][j].equals(entity.getName())) {
            // check whether the entity is on the sides of the grid.
            if (i == 0 || whetherLeftist(grid, i, j)) {
              onTheLeft = true;
              break;
            }
            if (i == numOfEntities - 1 || whetherRightist(grid, i, j)) {
              onTheRight = true;
              break;
            }
            break;
          }
        }
      }

      double entityX = entity.getLayoutInfo().getLayoutX();
      double entityY = entity.getLayoutInfo().getLayoutY();
      int numOfAttributes = entity.getAttributeList().size();

      List<Double> positions = generateAttributePositions(numOfAttributes);
      List<List<Double>> positionsAround = generateAttributePositionsAround(entityX, entityY, numOfAttributes);
      int index = 0;
      for (Attribute attribute: entity.getAttributeList()) {

        // If the entity is on the sides of the grid, the attributes should also on the sides of the grid.
        // Otherwise, the attributes should put around the entity.
        if (onTheLeft) {
          double x = entityX - 60;
          double y = entityY + positions.get(index);
          attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
              x, y));
        } else if (onTheRight) {
          double x = entityX + 100 + 60;
          double y = entityY + positions.get(index);
          attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
              x, y));
        } else {
          List<Double> position = positionsAround.get(index);
          double x = position.get(0);
          double y = position.get(1);
          attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
              x, y));
        }

        index++;
      }
    }

    for (Relationship relationship: schema.getRelationshipList()) {
      double relationshipX = relationship.getLayoutInfo().getLayoutX();
      double relationshipY = relationship.getLayoutInfo().getLayoutY();
      int numOfAttributes = relationship.getAttributeList().size();
      List<List<Double>> positionsAround = generateAttributePositionsAround(relationshipX, relationshipY, numOfAttributes);

      int index = 0;
      for (Attribute attribute: relationship.getAttributeList()) {
        List<Double> position = positionsAround.get(index);
        double x = position.get(0);
        double y = position.get(1);
        attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
            x, y));
        index++;
      }
    }

//    for (Entity entity: schema.getEntityList()) {
//      System.out.println(entity.getName() + ": " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
//    }
//    for (Relationship relationship: schema.getRelationshipList()) {
//      System.out.println(relationship.getName() + ": " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
//    }
  }

  public static void useGraphvizWithoutSubsetRequirement(Schema schema) throws IOException {

    MutableGraph g = mutGraph(schema.getName()).setDirected(false)
        .graphAttrs().add("size", "20,20!")
        .graphAttrs().add("overlap", "scale");

    List<MutableNode> nodeList = new ArrayList<>();

    for (Entity entity: schema.getEntityList()) {
      MutableNode node = mutNode(entity.getName()).add(Shape.RECTANGLE).add(Font.size(7));
      nodeList.add(node);
    }

    for (Relationship relationship: schema.getRelationshipList()) {
//      Node node = node(relationship.getName()).with(Shape.DIAMOND);
      MutableNode node = mutNode(relationship.getName()).add(Shape.DIAMOND).add(Font.size(7));
      nodeList.add(node);
    }

    for (MutableNode node: nodeList) {
      System.out.println("Node: " + node);
    }

    for (Relationship relationship: schema.getRelationshipList()) {
//      Node relationshipNode = findNode(relationship.getName(), nodeList);
      MutableNode relationshipNode = findNode(relationship.getName(), nodeList);
      List<RelationshipEdge> edges = relationship.getEdgeList();
      for (RelationshipEdge relationshipEdge: edges) {

        String nodeName = relationshipEdge.getConnObj().getName();
//        Node node = findNode(nodeName, nodeList);
        MutableNode node = findNode(nodeName, nodeList);
//        g = g.with(node.link(to(relationshipNode)));

        g.add(relationshipNode.addLink(node));
      }
    }

    for (Entity entity: schema.getEntityList()) {
      if (entity.getEntityType() == EntityType.SUBSET) {
//        Node subsetNode = findNode(entity.getName(), nodeList);
//        Node relyOnNode = findNode(entity.getBelongStrongEntity().getName(), nodeList);
//        g = g.with(subsetNode.link(to(relyOnNode)));
        MutableNode subsetNode = findNode(entity.getName(), nodeList);
        MutableNode relyOnNode = findNode(entity.getBelongStrongEntity().getName(), nodeList);
        g.add(subsetNode.addLink(relyOnNode));
      }
    }
//    g = g.graphAttr().with(GraphAttr.splines(SplineMode.ORTHO));

    // render the graph into png.
    Graphviz.useEngine(new GraphvizV8Engine());
//    Graphviz.fromGraph(g).engine(Engine.FDP).width(5000).render(Format.PNG).toFile(new File("example/ex47.png"));

    // render the graph into Json format so that we can extract the position information.
//    Graphviz.fromGraph(g).engine(Engine.NEATO).render(Format.PNG).toFile(new File("example/ex1.png"));

//     render the graph into Json format so that we can extract the position information.
    String jsonString = Graphviz.fromGraph(g).engine(Engine.FDP).width(5000).render(Format.JSON).toString();
    JSONObject jsonObject = new JSONObject(jsonString);
//    System.out.println("--------------------------- g:");
//    System.out.println(g);
//    System.out.println("json: -------------");
//    System.out.println(jsonObject);

    // Extract the layout information from the Json object.
    for (Object node: (JSONArray) jsonObject.get("objects")) {
      String[] pos = ((JSONObject ) node).get("pos").toString().split(",");
      boolean find = false;

      String nodeName = (String) ((JSONObject ) node).get("name");

      for (Entity entity: schema.getEntityList()) {
        String entityName = entity.getName();
        if (entityName.equals(nodeName)) {
          find = true;

          entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(),
              BelongObjType.ENTITY, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
          break;
        }
      }

      if (find) {
        continue;
      }
      for (Relationship relationship: schema.getRelationshipList()) {
        String relationshipName = relationship.getName();

        if (relationshipName.equals(nodeName)) {

          relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(),
              BelongObjType.RELATIONSHIP, Math.floor(Double.parseDouble(pos[0])), Math.floor(Double.parseDouble(pos[1]))));
          break;
        }
      }
    }

//    System.out.println("---------------------------------");
//    System.out.println("entities size: " + schema.getEntityList().size());
//    System.out.println("relationships size: " + schema.getRelationshipList().size());
//    System.out.println("nodes size: " + nodeList.size());

    // Firstly, initialise the grid with zero.
    int numOfEntities = schema.getEntityList().size() - 1;

    int gridWidth = ((numOfEntities / 10) + 2) * 1000;
    int gridHeight = ((numOfEntities / 10) + 2) * 1000;

    if (numOfEntities < 3) {
      numOfEntities = 3;
    } else if (3 < numOfEntities && numOfEntities <= 5) {
      numOfEntities = 5;
    } else if (5 < numOfEntities && numOfEntities <= 7) {
      numOfEntities = 7;
    } else if (7 < numOfEntities && numOfEntities <= 10){
      numOfEntities = 9;
    } else {
      numOfEntities = schema.getEntityList().size() / 2 + 9; ///
    }

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

//    for (Entity entity: schema.getEntityList()) {
//      if (subsets.contains(entity) && !superEntityMap.containsKey(entity.getName())) {
//
//      }
//    }

    for (Entity entity: schema.getEntityList()) {
      LayoutInfo layoutInfo = entity.getLayoutInfo();
//      if (layoutInfo != null) {
      double x = layoutInfo != null ? layoutInfo.getLayoutX() : 100;
      double y = layoutInfo != null ? layoutInfo.getLayoutY() : 100;
      int[] point = findNearestGridPoint(grid, x, y, finalWidth, finalHeight, entity.getName());
      points.add(point);

//      }
    }

    for (Entity entity: schema.getEntityList()) {
      LayoutInfo layoutInfo = entity.getLayoutInfo();
      if (layoutInfo == null) {
        // in this case, this is a subset entity.
        System.out.println("find a single subset!!!" + entity.getName());
        if (entity.getEntityType() == EntityType.SUBSET) {
          throw new IOException("Non-subset found...");
        } else {
          // in this case, we need to generate a position for the entity. This entity is disconnected with others.

        }

//        Entity relyOnEntity = entity.getBelongStrongEntity();
//        for (int i = 0; i < numOfEntities; i++) {
//          for (int j = 0; j < numOfEntities; j++) {
//            if (grid[i][j].equals(relyOnEntity.getName())) {
//              grid[i][j - 1] = entity.getName();
//              break;
//            }
//          }
//        }
      }
    }

    for (Relationship relationship: schema.getRelationshipList()) {
      LayoutInfo layoutInfo = relationship.getLayoutInfo();

      if (layoutInfo == null) {
        throw new IOException("relationship has no layoutInfo");
//        continue;
      }
//      double x = layoutInfo != null ? layoutInfo.getLayoutX() : 100;
//      double y = layoutInfo != null ? layoutInfo.getLayoutY() : 100;
      double x = layoutInfo.getLayoutX();
      double y = layoutInfo.getLayoutY();
      int[] point = findNearestGridPoint(grid, x, y, finalWidth, finalHeight, relationship.getName());
      points.add(point);

//      System.out.println("RELATIONSHIP: " + relationship.getName() + ", edges size: " + relationship.getEdgeList().size());
//      for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
//        System.out.println("edge: " + relationshipEdge.getConnObj().getName());
//      }

    }

    reverseByY(grid);

    // Thirdly, we need to map the grid point into actual position layouts.
    String[][] finalGridPositions = new String[numOfEntities][numOfEntities];
//    int gridWidth = numOfEntities <= 10 ? 1000 : 2000;
//    gridWidth = 5000;
//    int gridHeight = numOfEntities <= 10 ? 1000 : 2000;
//    gridHeight = 5000;
    int widthPerGrid = gridWidth / (numOfEntities + 2);
    int heightPerGrid = gridHeight / (numOfEntities + 2);
    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        finalGridPositions[i][j] = widthPerGrid * (i + 1) + "," + heightPerGrid * (j + 1);
      }
    }

    for (int i = 0; i < numOfEntities; i++) {
      for (int j = 0; j < numOfEntities; j++) {
        if (!grid[i][j].equals("*")) {
          String name = grid[i][j];
          boolean find = false;
          for (Entity entity: schema.getEntityList()) {
            if (entity.getName().equals(name)) {
              find = true;
              String[] position = finalGridPositions[i][j].split(",");
              double x = Double.parseDouble(position[0]);
              double y = Double.parseDouble(position[1]);
              entity.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), entity.getID(), BelongObjType.ENTITY,
                  x, y));
//              System.out.println("layoutInfo entity: " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
              break;
            }
          }

          if (find) {
            continue;
          }

          for (Relationship relationship: schema.getRelationshipList()) {
            if (relationship.getName().equals(name)) {
              String[] position = finalGridPositions[i][j].split(",");
              double x = Double.parseDouble(position[0]);
              double y = Double.parseDouble(position[1]);
              relationship.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), relationship.getID(), BelongObjType.RELATIONSHIP,
                  x, y));
//              System.out.println("layoutInfo relationship: " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
              break;
            }
          }
        }
      }
    }

    // At the end, we need to set the positions of all the attributes.
    for (Entity entity: schema.getEntityList()) {

//      boolean whetherOnTheSides = false;
      boolean onTheLeft = false;
      boolean onTheRight = false;
      for (int i = 0; i < numOfEntities; i++) {
        for (int j = 0; j < numOfEntities; j++) {
          if (grid[i][j].equals(entity.getName())) {
            // check whether the entity is on the sides of the grid.
            if (i == 0 || whetherLeftist(grid, i, j)) {
              onTheLeft = true;
              break;
            }
            if (i == numOfEntities - 1 || whetherRightist(grid, i, j)) {
              onTheRight = true;
              break;
            }
            break;
          }
        }
      }

      double entityX = entity.getLayoutInfo().getLayoutX();
      double entityY = entity.getLayoutInfo().getLayoutY();
      int numOfAttributes = entity.getAttributeList().size();

      List<Double> positions = generateAttributePositions(numOfAttributes);
      List<List<Double>> positionsAround = generateAttributePositionsAround(entityX, entityY, numOfAttributes);
      int index = 0;
      for (Attribute attribute: entity.getAttributeList()) {

        // If the entity is on the sides of the grid, the attributes should also on the sides of the grid.
        // Otherwise, the attributes should put around the entity.
        if (onTheLeft) {
          double x = entityX - 60;
          double y = entityY + positions.get(index);
          attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
              x, y));
        } else if (onTheRight) {
          double x = entityX + 100 + 60;
          double y = entityY + positions.get(index);
          attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
              x, y));
        } else {
          List<Double> position = positionsAround.get(index);
          double x = position.get(0);
          double y = position.get(1);
          attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
              x, y));
        }

        index++;
      }
    }

    for (Relationship relationship: schema.getRelationshipList()) {
      double relationshipX = relationship.getLayoutInfo().getLayoutX();
      double relationshipY = relationship.getLayoutInfo().getLayoutY();
      int numOfAttributes = relationship.getAttributeList().size();
      List<List<Double>> positionsAround = generateAttributePositionsAround(relationshipX, relationshipY, numOfAttributes);

      int index = 0;
      for (Attribute attribute: relationship.getAttributeList()) {
        List<Double> position = positionsAround.get(index);
        double x = position.get(0);
        double y = position.get(1);
        attribute.setLayoutInfo(new LayoutInfo(RandomUtils.generateID(), attribute.getID(), BelongObjType.ATTRIBUTE,
            x, y));
        index++;
      }
    }
//
//    for (Entity entity: schema.getEntityList()) {
//      System.out.println(entity.getName() + ": " + entity.getLayoutInfo().getLayoutX() + ", " + entity.getLayoutInfo().getLayoutY());
//    }
//    for (Relationship relationship: schema.getRelationshipList()) {
//      System.out.println(relationship.getName() + ": " + relationship.getLayoutInfo().getLayoutX() + ", " + relationship.getLayoutInfo().getLayoutY());
//    }
  }

  public static MutableNode findNode(String nodeName, List<MutableNode> nodeList) {
    for (MutableNode node: nodeList) {
      if (node.name().toString().equals(nodeName)) {
        return node;
      }
    }
    return null;
  }

  public static double findWidth(List<Entity> entities) {
    double largestX = 0;
    for (Entity entity: entities) {
//      System.out.println("Entity: " + entity.getName() + ", " + entity.getLayoutInfo());
      if (entity.getLayoutInfo() != null && entity.getLayoutInfo().getLayoutX() > largestX) {
        largestX = entity.getLayoutInfo().getLayoutX();
      }
    }
    return ((largestX / 100) + 1) * 100;
  }

  public static double findHeight(List<Entity> entities) {
    double largestY = 0;
    for (Entity entity: entities) {
      if (entity.getLayoutInfo() != null && entity.getLayoutInfo().getLayoutY() > largestY) {
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

  public static List<Double> generateAttributePositions(int numOfAttributes) {
    List<Double> positions = new ArrayList<>();
    int scale = (numOfAttributes - 1) / 2;
    double startPoint = 20 - 20 * scale;
    for (int i = 0; i < numOfAttributes; i++) {
      positions.add(startPoint + i * 20);
    }
    return positions;
  }

  public static List<List<Double>> generateAttributePositionsAround(double entityX, double entityY, int numOfAttributes) {
    List<List<Double>> positions = new ArrayList<>();
    int bottomNums = numOfAttributes / 2;
    int topNums = numOfAttributes - bottomNums;

    int scaleTop = topNums / 2;
    int scaleBottom = bottomNums / 2;
    double startPointTop = entityX + 20 + (-50) * scaleTop;
    double startPointBottom = entityX + 20 + (-50) * scaleBottom;

    for (int i = 0; i < topNums; i++) {
      List<Double> position = new ArrayList<>();
      double x = startPointTop + 60 * i;
      double y = entityY - 40;
      position.add(x);
      position.add(y);
      positions.add(position);
    }
    for (int i = 0; i < bottomNums; i++) {
      List<Double> position = new ArrayList<>();
      double x = startPointBottom + 60 * i;
      double y = entityY + 50 + 40;
      position.add(x);
      position.add(y);
      positions.add(position);
    }
    return positions;
  }

  public static boolean whetherLeftist(String[][] grid, int i, int j) {
    for (int x = i - 1; x >= 0; x--) {
      if (!grid[x][j].equals("*")) {
        return false;
      }
    }
    return true;
  }

  public static boolean whetherRightist(String[][] grid, int i, int j) {
    for (int x = i + 1; x < grid.length; x++) {
      if (!grid[x][j].equals("*")) {
        return false;
      }
    }
    return true;
  }

  public static Entity findSubset(String name, List<Entity> entities) {
    for (Entity entity: entities) {
      if (entity.getName().equals(name)) {
        return entity;
      }
    }
    return null;
  }
}

