package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.*;

/**
 * The edge connecting relationships and entities
 */
@Getter
public class RelationshipEdge {
    private Long ID;
    private Long relationshipID;
    private Long schemaID;
    private ERConnectableObj connObj;
    private Cardinality cardinality;
    private Boolean isKey;
    private Integer portAtRelationship;
    private Integer portAtBelongObj;
    private Date gmtCreate;
    private Date gmtModified;

    protected RelationshipEdge(Long ID, Long relationshipID, Long schemaID, ERConnectableObj connObj,
                               Cardinality cardinality, Boolean isKey, Integer portAtRelationship, Integer portAtBelongObj, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.relationshipID = relationshipID;
        this.schemaID = schemaID;
        this.connObj = connObj;
        this.cardinality = cardinality;
        this.isKey = isKey;
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
                    this.connObj.getID(), this.getConnObjType(), this.cardinality, this.isKey, this.portAtRelationship, this.portAtBelongObj, 0, new Date(), new Date());
            int ret = ER.relationshipEdgeMapper.insert(edgeDO);
            if (ret == 0) {
                throw new ERException("relationshipEdge insert db fail");
            }
            this.ID = edgeDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("relationshipEdge insertDB fail", e);
        }
    }

    /**
     * get the type of the connectable object
     *
     * @return returns the type of the target object
     */
    public BelongObjType getConnObjType() {
        if (this.connObj instanceof Entity) {
            return BelongObjType.ENTITY;
        } else if (this.connObj instanceof Relationship) {
            return BelongObjType.RELATIONSHIP;
        }
        return BelongObjType.UNKNOWN;
    }

    /**
     * Update the information of a relationship, set parameters as null if they are not expected to be updated
     *
     * @param relationshipID the new relationshipID
     * @param cardinality    the new name of this relationship
     * @param connObj        the new target object
     * @param isKey          whether this is a key relationship
     */
    public void updateInfo(Long relationshipID, Cardinality cardinality, ERConnectableObj connObj, Boolean isKey) throws ERException {
        if (relationshipID != null) {
            this.relationshipID = relationshipID;
        }
        if (cardinality != null) {
            this.cardinality = cardinality;
        }
        if (isKey != null) {
            this.isKey = isKey;
        }
        if (connObj != null) {
//            Relationship relationship = Relationship.queryByID(this.relationshipID);
//            List<ERConnectableObj> belongObjList = new ArrayList<>();
//            for (RelationshipEdge edge : relationship.getEdgeList()) {
//                belongObjList.add(edge.getConnObj());
//            }
//            belongObjList.add(connObj);
//            belongObjList.remove(this.getConnObj());
//            if (checkEntitesInSameRelationship(belongObjList)) {
//                throw new ERException("entities have been in the same relationship");
//            }
            this.connObj = connObj;
        }
        ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(this.ID, this.relationshipID, this.schemaID, this.connObj.getID(), this.getConnObjType(), this.cardinality, this.isKey, this.portAtRelationship, this.portAtBelongObj, 0, this.gmtCreate, new Date()));
    }

    /**
     * check if these connectable objects are in another relationship other than the current relationship
     *
     * @param currentRelationshipID the current relationship connecting these objects
     * @param belongObjList         the list of object
     * @return whether these connectable objects are in another relationship
     */
    protected static boolean checkInSameRelationship(Long currentRelationshipID, List<ERConnectableObj> belongObjList) {
        if (belongObjList.size() <= 1) {
            return false;
        }
        List<Long> entityIDs = new ArrayList<>();
        List<Long> relationshipIDs = new ArrayList<>();
        Map<Long, Long> objCountMap = new HashMap<>();
        for (ERConnectableObj connObj : belongObjList) {
            if (connObj instanceof Entity) {
                entityIDs.add(connObj.getID());
            } else if (connObj instanceof Relationship) {
                relationshipIDs.add(connObj.getID());
            }
        }
        if (entityIDs.size() != 0) {
            List<CaseInsensitiveMap<String, Object>> numList = ER.relationshipEdgeMapper.groupCountEntityNum(entityIDs, BelongObjType.ENTITY);
            if (numList != null) {
                for (Map<String, Object> objectMap : numList) {
                    objCountMap.put((Long) objectMap.get("relationship_id"), (Long) objectMap.get("belong_obj_num"));
                }
            }
        }
        if (relationshipIDs.size() != 0) {
            List<CaseInsensitiveMap<String, Object>> numList = ER.relationshipEdgeMapper.groupCountEntityNum(relationshipIDs, BelongObjType.RELATIONSHIP);
            if (numList != null) {
                for (Map<String, Object> objectMap : numList) {
                    Long relationshipNum = (Long) objectMap.get("belong_obj_num");
                    Long relationshipID = (Long) objectMap.get("relationship_id");
                    objCountMap.put(relationshipID, objCountMap.getOrDefault(relationshipID, 0L) + relationshipNum);
                }
            }
        }
        for (Map.Entry<Long, Long> entry : objCountMap.entrySet()) {
            if (!entry.getKey().equals(currentRelationshipID) && entry.getValue() == entityIDs.size() + relationshipIDs.size()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update both the port to which each ends of the relationship edge points
     *
     * @param portAtRelationship the port to which relationship end points
     * @param portAtBelongObj    the port to which target object points
     */
    public void updatePorts(Integer portAtRelationship, Integer portAtBelongObj) {
        if (portAtRelationship != null) {
            this.portAtRelationship = portAtRelationship;
        }
        if (portAtBelongObj != null) {
            this.portAtBelongObj = portAtBelongObj;
        }
        ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(this.ID, this.relationshipID, this.schemaID, this.connObj.getID(), this.getConnObjType(), this.cardinality, this.isKey, this.portAtRelationship, this.portAtBelongObj, 0, this.gmtCreate, new Date()));
    }

    /**
     * Query the list of relationshipEdges that have the same data specified by entityDO
     *
     * @param relationshipEdgeDO The values of some attributes of a relationship edge
     * @return a list of relationship edges
     */
    public static List<RelationshipEdge> query(RelationshipEdgeDO relationshipEdgeDO) {
        return ObjConv.ConvRelationshipEdgeListFromDB(ER.relationshipEdgeMapper.selectByRelationshipEdge(relationshipEdgeDO));
    }

    /**
     * Find the relationshipEdge that has this ID
     *
     * @param ID the ID of the relationship edge
     * @return the found relationship edge
     * @throws ERException throws ERException if no relationship is found
     */
    public static RelationshipEdge queryByID(Long ID) throws ERException {
        List<RelationshipEdge> edgeList = query(new RelationshipEdgeDO(ID));
        if (edgeList.size() == 0) {
            throw new ERException(String.format("RelationshipEdge with ID: %d not found ", ID));
        } else {
            return edgeList.get(0);
        }
    }

    /**
     * Delete the current relationship edge from the database
     */
    protected void deleteDB() {
        ER.relationshipEdgeMapper.deleteByID(this.ID);
    }
}