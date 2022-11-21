package io.github.MigadaTang.bean.dto.transform;

import io.github.MigadaTang.Attribute;
import io.github.MigadaTang.util.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDTO {

    private Long ID;

    private String name;

    private String dataType;

    private boolean isPrimary;

    private boolean isForeign;

    private Long foreignKeyColumn;

    private String foreignKeyColumnName;

    private Long belongTo;

    private boolean nullable;

    private Long foreignKeyTable;

    public void transformAttribute(Attribute attribute) {
        this.ID = attribute.getID();
        this.name = attribute.getName();
        this.dataType = attribute.getDataType().toString();
        this.isPrimary = attribute.getIsPrimary();
        this.isForeign = false;
        this.foreignKeyColumn = null;
        this.foreignKeyColumnName = null;
//        this.nullable = attribute.getAttributeType();
        this.foreignKeyTable = null;
    }

    public String nullable() {
        if (nullable) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }

    public ColumnDTO getForeignClone(Long tableID, boolean isPk, String foreignTableName) {
        ColumnDTO clone = new ColumnDTO(RandomUtils.generateID(), foreignTableName + "_" + this.name, this.dataType, isPk,
                true, this.ID, this.name, tableID, false, null);
        return clone;
    }
}
