package io.github.MigadaTang.transform;

import io.github.MigadaTang.Attribute;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.transform.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Table {
    private Long id;

    private String name;

    private EntityType tableType;

    private Long belongStrongTableID;

    private List<Column> columnList;

    private List<Column> primaryKey;

    private List<Column> multiValuedColumn;

    private Map<Long, List<Column>> foreignKey;

    private Long entityID;

    private boolean reflexive;

    private Map<Long, Long> columnIdsMap = new HashMap<>();

    protected void tranformEntity(Entity entity) {
        this.id = entity.getID();
        this.name = entity.getName().trim().replaceAll(" ", "_");
        this.columnList = new ArrayList<>();
        this.primaryKey = new ArrayList<>();
        this.multiValuedColumn = new ArrayList<>();
        this.foreignKey = new HashMap<>();
        this.tableType = entity.getEntityType();
        this.reflexive = false;
        if (entity.getBelongStrongEntity() != null) {
            this.belongStrongTableID = entity.getBelongStrongEntity().getID();
        }
//        this.foreignKey = new HashMap<>();
        for (Attribute attribute : entity.getAttributeList()) {
            Column column = new Column();
            if (attribute.getAttributeType() == AttributeType.Optional) {
                column.transformAttribute(attribute, true);
                this.columnList.add(column);
            } else if (attribute.getAttributeType() == AttributeType.Mandatory) {
                column.transformAttribute(attribute, false);
                this.columnList.add(column);
            } else {
                column.transformAttribute(attribute, false);
                multiValuedColumn.add(column);
            }
            if (column.isPrimary()) {
                this.primaryKey.add(column);
            }
        }
    }

    public boolean getReflexive() {
        return this.reflexive;
    }
}
