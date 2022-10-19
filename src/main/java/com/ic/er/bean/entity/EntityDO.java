package com.ic.er.bean.entity;

import com.ic.er.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDO {
    private Long id;

    private String name;

    private Long viewId;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public EntityDO(Long id) {
        this.id = id;
    }
}
