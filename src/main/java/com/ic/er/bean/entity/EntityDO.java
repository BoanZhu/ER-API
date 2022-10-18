package com.ic.er.bean.entity;

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

    private int isDelete;

    private Date gmtCreate;

    private Date gmtModified;

}
