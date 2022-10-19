package com.ic.er.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewDO {
    private Long id;

    private String name;

    private String creator;

    private Long parentId;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

}
