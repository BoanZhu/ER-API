package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ic.er.dto.entity.AttributeDO;
import com.ic.er.dto.entity.EntityDO;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Entity {
    @JsonIgnore
    private Long ID;
    private String name;
    @JsonIgnore
    private Long viewID;
    private List<Attribute> attributeList;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    public Entity(Long ID, String name, Long viewID, List<Attribute> attributeList, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.attributeList = attributeList;
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

    public Attribute addAttribute(String attributeName, DataType dataType,
                        int isPrimary, int isForeign) {
        Attribute attribute = new Attribute(0L, this.ID, this.viewID, attributeName, dataType, isPrimary, isForeign, new Date(), new Date());
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
        return new Entity(entityDO.getID(), entityDO.getName(), entityDO.getViewID(), attributeList,
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
            return null;
        } else {
            return entityDOList.get(0);
        }
    }

    private int insertDB() {
        EntityDO entityDO = new EntityDO(0L, this.name, this.viewID, 0, this.gmtCreate, this.gmtModified);
        int ret = ER.entityMapper.insert(entityDO);
        this.ID = entityDO.getID();
        return ret;
    }

    protected ResultState deleteDB() {
        int res = ER.entityMapper.deleteByID(this.ID);
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }

    public ResultState update() {
        int res = ER.entityMapper.updateByID(new EntityDO(this.ID, this.name, this.viewID, 0, this.gmtCreate, new Date()));
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }
}
