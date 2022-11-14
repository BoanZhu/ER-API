package io.github.MigadaTang.bean.dto.transform;

import io.github.MigadaTang.Attribute;
import io.github.MigadaTang.Entity;
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
public class TableDTO {
    private Long id;

    private String name;

    private EntityType tableType;

    private Long belongStrongTableID;

    private List<ColumnDTO> columnDTOList;

    private List<ColumnDTO> primaryKey;

    private Map<Long, List<ColumnDTO>> foreignKey;

    public void tranformEntity(Entity entity) {
        this.id = entity.getID();
        this.name = entity.getName();
        this.columnDTOList = new ArrayList<>();
        this.primaryKey = new ArrayList<>();
        this.tableType = entity.getEntityType();
        if (entity.getBelongStrongEntity() != null) {
            this.belongStrongTableID = entity.getBelongStrongEntity().getID();
        }
        this.foreignKey = new HashMap<>();
        for (Attribute attribute : entity.getAttributeList()) {
            ColumnDTO columnDTO = new ColumnDTO();
            columnDTO.transformAttribute(attribute);
            this.columnDTOList.add(columnDTO);
            if (columnDTO.isPrimary()) {
                this.primaryKey.add(columnDTO);
            }
        }
    }


}
