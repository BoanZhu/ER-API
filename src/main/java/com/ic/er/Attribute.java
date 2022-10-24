package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ic.er.Exception.ERException;
import com.ic.er.entity.AttributeDO;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import com.ic.er.common.Utils;
import lombok.Data;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.jdbc.SQL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
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
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
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

    private void insertDB() throws PersistenceException{
        try {
            AttributeDO aDo = new AttributeDO(this.ID, this.entityID, this.viewID, this.name, this.dataType, this.isPrimary, this.isForeign, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.attributeMapper.insert(aDo);
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
        return new Attribute(attributeDO.getID(), attributeDO.getEntityID(), attributeDO.getViewID(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(),
                attributeDO.getIsForeign(), attributeDO.getGmtCreate(), attributeDO.getGmtModified());
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
