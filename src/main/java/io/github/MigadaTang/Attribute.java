package io.github.MigadaTang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.entity.AttributeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Getter
@JsonIgnoreProperties({"id", "entityID", "schemaID", "gmtCreate", "gmtModified"})
public class Attribute {
    private Long ID;
    private Long belongObjID;
    private BelongObjType belongObjType;
    private Long schemaID;
    private String name;
    private DataType dataType;
    private Boolean isPrimary;
    private Boolean nullable;
    private Integer aimPort;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Attribute(Long ID, Long belongObjID, BelongObjType belongObjType, Long schemaID, String name, DataType dataType,
                        Boolean isPrimary, Boolean nullable, Integer aimPort, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.belongObjID = belongObjID;
        this.belongObjType = belongObjType;
        this.schemaID = schemaID;
        this.name = name;
        this.dataType = dataType;
        this.isPrimary = isPrimary;
        this.nullable = nullable;
        this.aimPort = aimPort;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            this.insertDB();
        }
    }

    private void insertDB() throws PersistenceException {
        try {
            AttributeDO aDo = new AttributeDO(this.ID, this.belongObjID, this.belongObjType, this.schemaID, this.name, this.dataType, this.isPrimary, this.nullable, this.aimPort, 0, this.gmtCreate, this.gmtModified);
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
            List<Attribute> attributeList = Attribute.query(new AttributeDO(this.belongObjID, this.belongObjType, this.schemaID, name));
            if (attributeList.size() != 0 && !attributeList.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("attribute with name: %s already exists", this.name));
            }
        }
        if (isPrimary != null && isPrimary) {
            List<Attribute> attributeList = Attribute.query(new AttributeDO(this.belongObjID, this.belongObjType, this.schemaID, null));
            if (attributeList.size() != 0 && !attributeList.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("attribute that is primary key already exists, name: %s", attributeList.get(0).getName()));
            }
        }
        if (this.nullable && this.isPrimary) {
            throw new ERException("primary attribute cannot be null");
        }
        ER.attributeMapper.updateByID(new AttributeDO(this.ID, this.belongObjID, this.belongObjType, this.schemaID, this.name, this.dataType, this.isPrimary, this.nullable, this.aimPort, 0, this.gmtCreate, new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY) throws ERException {
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, BelongObjType.ATTRIBUTE, layoutX, layoutY);
        }
        this.layoutInfo.update(layoutX, layoutY);
    }

    public void updateAimPort(Integer aimPort) throws ERException {
        if (this.aimPort != null) {
            this.aimPort = aimPort;
        }
        ER.attributeMapper.updateByID(new AttributeDO(this.ID, this.belongObjID, this.belongObjType, this.schemaID, this.name, this.dataType, this.isPrimary, this.nullable, this.aimPort, 0, this.gmtCreate, new Date()));
    }

    public static List<Attribute> query(AttributeDO attributeDO) {
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
