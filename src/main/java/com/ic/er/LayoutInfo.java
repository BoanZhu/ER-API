package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ic.er.common.BelongObjType;
import com.ic.er.entity.LayoutInfoDO;
import com.ic.er.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.List;

@Getter
@JsonIgnoreProperties({"id", "relatedObjID", "relatedObjType", "height", "width"})
public class LayoutInfo {
    private Long ID;
    private Long belongObjID;
    private BelongObjType belongObjType;
    private Double layoutX;
    private Double layoutY;
    private Double height;
    private Double width;

    protected LayoutInfo(Long ID, Long belongObjID, BelongObjType belongObjType, Double layoutX, Double layoutY) {
        this.ID = ID;
        this.belongObjID = belongObjID;
        this.belongObjType = belongObjType;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.height = 0.0;
        this.width = 0.0;
        if (this.ID == 0) {
            this.insertDB();
        }
    }

    public static List<LayoutInfo> queryByLayoutInfo(LayoutInfoDO layoutInfoDO) {
        List<LayoutInfoDO> LayoutInfoDOList = ER.layoutInfoMapper.selectByLayoutInfo(layoutInfoDO);
        return Trans.TransLayoutInfoListFormDB(LayoutInfoDOList);
    }

    public static LayoutInfo queryByObjIDAndObjType(Long relatedObjID, BelongObjType belongObjType) {
        List<LayoutInfo> layoutInfoDOList = queryByLayoutInfo(new LayoutInfoDO(relatedObjID, belongObjType));
        if (layoutInfoDOList.size() != 0) {
            return layoutInfoDOList.get(0);
        }
        return null;
    }

    public static LayoutInfo queryByID(Long ID) throws ERException {
        List<LayoutInfo> layoutInfoDOList = queryByLayoutInfo(new LayoutInfoDO(ID));
        if (layoutInfoDOList.size() == 0) {
            throw new ERException(String.format("LayoutInfo with ID: %d not found ", ID));
        } else {
            return layoutInfoDOList.get(0);
        }
    }

    private void insertDB() {
        try {
            LayoutInfoDO LayoutInfoDO = new LayoutInfoDO(0L, this.belongObjID, this.belongObjType, this.layoutX, this.layoutY, this.height, this.width);
            int ret = ER.layoutInfoMapper.insert(LayoutInfoDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = LayoutInfoDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    public void update(Double layoutX, Double layoutY) throws ERException {
        if (layoutX != null) {
            this.layoutX = layoutX;
        }
        if (layoutX != null) {
            this.layoutY = layoutY;
        }
        int ret = ER.layoutInfoMapper.updateByID(new LayoutInfoDO(this.ID, this.belongObjID, this.belongObjType, this.layoutX, this.layoutY, this.height, this.width));
        if (ret == 0) {
            throw new ERException(String.format("cannot find LayoutInfo with ID: %d", this.ID));
        }
    }
}
