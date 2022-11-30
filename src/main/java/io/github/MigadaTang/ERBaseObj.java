package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Contains the variables commonly needed by Entity, Attribute and Relationship
 */
@Data
@AllArgsConstructor
public class ERBaseObj {
    private Long ID;
    private Long schemaID;
    private String name;
    private BelongObjType belongObjType;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    /**
     * Update the position of this object on the diagram
     *
     * @param layoutX The x-axis of this entity
     * @param layoutY The y-axis of this entity
     */
    public void updateLayoutInfo(Double layoutX, Double layoutY) {
        if (getLayoutInfo() == null) {
            setLayoutInfo(new LayoutInfo(0L, getID(), belongObjType, layoutX, layoutY));
        }
        getLayoutInfo().update(layoutX, layoutY);
    }
}
