package com.ic.er.entity;

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

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public EntityDO(Long ID) {
        this.ID = ID;
    }
}
