package io.github.MigadaTang.entity;


import io.github.MigadaTang.common.RelatedObjType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LayoutInfoDO {

    private Long ID;

    private Long relatedObjID;

    private RelatedObjType relatedObjType;

    private Double layoutX;

    private Double layoutY;

    private Double height;

    private Double width;

    public LayoutInfoDO(Long id) {
        this.ID = id;
    }

    public LayoutInfoDO(Long relatedObjID, RelatedObjType relatedObjType) {
        this.relatedObjID = relatedObjID;
        this.relatedObjType = relatedObjType;
    }
}
