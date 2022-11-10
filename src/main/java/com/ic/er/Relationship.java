package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ic.er.common.*;
import com.ic.er.entity.AttributeDO;
import com.ic.er.entity.RelationshipDO;
import com.ic.er.entity.RelationshipEdgeDO;
import com.ic.er.exception.ERException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Getter
@JsonSerialize(using = RelationshipSerializer.class)
@JsonIgnoreProperties({"id", "schemaID", "gmtCreate", "gmtModified"})
public class Relationship {
    private Long ID;
    private String name;
    private Long schemaID;
    private List<EntityWithCardinality> entityWithCardinalityList;
    private List<Attribute> attributeList;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    protected Relationship(Long ID, String name, Long schemaID, List<Attribute> attributeList, List<EntityWithCardinality> entityWithCardinalityList, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.schemaID = schemaID;
        this.attributeList = attributeList;
        this.entityWithCardinalityList = entityWithCardinalityList;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            insertDB();
        }
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, RelatedObjType.RELATIONSHIP, 0.0, 0.0, 0.0, 0.0);
        }
    }


    private void insertDB() {
        try {
            // insert relationship
            RelationshipDO relationshipDO = new RelationshipDO(0L, this.name, this.schemaID, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.relationshipMapper.insert(relationshipDO);
            if (ret == 0) {
                throw new ERException("relationship insertDB fail");
            }
            this.ID = relationshipDO.getID();
            // todo support port
            // insert relationship edges
            for (EntityWithCardinality entityWithCardinality : this.entityWithCardinalityList) {
                ret = ER.relationshipEdgeMapper.insert(new RelationshipEdgeDO(0L, this.ID, this.schemaID,
                        entityWithCardinality.getEntity().getID(), entityWithCardinality.getCardinality(), 0, 0, 0, new Date(), new Date()));
                if (ret == 0) {
                    throw new ERException("relationshipEdge insert db fail");
                }
            }
        } catch (PersistenceException e) {
            throw new ERException("relationship insertDB fail", e);
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
        // delete relationship
        ER.relationshipMapper.deleteByID(this.ID);
        // delete relationship attributes

        // then delete all the relationship edges
        for (EntityWithCardinality eCard : this.entityWithCardinalityList) {
            ER.relationshipEdgeMapper.deleteByID(eCard.getID());
        }
    }

    public Attribute addAttribute(String attributeName, DataType dataType, Boolean nullable) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(null, this.ID, AttributeConnectObjType.RELATIONSHIP, this.schemaID, attributeName, null, null, null, null, null, null, null));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", this.name));
        }
        Attribute attribute = new Attribute(0L, this.ID, AttributeConnectObjType.RELATIONSHIP, this.schemaID, attributeName, dataType, false, nullable, null, null, null, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    public void updateInfo(String name, List<EntityWithCardinality> entityWithCardinalityList) {
        if (name != null) {
            this.name = name;
        }
        for (EntityWithCardinality eCard : entityWithCardinalityList) {
            Entity entity = eCard.getEntity();
            if (Entity.queryByID(entity.getID()) == null) {
                throw new ERException(String.format("entity with ID: %d not found", entity.getID()));
            }
            if (!entity.getSchemaID().equals(this.schemaID)) {
                throw new ERException(String.format("entity: %s does not belong to this schema", entity.getName()));
            }
            // todo add port support
            ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(eCard.getID(), null, null,
                    eCard.getEntity().getID(), eCard.getCardinality(), 0, 0, 0, eCard.getGmtCreate(), new Date()));
        }
        this.entityWithCardinalityList = entityWithCardinalityList;
        // todo query if relationship between all entities already exists
        ER.relationshipMapper.updateByID(new RelationshipDO(this.ID, this.name, this.schemaID, 0, this.gmtCreate, new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY, Double height, Double width) {
        this.layoutInfo.update(layoutX, layoutY, height, width);
    }

}

@AllArgsConstructor
@Data
class EntityWithCardinality {
    private Long ID;
    private Entity entity;
    private Cardinality cardinality;
    private Date gmtCreate;
    private Date gmtModified;
}