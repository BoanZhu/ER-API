package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.entity.*;

import java.util.ArrayList;
import java.util.List;

public class Trans {

    protected static LayoutInfo TransformFromDB(LayoutInfoDO layoutInfoDO) {
        return new LayoutInfo(layoutInfoDO.getID(), layoutInfoDO.getBelongObjID(), layoutInfoDO.getBelongObjType(), layoutInfoDO.getLayoutX(), layoutInfoDO.getLayoutY());
    }

    protected static List<LayoutInfo> TransLayoutInfoListFormDB(List<LayoutInfoDO> doList) {
        List<LayoutInfo> ret = new ArrayList<>();
        for (LayoutInfoDO LayoutInfoDO : doList) {
            ret.add(TransformFromDB(LayoutInfoDO));
        }
        return ret;
    }

    protected static Attribute TransformFromDB(AttributeDO attributeDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(attributeDO.getID(), BelongObjType.ATTRIBUTE);
        return new Attribute(attributeDO.getID(), attributeDO.getBelongObjId(), attributeDO.getBelongObjType(), attributeDO.getSchemaID(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(), attributeDO.getNullable(), attributeDO.getAimPort(),
                layoutInfo, attributeDO.getGmtCreate(), attributeDO.getGmtModified());
    }

    protected static List<Attribute> TransAttributeListFromDB(List<AttributeDO> doList) {
        List<Attribute> ret = new ArrayList<>();
        for (AttributeDO attributeDO : doList) {
            ret.add(TransformFromDB(attributeDO));
        }
        return ret;
    }

    protected static Entity TransformFromDB(EntityDO entityDO) {
        List<Attribute> attributeList = Attribute.query(new AttributeDO(entityDO.getID(), BelongObjType.ENTITY, entityDO.getSchemaID(), null));
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(entityDO.getID(), BelongObjType.ENTITY);
        Entity strongEntity = null;
        if (entityDO.getBelongStrongEntityID() != null && entityDO.getBelongStrongEntityID() != 0) {
            strongEntity = Entity.queryByID(entityDO.getBelongStrongEntityID());
        }
        return new Entity(entityDO.getID(), entityDO.getName(), entityDO.getSchemaID(), entityDO.getEntityType(), strongEntity, attributeList, entityDO.getAimPort(), layoutInfo, entityDO.getGmtCreate(), entityDO.getGmtModified());
    }

    protected static List<Entity> TransEntityListFormFromDB(List<EntityDO> doList) {
        List<Entity> ret = new ArrayList<>();
        for (EntityDO EntityDO : doList) {
            ret.add(TransformFromDB(EntityDO));
        }
        return ret;
    }


    protected static Relationship TransformFromDB(RelationshipDO relationshipDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(relationshipDO.getID(), BelongObjType.RELATIONSHIP);
        List<RelationshipEdge> edgeList = RelationshipEdge.query(new RelationshipEdgeDO(relationshipDO.getID(), null));
        List<Attribute> attributeList = Attribute.query(new AttributeDO(relationshipDO.getID(), BelongObjType.RELATIONSHIP, null, null));
        return new Relationship(relationshipDO.getID(), relationshipDO.getName(), relationshipDO.getSchemaID(), attributeList, edgeList,
                layoutInfo, relationshipDO.getGmtCreate(), relationshipDO.getGmtModified());
    }

    protected static List<Relationship> TransRelationshipListFromDB(List<RelationshipDO> doList) {
        List<Relationship> ret = new ArrayList<>();
        for (RelationshipDO RelationshipDO : doList) {
            ret.add(TransformFromDB(RelationshipDO));
        }
        return ret;
    }

    protected static RelationshipEdge TransformFromDB(RelationshipEdgeDO edgeDO) {
        Entity entity = Entity.queryByID(edgeDO.getEntityID());
        return new RelationshipEdge(edgeDO.getID(), edgeDO.getRelationshipID(), edgeDO.getSchemaID(),
                entity, edgeDO.getCardinality(), edgeDO.getPortAtRelationship(), edgeDO.getPortAtEntity(),
                edgeDO.getGmtCreate(), edgeDO.getGmtModified());
    }

    protected static List<RelationshipEdge> TransRelationshipEdgeListFromDB(List<RelationshipEdgeDO> doList) {
        List<RelationshipEdge> ret = new ArrayList<>();
        for (RelationshipEdgeDO relationshipEdgeDO : doList) {
            ret.add(TransformFromDB(relationshipEdgeDO));
        }
        return ret;
    }

    protected static Schema TransformFromDB(SchemaDO schema) {
        List<Entity> entityList = Entity.query(new EntityDO(null, schema.getID(), null));
        List<Relationship> relationshipList = Relationship.query(new RelationshipDO(null, schema.getID()));
        return new Schema(schema.getID(), schema.getName(), entityList, relationshipList, schema.getCreator(), schema.getGmtCreate(), schema.getGmtModified());
    }

    protected static List<Schema> TransSchemaListFromDB(List<SchemaDO> doList) {
        List<Schema> ret = new ArrayList<>();
        for (SchemaDO SchemaDO : doList) {
            ret.add(TransformFromDB(SchemaDO));
        }
        return ret;
    }

}
