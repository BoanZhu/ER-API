package io.github.MigadaTang;

import java.util.List;
import lombok.Data;

@Data
public class GraphNode {

  private Long id;
  private String name;
  private double x;
  private double y;
  private List<GraphNode> children;

  public GraphNode(Long id, String name, double x, double y) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public static GraphNode transformNode(Entity entity) {
    double x = getRandom(0, 500);
    double y = getRandom(0, 500);
    System.out.println(entity);
    return new GraphNode(entity.getID(), entity.getName(), x, y);
  }

  public static double getRandom(int min, int max) {
    double random = Math.random();
    System.out.println("random: " + random);
    return Math.floor(min + random * (max - min));
  }

}
