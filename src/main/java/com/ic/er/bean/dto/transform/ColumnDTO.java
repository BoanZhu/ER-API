package com.ic.er.bean.dto.transform;

import com.ic.er.Attribute;
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

    private TableDTO foreignKeyTable;

    public void transformAttribute(Attribute attribute) {
        this.name = attribute.getName();
        this.dataType = attribute.getDataType().toString();
        this.isPrimary = attribute.getIsPrimary();
        this.isForeign = 0;
        this.foreignKeyTable = null;
    }
}
