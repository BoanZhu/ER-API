package io.github.MigadaTang;

import io.github.MigadaTang.common.RelatedObjType;
import io.github.MigadaTang.entity.*;

import java.util.ArrayList;
import java.util.List;

public class Trans {

    protected static LayoutInfo TransformFromDB(LayoutInfoDO layoutInfoDO) {
        return new LayoutInfo(layoutInfoDO.getID(), layoutInfoDO.getRelatedObjID(), layoutInfoDO.getRelatedObjType(), layoutInfoDO.getLayoutX(), layoutInfoDO.getLayoutY(), layoutInfoDO.getHeight(), layoutInfoDO.getWidth());
    }

    protected static List<LayoutInfo> TransLayoutInfoListFormDB(List<LayoutInfoDO> doList) {
        List<LayoutInfo> ret = new ArrayList<>();
        for (LayoutInfoDO LayoutInfoDO : doList) {
            ret.add(TransformFromDB(LayoutInfoDO));
        }
        return ret;
    }

    protected static Attribute TransformFromDB(AttributeDO attributeDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(attributeDO.getID(), RelatedObjType.ATTRIBUTE);
        return new Attribute(attributeDO.getID(), attributeDO.getEntityID(), attributeDO.getSchemaID(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(), attributeDO.getNullable(),
                layoutInfo, 0.0, 0.0, attributeDO.getGmtCreate(), attributeDO.getGmtModified());
    }

    protected static List<Attribute> TransAttributeListFromDB(List<AttributeDO> doList) {
        List<Attribute> ret = new ArrayList<>();
        for (AttributeDO attributeDO : doList) {
            ret.add(TransformFromDB(attributeDO));
        }
        return ret;
    }

    protected static Entity TransformFromDB(EntityDO entityDO) {
        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(entityDO.getID(), entityDO.getSchemaID()));
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(entityDO.getID(), RelatedObjType.ENTITY);
        return new Entity(entityDO.getID(), entityDO.getName(), entityDO.getSchemaID(), attributeList, layoutInfo, null, null,
                entityDO.getGmtCreate(), entityDO.getGmtModified());
    }

    protected static List<Entity> TransEntityListFormFromDB(List<EntityDO> doList) {
        List<Entity> ret = new ArrayList<>();
        for (EntityDO EntityDO : doList) {
            ret.add(TransformFromDB(EntityDO));
        }
        return ret;
    }


    protected static Relationship TransformFromDB(RelationshipDO relationshipDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(relationshipDO.getID(), RelatedObjType.RELATIONSHIP);
        return new Relationship(relationshipDO.getID(), relationshipDO.getName(), relationshipDO.getSchemaID(),
                Entity.queryByID(relationshipDO.getFirstEntityID()), Entity.queryByID(relationshipDO.getSecondEntityID()),
                relationshipDO.getFirstCardinality(), relationshipDO.getSecondCardinality(), layoutInfo,
                relationshipDO.getGmtCreate(), relationshipDO.getGmtModified());
    }

    protected static List<Relationship> TransRelationshipListFromDB(List<RelationshipDO> doList) {
        List<Relationship> ret = new ArrayList<>();
        for (RelationshipDO RelationshipDO : doList) {
            ret.add(TransformFromDB(RelationshipDO));
        }
        return ret;
    }

    protected static Schema TransformFromDB(SchemaDO schema) {
        List<Entity> entityList = Entity.queryByEntity(new EntityDO(null, null, schema.getID(), null, null, null));
        List<Relationship> relationshipList = Relationship.queryByRelationship(new RelationshipDO(null, null, schema.getID(), null, null, null, null, null, null, null));
        return new Schema(schema.getID(), schema.getName(), entityList, relationshipList, schema.getCreator(),
                schema.getGmtCreate(), schema.getGmtModified());
    }

    protected static List<Schema> TransSchemaListFromDB(List<SchemaDO> doList) {
        List<Schema> ret = new ArrayList<>();
        for (SchemaDO SchemaDO : doList) {
            ret.add(TransformFromDB(SchemaDO));
        }
        return ret;
    }

}
