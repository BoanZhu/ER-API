package com.ic.er.bean.entity;

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

    private String dataType;

    private int isPrimary;

    private int isForeign;

    private int isDelete;

    private Date gmtCreate;

    private Date gmtModified;

}
