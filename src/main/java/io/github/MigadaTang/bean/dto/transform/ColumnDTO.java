package io.github.MigadaTang.bean.dto.transform;

import io.github.MigadaTang.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDTO {

    private String name;

    private String dataType;

    private boolean isPrimary;

    private int isForeign;

    private Long foreignKeyTable;

    private Long belongTo;

    private boolean nullable;

    public void transformAttribute(Attribute attribute) {
        this.name = attribute.getName();
        this.dataType = attribute.getDataType().toString();
        this.isPrimary = attribute.getIsPrimary();
        this.isForeign = 0;
        this.foreignKeyTable = null;
        this.nullable = attribute.getNullable();
    }

    public String nullable() {
        if (nullable) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }
}
