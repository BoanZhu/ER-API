package com.ic.er.bean.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphInfoDO {

    private Long id;

    private Long relatedObjId;

    private Double layoutX;

    private Double layoutY;

    private Double height;

    private Double length;

}
