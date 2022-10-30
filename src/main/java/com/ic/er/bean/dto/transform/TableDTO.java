package com.ic.er.bean.dto.transform;

import com.ic.er.Attribute;
import com.ic.er.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    private Long id;

    private String name;

    private List<ColumnDTO> columnDTOList;

    private List<ColumnDTO> primaryKey;

    public void tranformEntity(Entity entity) {
        this.id = entity.getID();
        this.name = entity.getName();
        this.columnDTOList = new ArrayList<>();
        this.primaryKey = new ArrayList<>();
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
