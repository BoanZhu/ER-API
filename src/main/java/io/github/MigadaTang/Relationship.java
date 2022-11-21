package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.entity.AttributeDO;
import io.github.MigadaTang.entity.RelationshipDO;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.github.MigadaTang.RelationshipEdge.checkEntitesInSameRelationship;

@Getter
public class Relationship {
    private Long ID;
    private String name;
    private Long schemaID;
    private List<Attribute> attributeList;
    private List<RelationshipEdge> edgeList;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;

    // do not handle layout info during creation, use update to add layout info
    protected Relationship(Long ID, String name, Long schemaID, List<Attribute> attributeList, List<RelationshipEdge> edgeList, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.schemaID = schemaID;
        this.attributeList = attributeList;
        this.edgeList = edgeList;
        this.layoutInfo = layoutInfo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            insertDB();
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
        } catch (PersistenceException e) {
            throw new ERException("relationship insertDB fail", e);
        }
    }

    public static List<Relationship> query(RelationshipDO RelationshipDO) {
        return Trans.TransRelationshipListFromDB(ER.relationshipMapper.selectByRelationship(RelationshipDO));
    }

    public static Relationship queryByID(Long ID) throws ERException {
        List<Relationship> relationships = query(new RelationshipDO(ID));
        if (relationships.size() == 0) {
            throw new ERException(String.format("Relationship with ID: %d not found ", ID));
        } else {
            return relationships.get(0);
        }
    }

    protected void deleteDB() {
        // delete the attributes of this relationship
        for (Attribute attribute : this.attributeList) {
            attribute.deleteDB();
        }

        // delete the edges of this relationship
        for (RelationshipEdge edge : this.edgeList) {
            edge.deleteDB();
        }

        // delete relationship
        ER.relationshipMapper.deleteByID(this.ID);
    }

    public void deleteEdge(RelationshipEdge edge) {
        edge.deleteDB();
        this.getEdgeList().remove(edge);
    }

    public Attribute addAttribute(String attributeName, DataType dataType, Boolean nullable) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        List<Attribute> attributeList = Attribute.query(new AttributeDO(this.ID, BelongObjType.RELATIONSHIP, this.schemaID, attributeName));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", this.name));
        }
        Attribute attribute = new Attribute(0L, this.ID, BelongObjType.RELATIONSHIP, this.schemaID, attributeName, dataType, false, nullable, -1, null, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    public void deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        attribute.deleteDB();
    }

    public RelationshipEdge linkEntity(Entity entity, Cardinality cardinality) {
        if (Entity.queryByID(entity.getID()) == null) {
            throw new ERException(String.format("entity with ID: %d not found", entity.getID()));
        }
        if (!entity.getSchemaID().equals(this.schemaID)) {
            throw new ERException(String.format("entity: %s does not belong to this schema", entity.getName()));
        }
        List<RelationshipEdge> relationshipEdges = RelationshipEdge.query(new RelationshipEdgeDO(this.ID, entity.getID()));
        if (relationshipEdges.size() != 0) {
            throw new ERException(String.format("relationship edge already exists, ID: %d", relationshipEdges.get(0).getID()));
        }
        Relationship relationship = Relationship.queryByID(this.ID);
        List<Long> entityIDs = new ArrayList<>();
        for (RelationshipEdge edge : relationship.getEdgeList()) {
            entityIDs.add(edge.getEntity().getID());
        }
        entityIDs.add(entity.getID());
        if (checkEntitesInSameRelationship(entityIDs)) {
            throw new ERException("entities have been in the same relationship");
        }
        RelationshipEdge edge = new RelationshipEdge(0L, this.ID, this.schemaID, entity, cardinality, -1, -1, new Date(), new Date());
        this.edgeList.add(edge);
        return edge;
    }

    public void updateInfo(String name) {
        if (name != null) {
            this.name = name;
        }
        ER.relationshipMapper.updateByID(new RelationshipDO(this.ID, this.name, this.schemaID, 0, this.gmtCreate, new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY) throws ERException {
        if (this.layoutInfo == null) {
            this.layoutInfo = new LayoutInfo(0L, this.ID, BelongObjType.RELATIONSHIP, layoutX, layoutY);
        }
        this.layoutInfo.update(layoutX, layoutY);
    }

}