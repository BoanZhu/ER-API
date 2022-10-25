package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ic.er.Exception.ERException;
import com.ic.er.common.*;
import com.ic.er.dao.LayoutInfoMapper;
import com.ic.er.entity.RelationshipDO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonSerialize(using = RelationshipSerializer.class)
@NoArgsConstructor
public class Relationship {
    @JsonIgnore
    private Long ID;
    private String name;
    @JsonIgnore
    private Long viewID;
    private Entity firstEntity;
    private Entity secondEntity;
    private Cardinality cardinality;
    private LayoutInfo layoutInfo;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    public Relationship(Long ID, String name, Long viewID, Entity firstEntity, Entity secondEntity, Cardinality cardinality, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
        this.cardinality = cardinality;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            if (ER.useDB) {
                insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.RELATIONSHIP, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.setLayoutX(layoutX);
        this.layoutInfo.setLayoutY(layoutY);
        this.layoutInfo.setHeight(height);
        this.layoutInfo.setWidth(width);
        this.layoutInfo.update();
    }

    private void insertDB() {
        try {
            RelationshipDO relationshipDO = new RelationshipDO(
                    0L, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.cardinality,
                    0, this.gmtCreate, this.gmtModified);
            int ret = ER.relationshipMapper.insert(relationshipDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = relationshipDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    private static Relationship TransformFromDB(RelationshipDO relationshipDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(relationshipDO.getID(), RelatedObjType.RELATIONSHIP);
        return new Relationship(relationshipDO.getID(), relationshipDO.getName(), relationshipDO.getViewID(),
                Entity.queryByID(relationshipDO.getFirstEntityID()), Entity.queryByID(relationshipDO.getSecondEntityID()),
                relationshipDO.getCardinality(), layoutInfo,
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

    public static Relationship queryByID(Long ID) throws ERException {
        List<Relationship> relationships = queryByRelationship(new RelationshipDO(ID));
        if (relationships.size() == 0) {
            throw new ERException(String.format("Relationship with ID: %d not found ", ID));
        } else {
            return relationships.get(0);
        }
    }

    protected void deleteDB() {
        ER.relationshipMapper.deleteByID(this.ID);
    }

    public void update() {
        int res = ER.relationshipMapper.updateByID(new RelationshipDO(this.ID, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.cardinality, 0, this.gmtCreate, this.gmtModified));
        if (res == 0) {
            throw new ERException(String.format("cannot find Relationship with ID: %d", this.ID));
        }
    }
}
