package com.ic.er;

import com.ic.er.bean.entity.EntityDO;
import com.ic.er.bean.entity.RelationshipDO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class Relationship {
    private Long ID;
    private String name;
    private Long viewID;
    private Long firstEntityID;
    private Long secondEntityID;
    private Long firstAttributeID;
    private Long secondAttributeID;
    private Cardinality cardinality;
    private Date gmtCreate;
    private Date gmtModified;

    public Relationship(Long ID, String name, Long viewID, Long firstEntityID, Long secondEntityID, Long firstAttributeID, Long secondAttributeID, Cardinality cardinality, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.firstEntityID = firstEntityID;
        this.secondEntityID = secondEntityID;
        this.firstAttributeID = firstAttributeID;
        this.secondAttributeID = secondAttributeID;
        this.cardinality = cardinality;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            this.ID = Utils.generateID();
            if (ER.useDB) {
                insertDB();
            }
        }
    }

    int insertDB() {
        return ER.relationshipMapper.insert(new RelationshipDO(
                0L,
                this.name,
                this.viewID,
                this.firstEntityID,
                this.secondEntityID,
                this.firstAttributeID,
                this.secondAttributeID,
                this.cardinality,
                0,
                this.gmtCreate,
                this.gmtModified
        ));
    }

    public static Relationship TransformFromDB(RelationshipDO relationshipDO) {
        return new Relationship(relationshipDO.getId(), relationshipDO.getName(), relationshipDO.getView_id(),
                relationshipDO.getFirst_entity_id(), relationshipDO.getSecond_entity_id(),
                relationshipDO.getFirst_attribute_id(), relationshipDO.getSecond_attribute_id(),
                relationshipDO.getCardinality(),
                relationshipDO.getGmt_create(), relationshipDO.getGmt_modified());
    }

    public static List<Relationship> TransListFormFromDB(List<RelationshipDO> doList) {
        List<Relationship> ret = new ArrayList<>();
        for (RelationshipDO RelationshipDO : doList) {
            ret.add(TransformFromDB(RelationshipDO));
        }
        return ret;
    }

    public static List<Relationship> queryByRelationship(RelationshipDO RelationshipDO) {
        return TransListFormFromDB(ER.relationshipMapper.selectByRelationship(RelationshipDO));
    }

    ResultState deleteDB() {
        int res = ER.relationshipMapper.deleteById(this.ID);
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }

    ResultState updateDB() {
        int res = ER.relationshipMapper.updateById(new RelationshipDO());
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }
}
