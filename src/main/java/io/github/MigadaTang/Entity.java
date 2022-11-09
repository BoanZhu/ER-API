package io.github.MigadaTang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.RelatedObjType;
import io.github.MigadaTang.entity.AttributeDO;
import io.github.MigadaTang.entity.EntityDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Getter
@JsonIgnoreProperties({"id", "schemaID", "gmtCreate", "gmtModified"})
public class Entity {
    private Long ID;
    private String name;
    private Long schemaID;
    private List<Attribute> attributeList;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Entity(Long ID, String name, Long schemaID, List<Attribute> attributeList, LayoutInfo layoutInfo, Double layoutX, Double layoutY, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.schemaID = schemaID;
        this.attributeList = attributeList;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            this.insertDB();
        }
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.ENTITY, layoutX, layoutY, 0.0, 0.0);
        }
    }


    public Attribute addAttribute(String attributeName, DataType dataType, Boolean isPrimary, Boolean nullable) {
        return addAttribute(attributeName, dataType, isPrimary, nullable, 0.0, 0.0);
    }

    public Attribute addAttribute(String attributeName, DataType dataType, Boolean isPrimary, Boolean nullable, Double layoutX, Double layoutY) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        if (isPrimary && nullable) {
            throw new ERException("primary attribute cannot be null");
        }
        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.ID, this.schemaID, attributeName, null, null, null, null, null, null));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", this.name));
        }
        attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.ID, this.schemaID, null, null, null, true, null, null, null));
        if (isPrimary && attributeList.size() != 0) {
            throw new ERException(String.format("attribute that is primary key already exists, name: %s", attributeList.get(0).getName()));
        }
        Attribute attribute = new Attribute(0L, this.ID, this.schemaID, attributeName, dataType, isPrimary, nullable, null, layoutX, layoutY, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    public boolean deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        attribute.deleteDB();
        return false;
    }

    private void insertDB() {
        try {
            EntityDO entityDO = new EntityDO(0L, this.name, this.schemaID, 0, this.gmtCreate, this.gmtModified);
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
            List<Entity> entities = Entity.queryByEntity(new EntityDO(null, name, this.schemaID, null, null, null));
            if (entities.size() != 0 && !entities.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("entity with name: %s already exists", name));
            }
        }
        ER.entityMapper.updateByID(new EntityDO(this.ID, this.name, this.schemaID, 0, this.gmtCreate, new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.update(layoutX, layoutY, height, width);
    }

    public static List<Entity> queryByEntity(EntityDO entityDO) {
        List<EntityDO> entityDOList = ER.entityMapper.selectByEntity(entityDO);
        return Trans.TransEntityListFormFromDB(entityDOList);
    }

    public static Entity queryByID(Long ID) {
        List<Entity> entityDOList = queryByEntity(new EntityDO(ID));
        if (entityDOList.size() == 0) {
            throw new ERException(String.format("entity with ID: %d not found", ID));
        } else {
            return entityDOList.get(0);
        }
    }

}
