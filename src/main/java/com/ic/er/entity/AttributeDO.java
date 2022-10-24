package com.ic.er.entity;

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

    private Long entityID;

    private Long viewID;

    private String name;

    private DataType dataType;

    private Integer isPrimary;

    private Integer isForeign;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public AttributeDO(Long ID) {
        this.ID = ID;
    }
    public AttributeDO(Long entityID, Long viewID) {
        this.entityID = entityID;
        this.viewID = viewID;
    }

}
