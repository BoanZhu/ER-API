package io.github.MigadaTang.entity;

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
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public RelationshipDO(Long ID) {
        this.ID = ID;
    }

    public RelationshipDO(String name, Long schemaID) {
        this.name = name;
        this.schemaID = schemaID;
    }

}
