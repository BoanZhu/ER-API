package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Data;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class RelationshipEdge {
    private Long ID;
    private Long relationshipID;
    private Long schemaID;
    private ERConnectableObj connObj;
    private Cardinality cardinality;
    private Integer portAtRelationship;
    private Integer portAtBelongObj;
    private Date gmtCreate;
    private Date gmtModified;

    protected RelationshipEdge(Long ID, Long relationshipID, Long schemaID, ERConnectableObj connObj,
                               Cardinality cardinality, Integer portAtRelationship, Integer portAtBelongObj, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.relationshipID = relationshipID;
        this.schemaID = schemaID;
        this.connObj = connObj;
        this.cardinality = cardinality;
        this.portAtRelationship = portAtRelationship;
        this.portAtBelongObj = portAtBelongObj;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            insertDB();
        }
    }

    private void insertDB() {
        try {
            RelationshipEdgeDO edgeDO = new RelationshipEdgeDO(0L, this.relationshipID, this.schemaID,
                    this.connObj.getID(), BelongObjType.ENTITY, this.cardinality, this.portAtRelationship, this.portAtBelongObj, 0, new Date(), new Date());
            int ret = ER.relationshipEdgeMapper.insert(edgeDO);
            if (ret == 0) {
                throw new ERException("relationshipEdge insert db fail");
            }
            this.ID = edgeDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("relationshipEdge insertDB fail", e);
        }
    }

    public void updateInfo(Cardinality cardinality, ERConnectableObj connObj) throws ERException {
        if (cardinality != null) {
            this.cardinality = cardinality;
        }
        if (connObj != null) {
            Relationship relationship = Relationship.queryByID(this.relationshipID);
            List<ERConnectableObj> belongObjList = new ArrayList<>();
            for (RelationshipEdge edge : relationship.getEdgeList()) {
                belongObjList.add(edge.getConnObj());
            }
            belongObjList.add(connObj);
            belongObjList.remove(this.getConnObj());
            if (checkEntitesInSameRelationship(belongObjList)) {
                throw new ERException("entities have been in the same relationship");
            }
            this.connObj = connObj;
        }
        ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(this.ID, this.relationshipID, this.schemaID, this.connObj.getID(), BelongObjType.ENTITY, this.cardinality, this.portAtRelationship, this.portAtBelongObj, 0, this.gmtCreate, new Date()));
    }

    // check if these entities have been in the same relationship
    protected static boolean checkEntitesInSameRelationship(List<ERConnectableObj> belongObjList) {
        List<Long> entityIDs = new ArrayList<>();
        List<Long> relationshipIDs = new ArrayList<>();
        for (ERConnectableObj connObj : belongObjList) {
            if (connObj instanceof Entity) {
                entityIDs.add(connObj.getID());
            } else if (connObj instanceof Relationship) {
                relationshipIDs.add(connObj.getID());
            }
        }
        Long entityNum = 0L, relationshipNum = 0L;
        if (entityIDs.size() != 0) {
            List<CaseInsensitiveMap<String, Object>> numList = ER.relationshipEdgeMapper.groupCountEntityNum(entityIDs, BelongObjType.ENTITY);
            if (numList != null) {
                for (Map<String, Object> objectMap : numList) {
                    if (objectMap.get("belong_obj_num") != null) {
                        entityNum = (Long) objectMap.get("belong_obj_num");
                    }
                }
            }
        }
        if (relationshipIDs.size() != 0) {
            List<CaseInsensitiveMap<String, Object>> numList = ER.relationshipEdgeMapper.groupCountEntityNum(relationshipIDs, BelongObjType.RELATIONSHIP);
            if (numList != null) {
                for (Map<String, Object> objectMap : numList) {
                    if (objectMap.get("belong_obj_num") != null) {
                        relationshipNum = (Long) objectMap.get("belong_obj_num");
                    }
                }
            }
        }
        if (entityNum + relationshipNum == entityIDs.size() + relationshipIDs.size()) {
            return true;
        }
        return false;
    }

    public void updatePorts(Integer portAtRelationship, Integer portAtEntity) throws ERException {
        if (portAtRelationship != null) {
            this.portAtRelationship = portAtRelationship;
        }
        if (portAtEntity != null) {
            this.portAtBelongObj = portAtEntity;
        }
        ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(this.ID, this.relationshipID, this.schemaID, this.connObj.getID(), BelongObjType.ENTITY, this.cardinality, this.portAtRelationship, this.portAtBelongObj, 0, this.gmtCreate, new Date()));
    }

    public static List<RelationshipEdge> query(RelationshipEdgeDO relationshipEdgeDO) {
        return ObjConv.ConvRelationshipEdgeListFromDB(ER.relationshipEdgeMapper.selectByRelationshipEdge(relationshipEdgeDO));
    }

    public static RelationshipEdge queryByID(Long ID) throws ERException {
        List<RelationshipEdge> edgeList = query(new RelationshipEdgeDO(ID));
        if (edgeList.size() == 0) {
            throw new ERException(String.format("RelationshipEdge with ID: %d not found ", ID));
        } else {
            return edgeList.get(0);
        }
    }

    protected void deleteDB() {
        ER.relationshipEdgeMapper.deleteByID(this.ID);
    }
}