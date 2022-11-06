package io.github.MigadaTang.entity;

import io.github.MigadaTang.common.Cardinality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipDO {
    private Long ID;
    private String name;
    private Long schemaID;
    private Long firstEntityID;
    private Long secondEntityID;
    private Cardinality firstCardinality;
    private Cardinality secondCardinality;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public RelationshipDO(Long ID) {
        this.ID = ID;
    }

    public RelationshipDO(Long firstEntityID, Long secondEntityID) {
        this.firstEntityID = firstEntityID;
        this.secondEntityID = secondEntityID;
    }

}
