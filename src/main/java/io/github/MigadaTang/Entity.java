package io.github.MigadaTang;

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
public class Entity {
    private Long ID;
    private String name;
    private Long schemaID;
    private EntityType entityType;
    private Entity belongStrongEntity;
    private List<Attribute> attributeList;
    private Integer aimPort;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Entity(Long ID, String name, Long schemaID, EntityType entityType, Entity belongStrongEntity, List<Attribute> attributeList, Integer aimPort, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.schemaID = schemaID;
        this.entityType = entityType;
        this.belongStrongEntity = belongStrongEntity;
        this.attributeList = attributeList;
        this.aimPort = aimPort;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            this.insertDB();
        }
    }


    // addAttribute without the layout information
    public Attribute addAttribute(String attributeName, DataType dataType, Boolean isPrimary, Boolean nullable) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        if (isPrimary && nullable) {
            throw new ERException("primary attribute cannot be null");
        }
        List<Attribute> attributeList = Attribute.query(new AttributeDO(null, this.ID, BelongObjType.ENTITY, this.schemaID, attributeName, null, null, null, null, null, null, null));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", this.name));
        }
        attributeList = Attribute.query(new AttributeDO(null, this.ID, BelongObjType.ENTITY, this.schemaID, null, null, null, true, null, null, null, null));
        if (isPrimary && attributeList.size() != 0) {
            throw new ERException(String.format("attribute that is primary key already exists, name: %s", attributeList.get(0).getName()));
        }
        Attribute attribute = new Attribute(0L, this.ID, BelongObjType.ENTITY, this.schemaID, attributeName, dataType, isPrimary, nullable, -1, null, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    public void deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        attribute.deleteDB();
    }

    private void insertDB() {
        try {
            Long belongStrongEntityID = null;
            if (this.getBelongStrongEntity() != null) {
                belongStrongEntityID = this.belongStrongEntity.getID();
            }
            EntityDO entityDO = new EntityDO(0L, this.name, this.schemaID, this.entityType, belongStrongEntityID, this.aimPort, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.entityMapper.insert(entityDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = entityDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    protected void deleteDB() {
        for (Attribute attribute : attributeList) {
            attribute.deleteDB();
        }
        ER.entityMapper.deleteByID(this.ID);
    }

    public void updateInfo(String name) {
        if (name != null) {
            this.name = name;
            List<Entity> entities = Entity.query(new EntityDO(name, this.schemaID, null));
            if (entities.size() != 0 && !entities.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("entity with name: %s already exists", name));
            }
        }
        Long belongStrongEntityID = null;
        if (this.belongStrongEntity != null) {
            belongStrongEntityID = this.belongStrongEntity.getID();
        }
        ER.entityMapper.updateByID(new EntityDO(this.ID, this.name, this.schemaID, this.entityType, belongStrongEntityID, this.aimPort, 0, this.gmtCreate, new Date()));
    }


    public void updateLayoutInfo(Double layoutX, Double layoutY) {
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, BelongObjType.ENTITY, layoutX, layoutY);
        }
        this.layoutInfo.update(layoutX, layoutY);
    }

    public void updateAimPort(Integer aimPort) throws ERException {
        if (aimPort != null) {
            this.aimPort = aimPort;
        }
        ER.entityMapper.updateByID(new EntityDO(this.ID, this.aimPort));
    }

    public static List<Entity> query(EntityDO entityDO) {
        List<EntityDO> entityDOList = ER.entityMapper.selectByEntity(entityDO);
        return Trans.TransEntityListFormFromDB(entityDOList);
    }

    public static Entity queryByID(Long ID) {
        List<Entity> entityDOList = query(new EntityDO(ID));
        if (entityDOList.size() == 0) {
            throw new ERException(String.format("entity with ID: %d not found", ID));
        } else {
            return entityDOList.get(0);
        }
    }

}
