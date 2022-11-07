package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ic.er.common.DataType;
import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.AttributeDO;
import com.ic.er.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Getter
@JsonIgnoreProperties({"id", "entityID", "schemaID", "gmtCreate", "gmtModified"})
public class Attribute {
    private Long ID;
    private Long entityID;
    private Long schemaID;
    private String name;
    private DataType dataType;
    private Boolean isPrimary;
    private Boolean nullable;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Attribute(Long ID, Long entityID, Long schemaID, String name, DataType dataType,
                        Boolean isPrimary, Boolean nullable, LayoutInfo layoutInfo, Double layoutX, Double layoutY, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.entityID = entityID;
        this.schemaID = schemaID;
        this.name = name;
        this.dataType = dataType;
        this.isPrimary = isPrimary;
        this.nullable = nullable;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            this.insertDB();
        }
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.ATTRIBUTE, layoutX, layoutY, 0.0, 0.0);
        }
    }


    private void insertDB() throws PersistenceException {
        try {
            AttributeDO aDo = new AttributeDO(this.ID, this.entityID, this.schemaID, this.name, this.dataType, this.isPrimary, this.nullable, 0, this.gmtCreate, this.gmtModified);
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

    public void updateInfo(String name, DataType dataType, Boolean isPrimary, Boolean nullable) throws ERException {
        if (name != null) {
            this.name = name;
        }
        if (dataType != null) {
            this.dataType = dataType;
        }
        if (isPrimary != null) {
            this.isPrimary = isPrimary;
        }
        if (nullable != null) {
            this.nullable = nullable;
        }
        if (name != null) {
            List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.entityID, this.schemaID, name, null, null, null, null, null, null));
            if (attributeList.size() != 0 && !attributeList.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("attribute with name: %s already exists", this.name));
            }
        }
        if (isPrimary != null && isPrimary) {
            List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.entityID, this.schemaID, null, null, true, null, null, null, null));
            if (attributeList.size() != 0 && !attributeList.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("attribute that is primary key already exists, name: %s", attributeList.get(0).getName()));
            }
        }
        if (this.nullable && this.isPrimary) {
            throw new ERException("primary attribute cannot be null");
        }
        ER.attributeMapper.updateByID(new AttributeDO(this.ID, this.entityID, this.schemaID, this.name, this.dataType, this.isPrimary, this.nullable, 0, this.gmtCreate, new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) throws ERException {
        this.layoutInfo.update(layoutX, layoutY, height, width);
    }

    public static List<Attribute> queryByAttribute(AttributeDO attributeDO) {
        return Trans.TransAttributeListFromDB(ER.attributeMapper.selectByAttribute(attributeDO));
    }

    public static Attribute queryByID(Long ID) throws ERException {
        List<Attribute> attributeList = Trans.TransAttributeListFromDB(ER.attributeMapper.selectByAttribute(new AttributeDO(ID)));
        if (attributeList.size() == 0) {
            throw new ERException(String.format("Attribute with ID: %d not found ", ID));
        } else {
            return attributeList.get(0);
        }
    }
}
