package com.ic.er;

import com.ic.er.bean.entity.AttributeDO;
import com.ic.er.bean.entity.EntityDO;
import com.ic.er.bean.vo.AttributeVO;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class Entity {
    private Long ID;
    private String name;
    private Long viewID;
    private List<Attribute> attributeList;
    private Date gmtCreate;
    private Date gmtModified;

    public Entity(Long ID, String name, Long viewID, List<Attribute> attributeList, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.attributeList = attributeList;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            this.ID = Utils.generateID();
            if (ER.useDB) {
                 insertDB();
            }
        }
    }

    public Attribute addAttribute(String attributeName, DataType dataType,
                        int isPrimary, int isForeign) {
        Attribute attribute = new Attribute(0L, this.ID, this.viewID, attributeName, dataType, isPrimary, isForeign, new Date(), new Date());
        this.attributeList.add(attribute);
        this.setGmtModified(new Date(System.currentTimeMillis()));
        if (ER.useDB) {
            this.updateDB();
        }
        return attribute;
    }

    public boolean removeAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        this.setGmtModified(new Date(System.currentTimeMillis()));
        if (ER.useDB) {
            this.updateDB();
        }
        return false;
    }

    public static Entity TransformFromDB(EntityDO EntityDO) {
        // todo add entity_id
        List<Attribute> attributeList = Attribute.queryByAttribute(null);
        return new Entity(EntityDO.getId(), EntityDO.getName(), EntityDO.getViewId(), attributeList,
                 EntityDO.getGmtCreate(), EntityDO.getGmtModified());
    }

    public static List<Entity> TransListFormFromDB(List<EntityDO> doList) {
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

    public long insertDB() {
        return ER.entityMapper.insert(new EntityDO(0L, this.name, this.viewID, 0, this.gmtCreate, this.gmtModified));
    }

    ResultState deleteDB() {
        int res = ER.entityMapper.deleteById(this.ID);
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }

    ResultState updateDB() {
        int res = ER.entityMapper.updateById(new EntityDO(this.ID, "", 0L, 0, new Date(), new Date()));
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }
}
