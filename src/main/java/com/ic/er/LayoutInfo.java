package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ic.er.Exception.ERException;
import com.ic.er.common.RelatedObjType;
import com.ic.er.common.Utils;
import com.ic.er.entity.LayoutInfoDO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LayoutInfo {
    @JsonIgnore
    private Long ID;
    @JsonIgnore
    private Long relatedObjID;
    @JsonIgnore
    private RelatedObjType relatedObjType;
    private Double layoutX;
    private Double layoutY;
    private Double height;
    private Double width;

    protected LayoutInfo(Long ID, Long relatedObjID, RelatedObjType relatedObjType, Double layoutX, Double layoutY, Double height, Double width) {
        this.ID = ID;
        this.relatedObjID = relatedObjID;
        this.relatedObjType = relatedObjType;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.height = height;
        this.width = width;
        if (this.ID == 0) {
            if (ER.useDB) {
                this.insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
    }

    public static List<LayoutInfo> queryByLayoutInfo(LayoutInfoDO layoutInfoDO) {
        List<LayoutInfoDO> LayoutInfoDOList = ER.layoutInfoMapper.selectByLayoutInfo(layoutInfoDO);
        return Trans.TransListFormDB(LayoutInfoDOList);
    }

    public static LayoutInfo queryByObjIDAndObjType(Long relatedObjID, RelatedObjType relatedObjType) {
        List<LayoutInfo> layoutInfoDOList = queryByLayoutInfo(new LayoutInfoDO(relatedObjID, relatedObjType));
        if (layoutInfoDOList.size() == 0) {
            throw new ERException(String.format("LayoutInfo with relatedObjID: %d, relatedObjType: %s not found ", relatedObjID, relatedObjType.name()));
        } else {
            return layoutInfoDOList.get(0);
        }
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
            LayoutInfoDO LayoutInfoDO = new LayoutInfoDO(0L, this.relatedObjID, this.relatedObjType, this.layoutX, this.layoutY, this.height, this.width);
            int ret = ER.layoutInfoMapper.insert(LayoutInfoDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = LayoutInfoDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    public void update(Double layoutX, Double layoutY, Double height, Double width) throws ERException {
        if (layoutX != null) {
            this.layoutX = layoutX;
        }
        if (layoutX != null) {
            this.layoutY = layoutY;
        }
        if (layoutX != null) {
            this.height = height;
        }
        if (layoutX != null) {
            this.width = width;
        }
        int ret = ER.layoutInfoMapper.updateByID(new LayoutInfoDO(this.ID, this.relatedObjID, this.relatedObjType, this.layoutX, this.layoutY, this.height, this.width));
        if (ret == 0) {
            throw new ERException(String.format("cannot find LayoutInfo with ID: %d", this.ID));
        }
    }
}
