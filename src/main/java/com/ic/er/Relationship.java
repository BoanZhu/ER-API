package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ic.er.exception.ERException;
import com.ic.er.common.*;
import com.ic.er.entity.RelationshipDO;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Getter
@JsonSerialize(using = RelationshipSerializer.class)
@JsonIgnoreProperties({"id", "viewID", "gmtCreate", "gmtModified"})
public class Relationship {
    private Long ID;
    private String name;
    private Long viewID;
    private Entity firstEntity;
    private Entity secondEntity;
    private Cardinality firstCardinality;
    private Cardinality secondCardinality;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Relationship(Long ID, String name, Long viewID, Entity firstEntity, Entity secondEntity, Cardinality firstCardinality, Cardinality secondCardinality, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
        this.firstCardinality = firstCardinality;
        this.secondCardinality = secondCardinality;
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
                    0L, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.firstCardinality, this.secondCardinality,
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

    public void updateInfo(String name, Entity firstEntity, Entity secondEntity, Cardinality firstCardinality, Cardinality secondCardinality) {
        if (name != null) {
            this.name = name;
        }
        if (firstEntity != null) {
            this.firstEntity = firstEntity;
            if (Entity.queryByID(firstEntity.getID()) == null) {
                throw new ERException(String.format("entity with ID: %d not found", firstEntity.getID()));
            }
        }
        if (secondEntity != null) {
            this.secondEntity = secondEntity;
            if (Entity.queryByID(secondEntity.getID()) == null) {
                throw new ERException(String.format("entity with ID: %d not found", secondEntity.getID()));
            }
        }
        if (firstCardinality != null) {
            this.firstCardinality = firstCardinality;
        }
        if (secondCardinality != null) {
            this.secondCardinality = secondCardinality;
        }
        if (firstEntity != null && secondEntity != null) {
            List<Relationship> oldRelationshipList = Relationship.queryByRelationship(new RelationshipDO(firstEntity.getID(), secondEntity.getID()));
            if (oldRelationshipList.size() != 0 && !oldRelationshipList.get(0).getID().equals(this.ID)) {
                throw new ERException(String.format("relation between entity %s and %s already exists", firstEntity.getName(), secondEntity.getName()));
            }
        }
        ER.relationshipMapper.updateByID(new RelationshipDO(this.ID, this.name, this.viewID, this.firstEntity.getID(), this.secondEntity.getID(), this.firstCardinality, this.secondCardinality, 0, this.gmtCreate, new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.update(layoutX, layoutY, height, width);
    }

}
