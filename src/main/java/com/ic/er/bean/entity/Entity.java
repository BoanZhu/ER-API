package com.ic.er.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Desceiption Entity entity
 * @author wendi
 * @date 15/10/2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
    private long id;

    private String name;

    private long view_id;

    private byte is_delete;

    private Date gmt_create;

    private Date gmt_modefied;

}
