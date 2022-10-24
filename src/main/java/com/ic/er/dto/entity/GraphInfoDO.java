package com.ic.er.dto.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphInfoDO {

    private Long ID;

    private Long relatedObjID;

    private Double layoutX;

    private Double layoutY;

    private Double height;

    private Double length;

}
