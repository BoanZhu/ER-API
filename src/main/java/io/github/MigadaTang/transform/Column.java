package io.github.MigadaTang.transform;

import io.github.MigadaTang.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column {

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

    protected void transformAttribute(Attribute attribute, boolean nullable) {
        this.ID = attribute.getID();
        this.name = attribute.getName();
        this.dataType = attribute.getDataType().toString();
        this.isPrimary = attribute.getIsPrimary();
        this.isForeign = false;
        this.foreignKeyColumn = null;
        this.foreignKeyColumnName = null;
        this.nullable = nullable;
        this.foreignKeyTable = null;
    }

    protected String nullable() {
        if (nullable) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }

    protected Column getForeignClone(Long tableID, boolean isPk, String foreignTableName) {
        Column clone = new Column(RandomUtils.generateID(), foreignTableName + "_" + this.name, this.dataType, isPk,
                true, this.ID, this.name, tableID, false, null);
        return clone;
    }
}
