package io.github.MigadaTang;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Contains the attributes commonly needed by @see io.github.MigadaTang.Entity, @see io.github.MigadaTang.Relationship, @see io.github.MigadaTang.Attribute
 */
@Data
@AllArgsConstructor
class ERBaseObj {
    private Long ID;
    private Long schemaID;
    private String name;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;
}
