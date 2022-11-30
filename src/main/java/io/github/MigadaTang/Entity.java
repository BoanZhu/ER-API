package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.entity.AttributeDO;
import io.github.MigadaTang.entity.EntityDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

/**
 * The entity in ER schema
 */
@Getter
public class Entity extends ERBaseObj implements ERConnectableObj {
    /**
     * The type of the entity
     */
    private EntityType entityType;
    /**
     * The entity to which this entity belongs, only applicable to subsets
     */
    private Entity belongStrongEntity;
    /**
     * The list of attributes this entity contains
     */
    private List<Attribute> attributeList;
    /**
     * The port to which this entity points, only applicable to subsets
     */
    private Integer aimPort;

    protected Entity(Long ID, String name, Long schemaID, EntityType entityType, Entity belongStrongEntity, List<Attribute> attributeList, Integer aimPort, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        super(ID, schemaID, name, BelongObjType.ENTITY, layoutInfo, gmtCreate, gmtModified);
        this.entityType = entityType;
        this.belongStrongEntity = belongStrongEntity;
        this.attributeList = attributeList;
        this.aimPort = aimPort;
        if (getID() == 0) {
            setID(insertDB());
        }
    }

    /**
     * Add an attribute that is primary key
     *
     * @param attributeName the name of the attribute
     * @param dataType      the type of data this attribute contains
     * @return the created attribute
     */
    public Attribute addPrimaryKey(String attributeName, DataType dataType) {
        return addAttribute(attributeName, dataType, true, AttributeType.Mandatory);
    }

    /**
     * Add an attribute that is not primary key
     *
     * @param attributeName the name of the attribute
     * @param dataType      the type of data this attribute contains
     * @param attributeType the type of this attribute
     * @return the created attribute
     */
    public Attribute addAttribute(String attributeName, DataType dataType, AttributeType attributeType) {
        return addAttribute(attributeName, dataType, false, attributeType);
    }


    /**
     * Add attribute onto the entity
     *
     * @param attributeName the name of the attribute
     * @param dataType      the type of data this attribute contains
     * @param isPrimary     whether this is a primary key
     * @param attributeType the type of this attribute
     * @return the created attribute
     */
    public Attribute addAttribute(String attributeName, DataType dataType, Boolean isPrimary, AttributeType attributeType) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        if (isPrimary && attributeType != AttributeType.Mandatory) {
            throw new ERException("primary attribute must be mandatory");
        }
        List<Attribute> attributeList = Attribute.query(new AttributeDO(null, getID(), BelongObjType.ENTITY, getSchemaID(), attributeName, null, null, null, null, null, null, null));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", getName()));
        }
        attributeList = Attribute.query(new AttributeDO(null, getID(), BelongObjType.ENTITY, getSchemaID(), null, null, true, null, null, null, null, null));
        if (isPrimary && attributeList.size() != 0) {
            throw new ERException(String.format("primary key already exists, name: %s", attributeList.get(0).getName()));
        }
        Attribute attribute = new Attribute(0L, getID(), BelongObjType.ENTITY, getSchemaID(), attributeName, dataType, isPrimary, attributeType, -1, null, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    /**
     * Remove the target attribute from both the list and the database
     *
     * @param attribute the target attribute
     */
    public void deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        attribute.deleteDB();
    }

    private Long insertDB() {
        try {
            Long belongStrongEntityID = null;
            if (this.getBelongStrongEntity() != null) {
                belongStrongEntityID = this.belongStrongEntity.getID();
            }
            EntityDO entityDO = new EntityDO(0L, getName(), getSchemaID(), this.entityType, belongStrongEntityID, this.aimPort, 0, getGmtCreate(), getGmtModified());
            int ret = ER.entityMapper.insert(entityDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            return entityDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    /**
     * Delete the current entity from the database and cascade delete all the related attributes
     */
    protected void deleteDB() {
        for (Attribute attribute : attributeList) {
            attribute.deleteDB();
        }
        ER.entityMapper.deleteByID(getID());
    }

    /**
     * Update certain attributes of this entity, set parameters as null if they are not expected to be updated
     *
     * @param name               The name of this entity
     * @param entityType         The type of this entity
     * @param belongStrongEntity The strong entity that this entity(must be a subset) belongs to
     */
    public void updateInfo(String name, EntityType entityType, Entity belongStrongEntity) {
        if (name != null) {
            setName(name);
            List<Entity> entities = Entity.query(new EntityDO(name, getSchemaID(), null));
            if (entities.size() != 0 && !entities.get(0).getID().equals(getID())) {
                throw new ERException(String.format("entity with name: %s already exists", name));
            }
        }
        Long belongStrongEntityID = null;
        if (belongStrongEntity != null) {
            this.belongStrongEntity = belongStrongEntity;
            belongStrongEntityID = belongStrongEntity.getID();
        }
        if (entityType != null) {
            if (entityType != EntityType.SUBSET) {
                removeBelongStrongEntity();
            }
            this.entityType = entityType;
        }
        if (this.belongStrongEntity != null) {
            belongStrongEntityID = this.belongStrongEntity.getID();
        }
        ER.entityMapper.updateByID(new EntityDO(getID(), getName(), getSchemaID(), this.entityType, belongStrongEntityID, this.aimPort, 0, getGmtCreate(), new Date()));
    }

    /**
     * Remove the entity that this subset belongs to
     */
    public void removeBelongStrongEntity() {
        if (this.entityType != EntityType.SUBSET) {
            return;
        }
        this.belongStrongEntity = null;
        ER.entityMapper.updateByID(new EntityDO(getID(), getName(), getSchemaID(), this.entityType, null, this.aimPort, 0, getGmtCreate(), new Date()));
    }


    /**
     * Update the port of the strong entity that this subset belongs to
     *
     * @param aimPort The port of the strong entity
     */
    public void updateAimPort(Integer aimPort) {
        if (entityType != EntityType.SUBSET) {
            return;
        }
        if (aimPort != null) {
            this.aimPort = aimPort;
        }
        Long belongStrongEntityID = null;
        if (this.belongStrongEntity != null) {
            belongStrongEntityID = this.belongStrongEntity.getID();
        }
        ER.entityMapper.updateByID(new EntityDO(getID(), this.aimPort, belongStrongEntityID));
    }

    /**
     * Query the list of entities that have the same data specified by entityDO exhaustively
     *
     * @param entityDO The values of some attributes of an entity
     * @return A list of entities
     */
    public static List<Entity> query(EntityDO entityDO) {
        return query(entityDO, true);
    }

    /**
     * Query the list of entities that have the same data specified by entityDO
     *
     * @param entityDO   The values of some attributes of an entity
     * @param exhaustive Whether to fetch the related strong entity
     * @return A list of entities
     */
    public static List<Entity> query(EntityDO entityDO, boolean exhaustive) {
        List<EntityDO> entityDOList = ER.entityMapper.selectByEntity(entityDO);
        return ObjConv.ConvEntityListFormFromDB(entityDOList, exhaustive);
    }

    /**
     * Find the entity that has this ID exhaustively
     *
     * @param ID ID The ID of the entity
     * @return The found entity
     * @throws ERException Throws an ERException if no entity is found
     */
    public static Entity queryByID(Long ID) throws ERException {
        return queryByID(ID, true);
    }

    /**
     * Find the entity that has this ID
     *
     * @param ID         ID The ID of the entity
     * @param exhaustive Whether to fetch the related strong entity
     * @return The found entity
     * @throws ERException Throws an ERException if no entity is found
     */
    public static Entity queryByID(Long ID, boolean exhaustive) throws ERException {
        List<Entity> entityDOList = query(new EntityDO(ID), exhaustive);
        if (entityDOList.size() == 0) {
            throw new ERException(String.format("entity with ID: %d not found", ID));
        } else {
            return entityDOList.get(0);
        }
    }

}
