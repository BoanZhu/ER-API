package io.github.MigadaTang.entity;

import io.github.MigadaTang.ERConnectableObj;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
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
    private Long belongObjID;
    private BelongObjType belongObjType;
    private Cardinality cardinality;
    private Integer portAtRelationship;
    private Integer portAtBelongObj;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public RelationshipEdgeDO(Long ID) {
        this.ID = ID;
    }

    public RelationshipEdgeDO(Long relationshipID, ERConnectableObj connObj) {
        this.relationshipID = relationshipID;
        if (connObj != null) {
            this.belongObjID = connObj.getID();
            if (connObj instanceof Entity) {
                this.belongObjType = BelongObjType.ENTITY;
            } else if (connObj instanceof Relationship) {
                this.belongObjType = BelongObjType.RELATIONSHIP;
            }
        }
    }
}
