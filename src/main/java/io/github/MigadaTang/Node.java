package io.github.MigadaTang;

import java.util.List;
import lombok.Data;

@Data
public class Node {

  private Long id;
  private String name;
  private double x;
  private double y;
  private List<Node> children;

  public Node(Long id, String name, double x, double y) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public static Node transformNode(Entity entity) {
    double x = getRandom(0, 500);
    double y = getRandom(0, 500);
    return new Node(entity.getID(), entity.getName(), x, y);
  }

  public static double getRandom(int min, int max) {
    return Math.floor(min + Math.random() * (max - min));
  }

}
