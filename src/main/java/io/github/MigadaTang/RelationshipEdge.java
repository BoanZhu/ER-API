package io.github.MigadaTang;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Data;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties({"id", "relationshipID", "schemaID", "gmtCreate", "gmtModified"})
public class RelationshipEdge {
    private Long ID;
    private Long relationshipID;
    private Long schemaID;
    private Entity entity;
    private Cardinality cardinality;
    private Integer portAtRelationship;
    private Integer portAtEntity;
    private Date gmtCreate;
    private Date gmtModified;

    protected RelationshipEdge(Long ID, Long relationshipID, Long schemaID, Entity entity, Cardinality cardinality, Integer portAtRelationship, Integer portAtEntity, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.relationshipID = relationshipID;
        this.schemaID = schemaID;
        this.entity = entity;
        this.cardinality = cardinality;
        this.portAtRelationship = portAtRelationship;
        this.portAtEntity = portAtEntity;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            insertDB();
        }
    }

    private void insertDB() {
        try {
            RelationshipEdgeDO edgeDO = new RelationshipEdgeDO(0L, this.relationshipID, this.schemaID,
                    this.entity.getID(), this.cardinality, this.portAtRelationship, this.portAtEntity, 0, new Date(), new Date());
            int ret = ER.relationshipEdgeMapper.insert(edgeDO);
            if (ret == 0) {
                throw new ERException("relationshipEdge insert db fail");
            }
            this.ID = edgeDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("relationshipEdge insertDB fail", e);
        }
    }

    public void updateInfo(Cardinality cardinality, Entity entity) throws ERException {
        if (cardinality != null) {
            this.cardinality = cardinality;
        }
        if (entity != null) {
            // todo check if there is already relationship between these entities
            this.entity = entity;
        }
        ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(this.ID, this.relationshipID, this.schemaID, this.entity.getID(), this.cardinality, this.portAtRelationship, this.portAtEntity, 0, this.gmtCreate, new Date()));
    }

    public void updatePorts(Integer portAtRelationship, Integer portAtEntity) throws ERException {
        if (portAtRelationship != null) {
            this.portAtRelationship = portAtRelationship;
        }
        if (portAtEntity != null) {
            this.portAtEntity = portAtEntity;
        }
        ER.relationshipEdgeMapper.updateByID(new RelationshipEdgeDO(this.ID, this.relationshipID, this.schemaID, this.entity.getID(), this.cardinality, this.portAtRelationship, this.portAtEntity, 0, this.gmtCreate, new Date()));
    }

    public static List<RelationshipEdge> query(RelationshipEdgeDO relationshipEdgeDO) {
        return Trans.TransRelationshipEdgeListFromDB(ER.relationshipEdgeMapper.selectByRelationshipEdge(relationshipEdgeDO));
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