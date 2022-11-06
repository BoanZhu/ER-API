package io.github.MigadaTang.entity;

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
    private Long entityID;
    private Long schemaID;
    private String name;
    private DataType dataType;
    private Boolean isPrimary;
    private Boolean nullable;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public AttributeDO(Long ID) {
        this.ID = ID;
    }

    public AttributeDO(Long entityID, Long schemaID) {
        this.entityID = entityID;
        this.schemaID = schemaID;
    }

}
