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

@Getter
public class Entity extends ERBaseObj implements ERConnectableObj {
    private EntityType entityType;
    private Entity belongStrongEntity;
    private List<Attribute> attributeList;
    private Integer aimPort;

    protected Entity(Long ID, String name, Long schemaID, EntityType entityType, Entity belongStrongEntity, List<Attribute> attributeList, Integer aimPort, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        super(ID, schemaID, name, layoutInfo, gmtCreate, gmtModified);
        this.entityType = entityType;
        this.belongStrongEntity = belongStrongEntity;
        this.attributeList = attributeList;
        this.aimPort = aimPort;
        if (getID() == 0) {
            setID(insertDB());
        }
    }


    // addAttribute without the layout information
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

    protected void deleteDB() {
        for (Attribute attribute : attributeList) {
            attribute.deleteDB();
        }
        ER.entityMapper.deleteByID(getID());
    }

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

    public void removeBelongStrongEntity() {
        if (this.entityType != EntityType.SUBSET) {
            return;
        }
        this.belongStrongEntity = null;
        ER.entityMapper.updateByID(new EntityDO(getID(), getName(), getSchemaID(), this.entityType, null, this.aimPort, 0, getGmtCreate(), new Date()));
    }


    public void updateLayoutInfo(Double layoutX, Double layoutY) {
        if (getLayoutInfo() == null) {
            setLayoutInfo(new LayoutInfo(0L, getID(), BelongObjType.ENTITY, layoutX, layoutY));
        }
        getLayoutInfo().update(layoutX, layoutY);
    }

    public void updateAimPort(Integer aimPort) throws ERException {
        if (aimPort != null) {
            this.aimPort = aimPort;
        }
        ER.entityMapper.updateByID(new EntityDO(getID(), this.aimPort));
    }

    public static List<Entity> query(EntityDO entityDO) {
        return query(entityDO, true);
    }

    public static List<Entity> query(EntityDO entityDO, boolean cascade) {
        List<EntityDO> entityDOList = ER.entityMapper.selectByEntity(entityDO);
        return ObjConv.ConvEntityListFormFromDB(entityDOList, cascade);
    }

    public static Entity queryByID(Long ID) {
        return queryByID(ID, true);
    }

    public static Entity queryByID(Long ID, boolean cascade) {
        List<Entity> entityDOList = query(new EntityDO(ID), cascade);
        if (entityDOList.size() == 0) {
            throw new ERException(String.format("entity with ID: %d not found", ID));
        } else {
            return entityDOList.get(0);
        }
    }

}
