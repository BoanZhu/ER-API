package com.ic.er.bean.entity;

import com.ic.er.common.Cardinality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipDO {
    private Long id;

    private String name;

    private Long viewId;

    private Long firstEntityId;

    private Long secondEntityId;

    private Long firstAttributeId;

    private Long secondAttributeId;

    private Cardinality cardinality;

    private int isDelete;

    private Date gmtCreate;

    private Date gmtModified;

}
