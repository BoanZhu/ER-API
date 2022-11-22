package io.github.MigadaTang;

import io.github.MigadaTang.common.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    private Long id;

    private String name;

    private EntityType tableType;

    private Long belongStrongTableID;

    private List<Column> columnList;

    private List<Column> primaryKey;

    private Map<Long, List<Column>> foreignKey;

    public void tranformEntity(Entity entity) {
        this.id = entity.getID();
        this.name = entity.getName();
        this.columnList = new ArrayList<>();
        this.primaryKey = new ArrayList<>();
        this.tableType = entity.getEntityType();
        if (entity.getBelongStrongEntity() != null) {
            this.belongStrongTableID = entity.getBelongStrongEntity().getID();
        }
        this.foreignKey = new HashMap<>();
        for (Attribute attribute : entity.getAttributeList()) {
            Column column = new Column();
            column.transformAttribute(attribute);
            this.columnList.add(column);
            if (column.isPrimary()) {
                this.primaryKey.add(column);
            }
        }
    }


}
