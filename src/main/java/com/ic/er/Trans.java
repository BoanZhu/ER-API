package com.ic.er;

import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.*;

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
        return new Attribute(attributeDO.getID(), attributeDO.getEntityID(), attributeDO.getViewID(),
                attributeDO.getName(), attributeDO.getDataType(), attributeDO.getIsPrimary(),
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
        List<Attribute> attributeList = Attribute.queryByAttribute(new AttributeDO(entityDO.getID(), entityDO.getViewID()));
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(entityDO.getID(), RelatedObjType.ENTITY);
        return new Entity(entityDO.getID(), entityDO.getName(), entityDO.getViewID(), attributeList, layoutInfo,
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
        return new Relationship(relationshipDO.getID(), relationshipDO.getName(), relationshipDO.getViewID(),
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

    protected static View TransformFromDB(ViewDO ViewDO) {
        List<Entity> entityList = Entity.queryByEntity(null);
        List<Relationship> relationshipList = Relationship.queryByRelationship(null);
        return new View(ViewDO.getID(), ViewDO.getName(), entityList, relationshipList, ViewDO.getCreator(),
                ViewDO.getGmtCreate(), ViewDO.getGmtModified());
    }

    protected static List<View> TransViewListFromDB(List<ViewDO> doList) {
        List<View> ret = new ArrayList<>();
        for (ViewDO ViewDO : doList) {
            ret.add(TransformFromDB(ViewDO));
        }
        return ret;
    }

}
