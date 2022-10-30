package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ic.er.exception.ERException;
import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.AttributeDO;
import com.ic.er.entity.EntityDO;
import com.ic.er.common.DataType;
import com.ic.er.common.Utils;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Getter
@JsonIgnoreProperties({"id", "viewID", "gmtCreate", "gmtModified"})
public class Entity {
    private Long ID;
    private String name;
    private Long viewID;
    private List<Attribute> attributeList;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Entity(Long ID, String name, Long viewID, List<Attribute> attributeList, LayoutInfo layoutInfo, Double layoutX, Double layoutY, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.attributeList = attributeList;
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
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.ENTITY, layoutX, layoutY, 0.0, 0.0);
        }
    }


    public Attribute addAttribute(String attributeName, DataType dataType, Boolean isPrimary) {
        return addAttribute(attributeName, dataType, isPrimary, 0.0, 0.0);
    }

    public Attribute addAttribute(String attributeName, DataType dataType, Boolean isPrimary, Double layoutX, Double layoutY) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.ID, this.viewID, attributeName, null, null, null, null, null));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", this.name));
        }
        attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.ID, this.viewID, null, null, true, null, null, null));
        if (isPrimary && attributeList.size() != 0) {
            throw new ERException(String.format("attribute that is primary key already exists, name: %s", attributeList.get(0).getName()));
        }
        Attribute attribute = new Attribute(0L, this.ID, this.viewID, attributeName, dataType, isPrimary, null, layoutX, layoutY, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    public boolean deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        if (ER.useDB) {
            attribute.deleteDB();
        }
        return false;
    }

    private void insertDB() {
        try {
            EntityDO entityDO = new EntityDO(0L, this.name, this.viewID, 0, this.gmtCreate, this.gmtModified);
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
        }
        List<Entity> entities = Entity.queryByEntity(new EntityDO(null, name, this.ID, null, null, null));
        if (entities.size() != 0) {
            throw new ERException(String.format("entity with name: %s already exists", name));
        }
        ER.entityMapper.updateByID(new EntityDO(this.ID, this.name, this.viewID, 0, this.gmtCreate, new Date()));
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
