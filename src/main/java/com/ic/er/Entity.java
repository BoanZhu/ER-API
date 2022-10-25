package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ic.er.Exception.ERException;
import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.AttributeDO;
import com.ic.er.entity.EntityDO;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Entity {
    @JsonIgnore
    private Long ID;
    private String name;
    @JsonIgnore
    private Long viewID;
    private List<Attribute> attributeList;
    private LayoutInfo layoutInfo;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.setLayoutX(layoutX);
        this.layoutInfo.setLayoutY(layoutY);
        this.layoutInfo.setHeight(height);
        this.layoutInfo.setWidth(width);
        this.layoutInfo.update();
    }

    public Entity(Long ID, String name, Long viewID, List<Attribute> attributeList, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
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
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.ENTITY, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public Attribute addAttribute(String attributeName, DataType dataType, int isPrimary, int isForeign) {
        Attribute attribute = new Attribute(0L, this.ID, this.viewID, attributeName, dataType, isPrimary, isForeign, null, new Date(), new Date());
        this.attributeList.add(attribute);
        if (ER.useDB) {
            this.update();
        }
        return attribute;
    }

    public boolean deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        if (ER.useDB) {
            attribute.deleteDB();
            this.update();
        }
        return false;
    }

    private static Entity TransformFromDB(EntityDO entityDO) {
        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(entityDO.getID(), entityDO.getViewID()));
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(entityDO.getID(), RelatedObjType.ENTITY);
        return new Entity(entityDO.getID(), entityDO.getName(), entityDO.getViewID(), attributeList, layoutInfo,
                entityDO.getGmtCreate(), entityDO.getGmtModified());
    }

    private static List<Entity> TransListFormFromDB(List<EntityDO> doList) {
        List<Entity> ret = new ArrayList<>();
        for (EntityDO EntityDO : doList) {
            ret.add(TransformFromDB(EntityDO));
        }
        return ret;
    }

    public static List<Entity> queryByEntity(EntityDO entityDO) {
        List<EntityDO> entityDOList = ER.entityMapper.selectByEntity(entityDO);
        return TransListFormFromDB(entityDOList);
    }

    public static Entity queryByID(Long ID) {
        List<Entity> entityDOList = queryByEntity(new EntityDO(ID));
        if (entityDOList.size() == 0) {
            throw new ERException(String.format("Entity with ID: %d not found ", ID));
        } else {
            return entityDOList.get(0);
        }
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
        ER.entityMapper.deleteByID(this.ID);
    }

    public void update() throws ERException {
        int ret = ER.entityMapper.updateByID(new EntityDO(this.ID, this.name, this.viewID, 0, this.gmtCreate, new Date()));
        if (ret == 0) {
            throw new ERException(String.format("cannot find Attribute with ID: %d", this.ID));
        }
    }
}
