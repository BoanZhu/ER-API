package io.github.MigadaTang;

import io.github.MigadaTang.exception.ERException;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Edge {

  private GraphNode sourceGraphNode;
  private GraphNode targetGraphNode;

  public Edge(GraphNode sourceGraphNode, GraphNode targetGraphNode) {
    this.sourceGraphNode = sourceGraphNode;
    this.targetGraphNode = targetGraphNode;
  }

  public static Edge transformEdge(Relationship relationship, List<GraphNode> graphNodes) {
    List<GraphNode> sourceAndTargetGraphNodes = new ArrayList<>();
    for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
//      relationshipEdge.getConnObjType() // Here we need to check the type of the ConnObj
      GraphNode graphNode = findNode(relationshipEdge.getConnObj().getID(), graphNodes);
      sourceAndTargetGraphNodes.add(graphNode);
    }
    if (sourceAndTargetGraphNodes.size() > 2) {
      throw new ERException(relationship.getName() + " this relationship contains more than two entities!");
    }
//    Node sourceNode = Node.transformNode(sourceAndTargetNodes.get(0));
//    Node targetNode = Node.transformNode(sourceAndTargetNodes.get(1));
    return new Edge(sourceAndTargetGraphNodes.get(0), sourceAndTargetGraphNodes.get(1));
  }

  public static GraphNode findNode(Long id, List<GraphNode> graphNodes) {
    for (GraphNode graphNode : graphNodes) {
      if (graphNode.getId().equals(id)) {
        return graphNode;
      }
    }
    return null;
  }
}
