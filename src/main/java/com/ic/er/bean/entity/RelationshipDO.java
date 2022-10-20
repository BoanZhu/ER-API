package com.ic.er.bean.entity;

import com.ic.er.common.Cardinality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipDO {
    private Long id;

    private String name;

    private Long viewId;

    private Long firstEntityId;

    private Long secondEntityId;

    private Cardinality cardinality;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public RelationshipDO(Long ID) {
        this.id = ID;
    }
    public RelationshipDO(Long firstEntityId, Long secondEntityId) {
        this.firstEntityId = firstEntityId;
        this.secondEntityId = secondEntityId;
    }

}
