package com.ic.er;

import com.ic.er.bean.entity.AttributeDO;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import com.ic.er.common.Utils;
import com.ic.er.dao.AttributeMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class Attribute {
    private Long ID;
    private Long entityID;
    private Long viewID;
    private String name;
    private DataType dataType;
    private int isPrimary;
    private int isForeign;
    private Date gmtCreate;
    private Date gmtModified;
    public Attribute(Long ID, Long entityID, Long viewID, String name, DataType dataType,
                     int isPrimary, int isForeign, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.entityID = entityID;
        this.viewID = viewID;
        this.name = name;
        this.dataType = dataType;
        this.isPrimary = isPrimary;
        this.isForeign = isForeign;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            if (ER.useDB) {
                this.insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
    }

    int insertDB() {
        AttributeDO aDo = new AttributeDO(this.ID, this.entityID, this.viewID, this.name, this.dataType, this.isPrimary, this.isForeign, 0, this.gmtCreate, this.gmtModified);
        int ret = ER.attributeMapper.insert(aDo);
        this.ID = aDo.getId();
        return ret;
    }

    ResultState deleteDB() {
        int ret = ER.attributeMapper.deleteById(this.ID);
        if (ret != 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(ResultStateCode.Success, "delete fail");
        }
    }

    ResultState updateDB() {
        int ret = ER.attributeMapper.updateById(new AttributeDO(this.ID, this.entityID, this.viewID, this.name, this.dataType, this.isPrimary, this.isForeign, 0, this.gmtCreate, new Date()));
        if (ret != 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(ResultStateCode.Success, "update fail");
        }
    }

    // transform the data from db format (xxxDO) to java class format
    public static Attribute TransformFromDB(AttributeDO attributeDO) {
        return new Attribute(attributeDO.getId(), attributeDO.getEntityId(), attributeDO.getViewId(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(),
                attributeDO.getIsForeign(), attributeDO.getGmtCreate(), attributeDO.getGmtModified());
    }

    public static List<Attribute> TransListFormFromDB(List<AttributeDO> doList) {
        List<Attribute> ret = new ArrayList<>();
        for (AttributeDO attributeDO : doList) {
            ret.add(TransformFromDB(attributeDO));
        }
        return ret;
    }

    public static List<Attribute> queryByAttribute(AttributeDO attributeDO) {
        return TransListFormFromDB(ER.attributeMapper.selectByAttribute(attributeDO));
    }

    public static Attribute queryByID(Long ID) {
        List<Attribute> attributeList = TransListFormFromDB(ER.attributeMapper.selectByAttribute(new AttributeDO(ID)));
        if (attributeList.size() != 0) {
            return attributeList.get(0);
        } else {
            return null;
        }
    }

}
