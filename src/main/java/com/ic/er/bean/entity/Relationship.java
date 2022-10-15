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
    private long id;

    private String name;

    private long first_entity_id;

    private long second_entity_id;

    private long first_attribute_id;

    private long second_attribute_id;

    private short cardinatily;

    private byte is_delete;

    private Date gmt_create;

    private Date gmt_modefied;

}
