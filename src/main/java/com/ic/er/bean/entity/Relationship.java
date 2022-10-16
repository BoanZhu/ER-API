package com.ic.er.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Desceiption Relationship entity
 * @author wendi
 * @date 15/10/2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Relationship {
    private Long id;

    private String name;

    private Long view_id;

    private Long first_entity_id;

    private Long second_entity_id;

    private Long first_attribute_id;

    private Long second_attribute_id;

    private int cardinatily;

    private int is_delete;

    private Date gmt_create;

    private Date gmt_modefied;

}
