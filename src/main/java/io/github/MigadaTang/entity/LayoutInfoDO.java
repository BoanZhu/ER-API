package io.github.MigadaTang.entity;


import io.github.MigadaTang.common.BelongObjType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LayoutInfoDO {

    private Long ID;

    private Long belongObjID;

    private BelongObjType belongObjType;

    private Double layoutX;

    private Double layoutY;

    public LayoutInfoDO(Long id) {
        this.ID = id;
    }

    public LayoutInfoDO(Long belongObjID, BelongObjType belongObjType) {
        this.belongObjID = belongObjID;
        this.belongObjType = belongObjType;
    }
}
