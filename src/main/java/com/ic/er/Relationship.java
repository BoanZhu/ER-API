package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ic.er.Exception.ERException;
import com.ic.er.common.*;
import com.ic.er.entity.RelationshipDO;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
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
    private LayoutInfo layoutInfo;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    protected Relationship(Long ID, String name, Long viewID, Entity firstEntity, Entity secondEntity, Cardinality cardinality, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
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

    public static List<Relationship> queryByRelationship(RelationshipDO RelationshipDO) {
        return Trans.TransRelationshipListFromDB(ER.relationshipMapper.selectByRelationship(RelationshipDO));
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

    public void updateInfo(String name, Entity firstEntity, Entity secondEntity, Cardinality cardinality) {
        if (name != null) {
            this.name = name;
        }
        if (firstEntity != null) {
            this.firstEntity = firstEntity;
        }
        if (secondEntity != null) {
            this.secondEntity = secondEntity;
        }
        if (cardinality != null) {
            this.cardinality = cardinality;
        }
        int res = ER.relationshipMapper.updateByID(new RelationshipDO(this.ID, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.cardinality, 0, this.gmtCreate, new Date()));
        if (res == 0) {
            throw new ERException(String.format("cannot find Relationship with ID: %d", this.ID));
        }
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.update(layoutX, layoutY, height, width);
    }

}
