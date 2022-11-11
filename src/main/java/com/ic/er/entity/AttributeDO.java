package com.ic.er.entity;

import com.ic.er.common.BelongObjType;
import com.ic.er.common.DataType;
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
    private Boolean nullable;
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
