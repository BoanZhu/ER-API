package io.github.MigadaTang.transform;

import io.github.MigadaTang.Attribute;
import java.util.Locale;
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
        this.dataType = attribute.getDataType().toString().toLowerCase();
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

    protected Column getForeignClone(Long tableID, boolean isPk, String foreignTableName, Boolean nullable) {
        String name = "";
        if (foreignTableName.equals("")) {
            name = this.name; // use the foreign key's name
        } else {
//            name = foreignTableName + "_" + this.name;
//            name = this.name; ///
            name = foreignTableName; // otherwise, use foreign table name
        }
        Column clone = new Column(this.ID, name, this.dataType, isPk,
            true, this.ID, this.name, tableID, false, null); // todo:
        return clone;
    }
}
