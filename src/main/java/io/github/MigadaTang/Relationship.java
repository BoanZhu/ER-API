package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
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


@Getter
public class Relationship extends ERBaseObj implements ERConnectableObj {
    private List<Attribute> attributeList;
    private List<RelationshipEdge> edgeList;

    // do not handle layout info during creation, use update to add layout info
    protected Relationship(Long ID, String name, Long schemaID, List<Attribute> attributeList, List<RelationshipEdge> edgeList, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        super(ID, schemaID, name, layoutInfo, gmtCreate, gmtModified);
        this.attributeList = attributeList;
        this.edgeList = edgeList;
        if (getID() == 0) {
            setID(insertDB());
        }
    }


    private Long insertDB() {
        try {
            // insert relationship
            RelationshipDO relationshipDO = new RelationshipDO(0L, getName(), getSchemaID(), 0, getGmtCreate(), getGmtModified());
            int ret = ER.relationshipMapper.insert(relationshipDO);
            if (ret == 0) {
                throw new ERException("relationship insertDB fail");
            }
            return relationshipDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("relationship insertDB fail", e);
        }
    }

    public static List<Relationship> query(RelationshipDO relationshipDO) {
        return query(relationshipDO, true);
    }

    public static List<Relationship> query(RelationshipDO relationshipDO, boolean cascade) {
        return ObjConv.ConvRelationshipListFromDB(ER.relationshipMapper.selectByRelationship(relationshipDO), cascade);
    }

    public static Relationship queryByID(Long ID) throws ERException {
        return queryByID(ID, true);
    }

    public static Relationship queryByID(Long ID, boolean cascade) throws ERException {
        List<Relationship> relationships = query(new RelationshipDO(ID), cascade);
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
        ER.relationshipMapper.deleteByID(getID());
    }

    public void deleteEdge(RelationshipEdge edge) {
        edge.deleteDB();
        this.getEdgeList().remove(edge);
    }

    public Attribute addAttribute(String attributeName, DataType dataType, AttributeType attributeType) {
        if (attributeName.equals("")) {
            throw new ERException("attributeName cannot be empty");
        }
        List<Attribute> attributeList = Attribute.query(new AttributeDO(getID(), BelongObjType.RELATIONSHIP, getSchemaID(), attributeName));
        if (attributeList.size() != 0) {
            throw new ERException(String.format("attribute with name: %s already exists", getName()));
        }
        Attribute attribute = new Attribute(0L, getID(), BelongObjType.RELATIONSHIP, getSchemaID(), attributeName, dataType, false, attributeType, -1, null, new Date(), new Date());
        this.attributeList.add(attribute);
        return attribute;
    }

    public void deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        attribute.deleteDB();
    }

    public RelationshipEdge linkEntity(ERConnectableObj belongObj, Cardinality cardinality) {
        if (Entity.queryByID(belongObj.getID()) == null) {
            throw new ERException(String.format("entity with ID: %d not found", belongObj.getID()));
        }
        if (!belongObj.getSchemaID().equals(getSchemaID())) {
            throw new ERException(String.format("entity: %s does not belong to this schema", belongObj.getName()));
        }
        List<RelationshipEdge> relationshipEdges = RelationshipEdge.query(new RelationshipEdgeDO(getID(), belongObj));
        if (relationshipEdges.size() != 0) {
            throw new ERException(String.format("relationship edge already exists, ID: %d", relationshipEdges.get(0).getID()));
        }
        Relationship relationship = Relationship.queryByID(getID());
        List<ERConnectableObj> belongObjList = new ArrayList<>();
        for (RelationshipEdge edge : relationship.getEdgeList()) {
            belongObjList.add(edge.getConnObj());
        }
        belongObjList.add(belongObj);
        if (RelationshipEdge.checkEntitesInSameRelationship(belongObjList)) {
            throw new ERException("entities have been in the same relationship");
        }
        RelationshipEdge edge = new RelationshipEdge(0L, getID(), getSchemaID(), belongObj, cardinality, -1, -1, new Date(), new Date());
        this.edgeList.add(edge);
        return edge;
    }

    public void updateInfo(String name) {
        if (name != null) {
            setName(name);
        }
        ER.relationshipMapper.updateByID(new RelationshipDO(getID(), getName(), getSchemaID(), 0, getGmtCreate(), new Date()));
    }

    public void updateLayoutInfo(Double layoutX, Double layoutY) throws ERException {
        if (getLayoutInfo() == null) {
            setLayoutInfo(new LayoutInfo(0L, getID(), BelongObjType.RELATIONSHIP, layoutX, layoutY));
        }
        getLayoutInfo().update(layoutX, layoutY);
    }

}