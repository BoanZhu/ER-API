package com.ic.er.entity;

import com.ic.er.common.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDO {
    private Long ID;

    private String name;

    private Long schemaID;

    private Integer aimPort;

    private EntityType entityType;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public EntityDO(Long ID) {
        this.ID = ID;
    }

    public EntityDO(Long ID, String name, Long schemaID) {
        this.ID = ID;
    }
}
