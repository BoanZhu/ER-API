package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.dao.AttributeDAO;
import io.github.MigadaTang.entity.AttributeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

/**
 * The attributes on entities and relationships
 */
@Getter
public class Attribute extends ERBaseObj {
    /**
     * The ID of the object to which this attribute belongs
     */
    private Long belongObjID;
    /**
     * The type of the object to which this attribute belongs
     */
    private BelongObjType belongObjType;
    /**
     * The type of data this attribute contains
     */
    private DataType dataType;
    /**
     * Whether this attribute is a primary key, multiple primary keys form a composite primary key
     */
    private Boolean isPrimary;
    /**
     * The number of values this attribute contains
     */
    private AttributeType attributeType;
    /**
     * The port of an entity to which this attribute points
     */
    private Integer aimPort;

    protected Attribute(Long ID, Long belongObjID, BelongObjType belongObjType, Long schemaID, String name, DataType dataType,
                        Boolean isPrimary, AttributeType attributeType, Integer aimPort, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        super(ID, schemaID, name, BelongObjType.ATTRIBUTE, layoutInfo, gmtCreate, gmtModified);
        this.belongObjID = belongObjID;
        this.belongObjType = belongObjType;
        this.dataType = dataType;
        this.isPrimary = isPrimary;
        this.attributeType = attributeType;
        this.aimPort = aimPort;
        if (getID() == 0) {
            setID(insertDB());
        }
    }

    private Long insertDB() throws PersistenceException {
        try {
            AttributeDO aDo = new AttributeDO(getID(), this.belongObjID, this.belongObjType, getSchemaID(), getName(), this.dataType, this.isPrimary, this.attributeType, this.aimPort, 0, getGmtCreate(), getGmtModified());
            int ret = AttributeDAO.insert(aDo);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            return aDo.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    /**
     * Delete the current attribute from the database
     */
    protected void deleteDB() {
        AttributeDAO.deleteByID(getID());
    }

    /**
     * Update certain attributes of this entity, set parameters as null if they are not expected to be updated
     *
     * @param name          The name of this entity
     * @param dataType      The type of data this attribute contains
     * @param attributeType The number of values this attribute contains
     * @param isPrimary     Whether this attribute is a primary key
     */
    public void updateInfo(String name, DataType dataType, Boolean isPrimary, AttributeType attributeType) throws ERException {
        if (name != null) {
            setName(name);
        }
        if (dataType != null) {
            this.dataType = dataType;
        }
        if (isPrimary != null) {
            this.isPrimary = isPrimary;
        }
        if (attributeType != null) {
            this.attributeType = attributeType;
        }
        if (name != null) {
            List<Attribute> attributeList = Attribute.query(new AttributeDO(this.belongObjID, this.belongObjType, getSchemaID(), name));
            if (attributeList.size() != 0 && !attributeList.get(0).getID().equals(getID())) {
                throw new ERException(String.format("attribute with name: %s already exists", getName()));
            }
        }
        if (this.isPrimary && this.attributeType != AttributeType.Mandatory) {
            throw new ERException("primary attribute must be mandatory");
        }
        AttributeDAO.updateByID(new AttributeDO(getID(), this.belongObjID, this.belongObjType, getSchemaID(), getName(), this.dataType, this.isPrimary, this.attributeType, this.aimPort, 0, getGmtCreate(), new Date()));
    }


    /**
     * Update the port to which this attribute points to
     *
     * @param aimPort The port of the entity
     */
    public void updateAimPort(Integer aimPort) throws ERException {
        if (aimPort != null) {
            this.aimPort = aimPort;
        }
        AttributeDAO.updateByID(new AttributeDO(getID(), this.belongObjID, this.belongObjType, getSchemaID(), getName(), this.dataType, this.isPrimary, this.attributeType, this.aimPort, 0, getGmtCreate(), new Date()));
    }

    /**
     * Query the list of entities that have the same data specified by entityDO
     *
     * @param attributeDO values of some attributes of an attribute
     * @return A list of attributes
     */
    public static List<Attribute> query(AttributeDO attributeDO) {
        return ObjConv.ConvAttributeListFromDB(AttributeDAO.selectByAttribute(attributeDO));
    }

    /**
     * Find the attribute that has this ID
     *
     * @param ID The ID of the attribute
     * @return The found attribute
     * @throws ERException throws an ERException if no attribute is found
     */
    public static Attribute queryByID(Long ID) throws ERException {
        List<Attribute> attributeList = ObjConv.ConvAttributeListFromDB(AttributeDAO.selectByAttribute(new AttributeDO(ID)));
        if (attributeList.size() == 0) {
            throw new ERException(String.format("Attribute with ID: %d not found ", ID));
        } else {
            return attributeList.get(0);
        }
    }
}
