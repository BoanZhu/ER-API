package io.github.MigadaTang;

import io.github.MigadaTang.exception.ERException;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Edge {

  private Node sourceNode;
  private Node targetNode;

  public Edge(Node sourceNode, Node targetNode) {
    this.sourceNode = sourceNode;
    this.targetNode = targetNode;
  }

  public static Edge transformEdge(Relationship relationship, List<Entity> entityList) {
    List<Entity> sourceAndTargetNodes = new ArrayList<>();
    for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
//      relationshipEdge.getConnObjType() // Here we need to check the type of the ConnObj
      Entity entity = findEntity(relationshipEdge.getConnObj().getID(), entityList);
      sourceAndTargetNodes.add(entity);
    }
    if (sourceAndTargetNodes.size() > 2) {
      throw new ERException(relationship.getName() + " this relationship contains more than two entities!");
    }
    Node sourceNode = Node.transformNode(sourceAndTargetNodes.get(0));
    Node targetNode = Node.transformNode(sourceAndTargetNodes.get(1));
    return new Edge(sourceNode, targetNode);
  }

  public static Entity findEntity(Long id, List<Entity> entityList) {
    for (Entity entity: entityList) {
      if (entity.getID().equals(id)) {
        return entity;
      }
    }
    return null;
  }
}
