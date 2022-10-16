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
    private Long id;

    private String name;

    private Long view_id;

    private int is_delete;

    private Date gmt_create;

    private Date gmt_modefied;

}
