package com.ic.er.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDO {
    private Long ID;

    private String name;

    private String creator;

    private Long parentID;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    public SchemaDO(Long ID) {
        this.ID = ID;
    }

}
