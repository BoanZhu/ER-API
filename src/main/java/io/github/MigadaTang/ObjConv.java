package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.entity.*;

import java.util.ArrayList;
import java.util.List;

public class ObjConv {

    protected static LayoutInfo ConvFromDB(LayoutInfoDO layoutInfoDO) {
        return new LayoutInfo(layoutInfoDO.getID(), layoutInfoDO.getBelongObjID(), layoutInfoDO.getBelongObjType(), layoutInfoDO.getLayoutX(), layoutInfoDO.getLayoutY());
    }

    protected static List<LayoutInfo> ConvLayoutInfoListFormDB(List<LayoutInfoDO> doList) {
        List<LayoutInfo> ret = new ArrayList<>();
        for (LayoutInfoDO LayoutInfoDO : doList) {
            ret.add(ConvFromDB(LayoutInfoDO));
        }
        return ret;
    }

    protected static Attribute ConvFromDB(AttributeDO attributeDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(attributeDO.getID(), BelongObjType.ATTRIBUTE);
        return new Attribute(attributeDO.getID(), attributeDO.getBelongObjId(), attributeDO.getBelongObjType(), attributeDO.getSchemaID(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(), attributeDO.getAttributeType(), attributeDO.getAimPort(),
                layoutInfo, attributeDO.getGmtCreate(), attributeDO.getGmtModified());
    }

    protected static List<Attribute> ConvAttributeListFromDB(List<AttributeDO> doList) {
        List<Attribute> ret = new ArrayList<>();
        for (AttributeDO attributeDO : doList) {
            ret.add(ConvFromDB(attributeDO));
        }
        return ret;
    }

    protected static Entity ConvFromDB(EntityDO entityDO) {
        List<Attribute> attributeList = Attribute.query(new AttributeDO(entityDO.getID(), BelongObjType.ENTITY, entityDO.getSchemaID(), null));
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(entityDO.getID(), BelongObjType.ENTITY);
        Entity strongEntity = null;
        if (entityDO.getBelongStrongEntityID() != null && entityDO.getBelongStrongEntityID() != 0) {
            strongEntity = Entity.queryByID(entityDO.getBelongStrongEntityID());
        }
        return new Entity(entityDO.getID(), entityDO.getName(), entityDO.getSchemaID(), entityDO.getEntityType(), strongEntity, attributeList, entityDO.getAimPort(), layoutInfo, entityDO.getGmtCreate(), entityDO.getGmtModified());
    }

    protected static List<Entity> ConvEntityListFormFromDB(List<EntityDO> doList) {
        List<Entity> ret = new ArrayList<>();
        for (EntityDO EntityDO : doList) {
            ret.add(ConvFromDB(EntityDO));
        }
        return ret;
    }


    protected static Relationship ConvFromDB(RelationshipDO relationshipDO, boolean cascade) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(relationshipDO.getID(), BelongObjType.RELATIONSHIP);
        List<RelationshipEdge> edgeList = null;
        List<Attribute> attributeList = null;
        if (cascade) {
            edgeList = RelationshipEdge.query(new RelationshipEdgeDO(relationshipDO.getID(), null));
            attributeList = Attribute.query(new AttributeDO(relationshipDO.getID(), BelongObjType.RELATIONSHIP, null, null));
        }
        return new Relationship(relationshipDO.getID(), relationshipDO.getName(), relationshipDO.getSchemaID(), attributeList, edgeList,
                layoutInfo, relationshipDO.getGmtCreate(), relationshipDO.getGmtModified());
    }

    protected static List<Relationship> ConvRelationshipListFromDB(List<RelationshipDO> doList, boolean cascade) {
        List<Relationship> ret = new ArrayList<>();
        for (RelationshipDO RelationshipDO : doList) {
            ret.add(ConvFromDB(RelationshipDO, cascade));
        }
        return ret;
    }

    protected static RelationshipEdge ConvFromDB(RelationshipEdgeDO edgeDO) {
        ERConnectableObj connObj = null;
        if (edgeDO.getBelongObjType() == BelongObjType.RELATIONSHIP) {
            connObj = Relationship.queryByID(edgeDO.getBelongObjID(), false);
        } else if (edgeDO.getBelongObjType() == BelongObjType.ENTITY) {
            connObj = Entity.queryByID(edgeDO.getBelongObjID());
        }
        return new RelationshipEdge(edgeDO.getID(), edgeDO.getRelationshipID(), edgeDO.getSchemaID(),
                connObj, edgeDO.getCardinality(), edgeDO.getPortAtRelationship(), edgeDO.getPortAtBelongObj(),
                edgeDO.getGmtCreate(), edgeDO.getGmtModified());
    }

    protected static List<RelationshipEdge> ConvRelationshipEdgeListFromDB(List<RelationshipEdgeDO> doList) {
        List<RelationshipEdge> ret = new ArrayList<>();
        for (RelationshipEdgeDO relationshipEdgeDO : doList) {
            ret.add(ConvFromDB(relationshipEdgeDO));
        }
        return ret;
    }

    protected static Schema ConvFromDB(SchemaDO schema) {
        List<Entity> entityList = Entity.query(new EntityDO(null, schema.getID(), null));
        List<Relationship> relationshipList = Relationship.query(new RelationshipDO(null, schema.getID()), true);
        return new Schema(schema.getID(), schema.getName(), entityList, relationshipList, schema.getCreator(), schema.getGmtCreate(), schema.getGmtModified());
    }

    protected static List<Schema> ConvSchemaListFromDB(List<SchemaDO> doList) {
        List<Schema> ret = new ArrayList<>();
        for (SchemaDO SchemaDO : doList) {
            ret.add(ConvFromDB(SchemaDO));
        }
        return ret;
    }

}
