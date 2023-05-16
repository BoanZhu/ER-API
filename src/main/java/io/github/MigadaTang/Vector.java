package io.github.MigadaTang;

import lombok.Data;

@Data
public class Vector {

  private Long id;
  private double x;
  private double y;

  public Vector(Long id, double x, double y) {
    this.id = id;
    this.x = x;
    this.y = y;
  }

}
