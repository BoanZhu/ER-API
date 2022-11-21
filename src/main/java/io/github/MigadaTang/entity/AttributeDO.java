package io.github.MigadaTang.entity;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDO {
    private Long ID;
    private Long belongObjId;
    private BelongObjType belongObjType;
    private Long schemaID;
    private String name;
    private DataType dataType;
    private Boolean isPrimary;
    private AttributeType attributeType;
    private Integer aimPort;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public AttributeDO(Long ID) {
        this.ID = ID;
    }

    public AttributeDO(Long belongObjId, BelongObjType belongObjType, Long schemaID, String name) {
        this.belongObjId = belongObjId;
        this.belongObjType = belongObjType;
        this.schemaID = schemaID;
        this.name = name;
    }

}
