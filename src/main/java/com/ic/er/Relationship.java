package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ic.er.bean.entity.RelationshipDO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.RelationshipSerializer;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonSerialize(using = RelationshipSerializer.class)
public class Relationship {
    @JsonIgnore
    private Long ID;
    private String name;
    @JsonIgnore
    private Long viewID;
    private Entity firstEntity;
    private Entity secondEntity;
    private Cardinality cardinality;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    public Relationship(Long ID, String name, Long viewID, Entity firstEntity, Entity secondEntity, Cardinality cardinality, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
        this.cardinality = cardinality;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            if (ER.useDB) {
                insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
    }

    private int insertDB() {
        RelationshipDO relationshipDO = new RelationshipDO(
                0L, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.cardinality,
                0, this.gmtCreate, this.gmtModified);
        int ret = ER.relationshipMapper.insert(relationshipDO);
        this.ID = relationshipDO.getId();
        return ret;
    }

    private static Relationship TransformFromDB(RelationshipDO relationshipDO) {
        return new Relationship(relationshipDO.getId(), relationshipDO.getName(), relationshipDO.getViewId(),
                Entity.queryByID(relationshipDO.getFirstEntityId()), Entity.queryByID(relationshipDO.getSecondEntityId()),
                relationshipDO.getCardinality(),
                relationshipDO.getGmtCreate(), relationshipDO.getGmtModified());
    }

    private static List<Relationship> TransListFormFromDB(List<RelationshipDO> doList) {
        List<Relationship> ret = new ArrayList<>();
        for (RelationshipDO RelationshipDO : doList) {
            ret.add(TransformFromDB(RelationshipDO));
        }
        return ret;
    }

    public static List<Relationship> queryByRelationship(RelationshipDO RelationshipDO) {
        return TransListFormFromDB(ER.relationshipMapper.selectByRelationship(RelationshipDO));
    }

    public static Relationship queryByID(Long ID) {
        List<Relationship> relationships = queryByRelationship(new RelationshipDO(ID));
        if (relationships.size() == 0) {
            return null;
        } else {
            return relationships.get(0);
        }
    }

    protected ResultState deleteDB() {
        int res = ER.relationshipMapper.deleteById(this.ID);
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }

    public ResultState update() {
        int res = ER.relationshipMapper.updateById(new RelationshipDO(this.ID, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.cardinality, 0, this.gmtCreate, this.gmtModified));
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }
}
