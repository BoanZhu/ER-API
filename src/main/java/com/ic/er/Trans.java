package com.ic.er;

import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.AttributeDO;
import com.ic.er.entity.LayoutInfoDO;
import com.ic.er.entity.RelationshipDO;
import com.ic.er.entity.ViewDO;

import java.util.ArrayList;
import java.util.List;

public class Trans {

    protected static LayoutInfo TransformFromDB(LayoutInfoDO layoutInfoDO) {
        return new LayoutInfo(layoutInfoDO.getID(), layoutInfoDO.getRelatedObjID(), layoutInfoDO.getRelatedObjType(), layoutInfoDO.getLayoutX(), layoutInfoDO.getLayoutY(), layoutInfoDO.getHeight(), layoutInfoDO.getWidth());
    }

    protected static List<LayoutInfo> TransListFormDB(List<LayoutInfoDO> doList) {
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
                attributeDO.getIsForeign(), layoutInfo, attributeDO.getGmtCreate(), attributeDO.getGmtModified());
    }

    protected static <T> T TransformFromDB(T element) {
        if (returnType.equals(Boolean.class)) {

        }
        return new LayoutInfo(layoutInfoDO.getID(), layoutInfoDO.getRelatedObjID(), layoutInfoDO.getRelatedObjType(), layoutInfoDO.getLayoutX(), layoutInfoDO.getLayoutY(), layoutInfoDO.getHeight(), layoutInfoDO.getWidth());
    }

    protected static <T> List<T> TransListFromDB(List<T> doList) {
        List<T> ret = new ArrayList<>();
        for (T element : doList) {
            ret.add(TransformFromDB(element));
        }
        return ret;
    }

    protected static Relationship TransformFromDB(RelationshipDO relationshipDO) {
        LayoutInfo layoutInfo = LayoutInfo.queryByObjIDAndObjType(relationshipDO.getID(), RelatedObjType.RELATIONSHIP);
        return new Relationship(relationshipDO.getID(), relationshipDO.getName(), relationshipDO.getViewID(),
                Entity.queryByID(relationshipDO.getFirstEntityID()), Entity.queryByID(relationshipDO.getSecondEntityID()),
                relationshipDO.getCardinality(), layoutInfo,
                relationshipDO.getGmtCreate(), relationshipDO.getGmtModified());
    }

    protected static List<Relationship> TransListFromDB(List<RelationshipDO> doList) {
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

    protected static List<View> TransListFromDB(List<ViewDO> doList) {
        List<View> ret = new ArrayList<>();
        for (ViewDO ViewDO : doList) {
            ret.add(TransformFromDB(ViewDO));
        }
        return ret;
    }

}
