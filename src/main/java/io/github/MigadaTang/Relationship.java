package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.dao.RelationshipDAO;
import io.github.MigadaTang.entity.AttributeDO;
import io.github.MigadaTang.entity.RelationshipDO;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.Date;
import java.util.List;


/**
 * The relationship in ER schema
 */
@Getter
public class Relationship extends ERBaseObj implements ERConnectableObj {
    /**
     * The list of attributes on this relationship
     */
    private List<Attribute> attributeList;
    /**
     * The list of edges connecting this relationship and other relationship or entities
     */
    private List<RelationshipEdge> edgeList;
    /**
     * The list of edges connecting this relationship and other relationship or entities
     */
    private boolean isReflexive;

    protected Relationship(Long ID, String name, Long schemaID, boolean isReflexive, List<Attribute> attributeList, List<RelationshipEdge> edgeList, LayoutInfo layoutInfo, Date gmtCreate, Date gmtModified) {
        super(ID, schemaID, name, BelongObjType.RELATIONSHIP, layoutInfo, gmtCreate, gmtModified);
        this.attributeList = attributeList;
        this.edgeList = edgeList;
        this.isReflexive = isReflexive;
        if (getID() == 0) {
            setID(insertDB());
        }
    }

    public void setReflexive(boolean isReflexive) {
        this.isReflexive = isReflexive;
    }

    private Long insertDB() {
        try {
            RelationshipDO relationshipDO = new RelationshipDO(0L, getName(), getSchemaID(), 0, getGmtCreate(), getGmtModified());
            int ret = RelationshipDAO.insert(relationshipDO);
            if (ret == 0) {
                throw new ERException("relationship insertDB fail");
            }
            return relationshipDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("relationship insertDB fail", e);
        }
    }

    /**
     * Query the list of entities that have the same data specified by entityDO
     *
     * @param relationshipDO The values of some attributes of an entity
     * @return a list of relationships
     */
    public static List<Relationship> query(RelationshipDO relationshipDO) {
        return query(relationshipDO, true);
    }

    /**
     * Query the list of entities that have the same data specified by entityDO
     *
     * @param relationshipDO The values of some attributes of a relationship
     * @param exhaustive     Whether to fetch the related entities and relationships
     * @return a list of relationships
     */
    public static List<Relationship> query(RelationshipDO relationshipDO, boolean exhaustive) {
        return ObjConv.ConvRelationshipListFromDB(RelationshipDAO.selectByRelationship(relationshipDO), exhaustive);
    }

    /**
     * Query the list of entities that have the same data specified by entityDO
     *
     * @param ID the ID of the relationship
     * @return the corresponding relationship
     * @throws ERException throws ERException if no relationship is found
     */
    public static Relationship queryByID(Long ID) throws ERException {
        return queryByID(ID, true);
    }

    /**
     * Query the list of entities that have the same data specified by entityDO
     *
     * @param ID         the ID of the relationship
     * @param exhaustive Whether to fetch the related entities and relationships
     * @return the corresponding relationship
     * @throws ERException throws ERException if no relationship is found
     */
    public static Relationship queryByID(Long ID, boolean exhaustive) throws ERException {
        List<Relationship> relationships = query(new RelationshipDO(ID), exhaustive);
        if (relationships.size() == 0) {
            throw new ERException(String.format("Relationship with ID: %d not found ", ID));
        } else {
            return relationships.get(0);
        }
    }

    /**
     * Delete the current relationship from the database and cascade delete all the attributes and edges in this schema
     */
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
        RelationshipDAO.deleteByID(getID());
    }

    /**
     * Delete the target edge from both the list of edges and the database
     *
     * @param edge the target edge
     */
    public void deleteEdge(RelationshipEdge edge) {
        edge.deleteDB();
        this.getEdgeList().remove(edge);
    }

    /**
     * Add an attribute to the relationship
     *
     * @param attributeName the name of the attribute
     * @param dataType      the type of data this attribute contains
     * @param attributeType the type of this attribute
     * @return the created attribute
     */
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

    /**
     * Delete the target attribute from both the list of edges and the database
     *
     * @param attribute The target attribute
     */
    public void deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        attribute.deleteDB();
    }

    /**
     * Link this relationship to other relationship or entities
     *
     * @param belongObj   the target object
     * @param cardinality the cardinality of the target object
     * @return the created edge connecting two objects
     */
    public RelationshipEdge linkObj(ERConnectableObj belongObj, Cardinality cardinality) {
        return linkObj(belongObj, cardinality, false);
    }

    /**
     * Link this relationship to other relationship or entities
     *
     * @param belongObj   the target object
     * @param cardinality the cardinality of the target object
     * @param isKey       whether this a key relationship for a weak entity
     * @return the created edge connecting two objects
     */
    public RelationshipEdge linkObj(ERConnectableObj belongObj, Cardinality cardinality, Boolean isKey) {
//        System.out.println("belongObj: " + belongObj);
        if (belongObj instanceof Entity) {
            if (Entity.queryByID(belongObj.getID()) == null) {
                throw new ERException(String.format("entity with ID: %d not found", belongObj.getID()));
            }
        } else if (belongObj instanceof Relationship) {
            if (Relationship.queryByID(belongObj.getID()) == null) {
                throw new ERException(String.format("relationship with ID: %d not found", belongObj.getID()));
            }
        } else {
            throw new ERException("unsupported belong obj");
        }
        if (!belongObj.getSchemaID().equals(getSchemaID())) {
            throw new ERException(String.format("entity: %s does not belong to this schema", belongObj.getName()));
        }
//        List<RelationshipEdge> relationshipEdges = RelationshipEdge.query(new RelationshipEdgeDO(getID(), belongObj));
//        if (relationshipEdges.size() != 0) {
//            throw new ERException(String.format("relationship edge already exists, ID: %d", relationshipEdges.get(0).getID()));
//        }
        List<RelationshipEdge> relationshipEdges = RelationshipEdge.query(new RelationshipEdgeDO(getID(), belongObj));
//        RelationshipEdge edge = null;
//        if (relationshipEdges.size() == 0) {
//            edge = new RelationshipEdge(0L, getID(), getSchemaID(), belongObj, cardinality, isKey, -1, -1, new Date(), new Date());
//            this.edgeList.add(edge);
//        }
        if (relationshipEdges.size() != 0) {
            if (!isReflexive) {
                return null; ///
//                throw new ERException(String.format("relationship edge already exists, ID: %d", relationshipEdges.get(0).getID()));
            } else {
                if (relationshipEdges.size() == 1) {
                    RelationshipEdge edge = new RelationshipEdge(0L, getID(), getSchemaID(), belongObj, cardinality, isKey, -1, -1, new Date(), new Date());
                    this.edgeList.add(edge);
                    return edge;
                } else {
                    throw new ERException(String.format("reflexive relationship already contains two edges, name: %s", getName()));
                }
            }

        }
        RelationshipEdge edge = new RelationshipEdge(0L, getID(), getSchemaID(), belongObj, cardinality, isKey, -1, -1, new Date(), new Date());
        this.edgeList.add(edge);
//        // todo: reflexive的情况会和其冲突 reflexive一定是连自己两次 而正常则不需要 可能需要加一个属性去判断到底是怎么样的 这样前端也方便判断
        return edge;
    }

    /**
     * Update the information of a relationship
     *
     * @param name the new name of this relationship
     */
    public void updateInfo(String name) {
        if (name != null) {
            setName(name);
        }
        RelationshipDAO.updateByID(new RelationshipDO(getID(), getName(), getSchemaID(), 0, getGmtCreate(), new Date()));
    }
}