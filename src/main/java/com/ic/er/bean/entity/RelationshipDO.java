package com.ic.er.bean.entity;

import com.ic.er.common.Cardinality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipDO {
    private Long id;

    private String name;

    private Long view_id;

    private Long first_entity_id;

    private Long second_entity_id;

    private Long first_attribute_id;

    private Long second_attribute_id;

    private Cardinality cardinality;

    private int is_delete;

    private Date gmt_create;

    private Date gmt_modified;

}
