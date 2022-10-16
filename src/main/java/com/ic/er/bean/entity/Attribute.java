package com.ic.er.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    private Long id;

    private Long entity_id;

    private Long view_id;

    private String name;

    private String data_type;

    private int is_primary;

    private int is_foreign;

    private int is_delete;

    private Date gmt_create;

    private Date gmt_modefied;

}
