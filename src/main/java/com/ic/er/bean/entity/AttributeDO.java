package com.ic.er.bean.entity;

import com.ic.er.common.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeDO {
    private Long id;

    private Long entityId;

    private Long viewId;

    private String name;

    private DataType dataType;

    private Integer isPrimary;

    private Integer isForeign;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public AttributeDO(Long id) {
        this.id = id;
    }
    public AttributeDO(Long entityId, Long viewId) {
        this.entityId = entityId;
        this.viewId = viewId;
    }

}
