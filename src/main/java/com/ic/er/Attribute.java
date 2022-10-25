package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ic.er.Exception.ERException;
import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.AttributeDO;
import com.ic.er.common.DataType;
import com.ic.er.common.Utils;
import com.ic.er.entity.LayoutInfoDO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Attribute {
    @JsonIgnore
    private Long ID;
    @JsonIgnore
    private Long entityID;
    @JsonIgnore
    private Long viewID;
    private String name;
    private DataType dataType;
    private int isPrimary;
    private int isForeign;
    private LayoutInfo layoutInfo;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    public Attribute(Long ID, Long entityID, Long viewID, String name, DataType dataType,
                     int isPrimary, int isForeign, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.entityID = entityID;
        this.viewID = viewID;
        this.name = name;
        this.dataType = dataType;
        this.isPrimary = isPrimary;
        this.isForeign = isForeign;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            if (ER.useDB) {
                this.insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.ATTRIBUTE, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.setLayoutX(layoutX);
        this.layoutInfo.setLayoutY(layoutY);
        this.layoutInfo.setHeight(height);
        this.layoutInfo.setWidth(width);
        this.layoutInfo.update();
    }

    private void insertDB() throws PersistenceException {
        try {
            AttributeDO aDo = new AttributeDO(this.ID, this.entityID, this.viewID, this.name, this.dataType, this.isPrimary, this.isForeign, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.attributeMapper.insert(aDo);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = aDo.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    protected void deleteDB() {
        ER.attributeMapper.deleteByID(this.ID);
    }

    public void updateDB() throws ERException {
        int ret = ER.attributeMapper.updateByID(new AttributeDO(this.ID, this.entityID, this.viewID, this.name, this.dataType, this.isPrimary, this.isForeign, 0, this.gmtCreate, new Date()));
        if (ret == 0) {
            throw new ERException(String.format("cannot find Attribute with ID: %d", this.ID));
        }
    }

    // transform the data from db format (xxxDO) to java class format
    private static Attribute TransformFromDB(AttributeDO attributeDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(attributeDO.getID(), RelatedObjType.ATTRIBUTE);
        return new Attribute(attributeDO.getID(), attributeDO.getEntityID(), attributeDO.getViewID(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(),
                attributeDO.getIsForeign(), layoutInfo, attributeDO.getGmtCreate(), attributeDO.getGmtModified());
    }

    private static List<Attribute> TransListFormFromDB(List<AttributeDO> doList) {
        List<Attribute> ret = new ArrayList<>();
        for (AttributeDO attributeDO : doList) {
            ret.add(TransformFromDB(attributeDO));
        }
        return ret;
    }

    public static List<Attribute> queryByAttribute(AttributeDO attributeDO) {
        return TransListFormFromDB(ER.attributeMapper.selectByAttribute(attributeDO));
    }

    public static Attribute queryByID(Long ID) throws ERException {
        List<Attribute> attributeList = TransListFormFromDB(ER.attributeMapper.selectByAttribute(new AttributeDO(ID)));
        if (attributeList.size() == 0) {
            throw new ERException(String.format("Attribute with ID: %d not found ", ID));
        } else {
            return attributeList.get(0);
        }
    }
}
