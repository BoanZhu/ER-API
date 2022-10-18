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

    private Long parent_id;

    private int is_delete;

    private Date gmt_create;

    private Date gmt_modified;

}
