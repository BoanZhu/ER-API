package com.ic.er.entity;

import com.ic.er.common.Cardinality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipEdgeDO {
    private Long ID;
    private Long relationshipID;
    private Long schemaID;
    private Long entityID;
    private Cardinality cardinality;
    private Integer portAtRelationship;
    private Integer portAtEntity;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public RelationshipEdgeDO(Long ID) {
        this.ID = ID;
    }

    public RelationshipEdgeDO(Long relationshipID, Long entityID) {
        this.relationshipID = relationshipID;
        this.entityID = entityID;
    }
}