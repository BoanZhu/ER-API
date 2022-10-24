package com.ic.er.entity;


import com.ic.er.common.RelatedObjType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphInfoDO {

    private Long ID;

    private Long relatedObjID;

    private RelatedObjType relatedObjType;

    private Double layoutX;

    private Double layoutY;

    private Double height;

    private Double length;

}
