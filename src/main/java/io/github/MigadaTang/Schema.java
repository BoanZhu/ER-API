package io.github.MigadaTang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.ConnObjWithCardinality;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.dao.SchemaDAO;
import io.github.MigadaTang.entity.EntityDO;
import io.github.MigadaTang.entity.RelationshipEdgeDO;
import io.github.MigadaTang.entity.SchemaDO;
import io.github.MigadaTang.exception.ERException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.serializer.*;
import io.github.MigadaTang.transform.Column;
import io.github.MigadaTang.transform.GenerationSqlUtil;
import io.github.MigadaTang.transform.ParserUtil;
import io.github.MigadaTang.transform.Table;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.ibatis.exceptions.PersistenceException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The ER schema to which entities, attributes and relationships can be added
 */
@Getter
@JsonDeserialize(using = SchemaDeserializer.class)
public class Schema {
    /**
     * The ID of this schema
     */
    private Long ID;
    /**
     * The name of this schema
     */
    private String name;
    /**
     * The list of entities in this schema
     */
    private List<Entity> entityList;
    /**
     * The list of relationships in this schema
     */
    private List<Relationship> relationshipList;
    private Date gmtCreate;
    private Date gmtModified;

    /**
     * The list of history tables. There are two ways to set this list: the first way is after
     * reverse engineer, this list will be set to the result tables, and all new tables of this schema
     * will based on these old tables so that when generating sql it will compared them. The second way
     * is after executing the new schema created, so that every new modifications can be found.
     */
    private List<Table> oldTables;

    protected Schema(Long ID, String name, List<Entity> entityList, List<Relationship> relationshipList, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.entityList = entityList;
        this.relationshipList = relationshipList;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.oldTables = new ArrayList<>();
        if (this.ID == 0) {
            insertDB();
        }
    }

//    public void setName(String name) {
//        this.name = name;
//        this.updateInfo();
//    }

    //

    /**
     * Add strong entity by default
     *
     * @param entityName name of the entity
     * @return the created entity
     */
    public Entity addEntity(String entityName) {
        return addEntity(entityName, EntityType.STRONG, null);
    }

    /**
     * add entity by name and type
     *
     * @param entityName name of the entity
     * @param entityType type of the entity
     * @return created entity
     */
    public Entity addEntity(String entityName, EntityType entityType) {
        return addEntity(entityName, entityType, null);
    }

    /**
     * add a subset
     *
     * @param subsetName   the name of the subset
     * @param strongEntity the entity of the strong entity to which this subset belongs
     * @return the created entity
     */
    public Entity addSubset(String subsetName, Entity strongEntity) {
        // check if the specified strong entity that this subset relies on exists
        Entity entity;
        try {
            entity = Entity.queryByID(strongEntity.getID());
        } catch (ERException ex) {
            throw new ERException("addSubset fail: the specified strong entity does not exist");
        }
        if (!entity.getSchemaID().equals(this.ID)) {
            throw new ERException("entity does not belong to this schema");
        }
        return addEntity(subsetName, EntityType.SUBSET, strongEntity);
    }

    /**
     * add a generalisation
     *
     * @param generalisationName   the name of the generalisation(subset)
     * @param strongEntity the entity of the strong entity to which this generalisation belongs
     * @return the created entity
     */
    public Entity addGeneralisation(String generalisationName, Entity strongEntity) {
        // check if the specified strong entity that this generalisation relies on exists
        Entity entity;
        try {
            entity = Entity.queryByID(strongEntity.getID());
        } catch (ERException ex) {
            throw new ERException("addGeneralisation fail: the specified strong entity does not exist");
        }
        if (!entity.getSchemaID().equals(this.ID)) {
            throw new ERException("entity does not belong to this schema");
        }
        return addEntity(generalisationName, EntityType.GENERALISATION, strongEntity);
    }

    /**
     * add a weak entity
     *
     * @param entityName              the name of the weak entity
     * @param strongEntity            the strong entity to which this weak entity belongs
     * @param relationshipName        the name of the relationship between the strong and weak entity
     * @param weakEntityCardinality   the cardinality of the weak entity
     * @param strongEntityCardinality the cardinality of the strong entity
     * @return a pair of weak entity and relationship
     */
    public ImmutablePair<Entity, Relationship> addWeakEntity(String entityName, Entity strongEntity, String relationshipName, Cardinality weakEntityCardinality, Cardinality strongEntityCardinality) {
        // check if the specified strong entity that this subset relies on exists
        Entity entity;
        try {
//            System.out.println("123: " + strongEntity.getID());
            entity = Entity.queryByID(strongEntity.getID());
//            System.out.println("entity: " + entity);
        } catch (ERException ex) {
            throw new ERException("addWeakEntity fail: the specified strong entity does not exist");
        }
        // check if the strong entity belongs to this schema
        if (!entity.getSchemaID().equals(this.ID)) {
            throw new ERException("entity does not belong to this schema");
        }
        // add weak entity
        Entity weakEntity = addEntity(entityName, EntityType.WEAK, null);
        // add relationship
        Relationship relationship = createRelationship(relationshipName, weakEntity, strongEntity, weakEntityCardinality, strongEntityCardinality);
        for (RelationshipEdge edge : relationship.getEdgeList()) {
            if (edge.getConnObj().getID().equals(weakEntity.getID())) {
                edge.updateInfo(null, null, null, true);
            }
        }
        return new ImmutablePair<>(weakEntity, relationship);
    }

    /**
     * base method for addEntity, for internal use only, users should add entity through other public methods
     *
     * @param entityName         the name of the entity
     * @param entityType         the type of the entity
     * @param belongStrongEntity the entity that a subset belongs to
     * @return the created entity
     */
    private Entity addEntity(String entityName, EntityType entityType, Entity belongStrongEntity) {
        if (entityName.equals("")) {
            throw new ERException("entityName cannot be empty");
        }
        List<Entity> entities = Entity.query(new EntityDO(entityName, this.ID, null));
        if (entities.size() != 0) {
            throw new ERException(String.format("entity with name: %s already exists", entityName));
        }
        Entity entity = new Entity(0L, entityName, this.ID, entityType, belongStrongEntity, new ArrayList<>(), Integer.valueOf(-1), null, new Date(), new Date());
        this.entityList.add(entity);
        return entity;
    }

    public Entity addEntity(String entityName, Long entityId) {
        return addEntity(entityName, EntityType.STRONG, null, entityId);
    }

    private Entity addEntity(String entityName, EntityType entityType, Entity belongStrongEntity, Long entityId) {
        if (entityName.equals("")) {
            throw new ERException("entityName cannot be empty");
        }
        List<Entity> entities = Entity.query(new EntityDO(entityName, this.ID, null));
        if (entities.size() != 0) {
            throw new ERException(String.format("entity with name: %s already exists", entityName));
        }
        Entity entity = new Entity(entityId, entityName, this.ID, entityType, belongStrongEntity, new ArrayList<>(), Integer.valueOf(-1), null, new Date(), new Date());
        System.out.println("why: " + entity);
        this.entityList.add(entity);
        return entity;
    }

    /**
     * Delete the target entity from both the list of entities and the database
     *
     * @param entity the target entity
     */
    public void deleteEntity(Entity entity) {
        // firstly, delete all the edges connected to this entity
        List<RelationshipEdge> edgeList = RelationshipEdge.query(new RelationshipEdgeDO(null, entity));
        for (RelationshipEdge edge : edgeList) {
            edge.deleteDB();
        }
        entity.deleteDB();
        List<Entity> entities = Entity.query(new EntityDO(null, null, entity.getID()), false);
        for (Entity subset : entities) {
            subset.removeBelongStrongEntity();
        }
        this.entityList.remove(entity);
    }

    /**
     * create an empty relationship
     *
     * @param relationshipName the name of the relationship
     * @return the created relationship
     */
    public Relationship createEmptyRelationship(String relationshipName) {
        Relationship relationship = new Relationship(0L, relationshipName, this.ID, false, new ArrayList<>(), new ArrayList<>(), null, new Date(), new Date());
        this.relationshipList.add(relationship);
        return relationship;
    }

    /**
     * create a relationship connecting two connectable objects
     *
     * @param relationshipName  the name of the relationship
     * @param firstObj          the first connectable object
     * @param secondObj         the second connectable object
     * @param firstCardinality  the cardinality of the first connectable object
     * @param secondCardinality the cardinality of the second connectable object
     * @return a created relationship
     */
    public Relationship createRelationship(String relationshipName, ERConnectableObj firstObj, ERConnectableObj secondObj, Cardinality firstCardinality, Cardinality secondCardinality) {
        ArrayList<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
        connObjWithCardinalityList.add(new ConnObjWithCardinality(firstObj, firstCardinality));
        connObjWithCardinalityList.add(new ConnObjWithCardinality(secondObj, secondCardinality));
        return createNaryRelationship(relationshipName, connObjWithCardinalityList);
    }

    /**
     * create relationship connecting two entities
     *
     * @param relationshipName  the name of the relationship
     * @param firstEntity       the first entity
     * @param secondEntity      the second entity
     * @param firstCardinality  the cardinality of the first entity
     * @param secondCardinality the cardinality of the second entity
     * @return a created relationship
     */
    public Relationship createRelationship(String relationshipName, Entity firstEntity, Entity secondEntity, Cardinality firstCardinality, Cardinality secondCardinality) {
        ArrayList<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
        connObjWithCardinalityList.add(new ConnObjWithCardinality(firstEntity, firstCardinality));
        connObjWithCardinalityList.add(new ConnObjWithCardinality(secondEntity, secondCardinality));
        return createNaryRelationship(relationshipName, connObjWithCardinalityList);
    }

    // createNaryRelationship

    /**
     * create relationship between multiple connectable objects
     *
     * @param relationshipName           the name of the relationship
     * @param connObjWithCardinalityList a list of the connectable objects with their cardinalities
     * @return a created relationship
     */
    public Relationship createNaryRelationship(String relationshipName, List<ConnObjWithCardinality> connObjWithCardinalityList) {
        if (relationshipName.equals("")) {
            throw new ERException("relationshipName cannot be empty");
        }
        if (connObjWithCardinalityList.size() <= 1) {
            throw new ERException("must have at least 2 components to create relationship");
        }
        for (ConnObjWithCardinality eCard : connObjWithCardinalityList) {
            if (eCard.getConnObj() instanceof Entity) {
                ERConnectableObj entity = eCard.getConnObj();
                if (Entity.queryByID(entity.getID()) == null) {
                    throw new ERException(String.format("entity with ID: %d not found", entity.getID()));
                }
                if (!entity.getSchemaID().equals(this.ID)) {
                    throw new ERException(String.format("entity: %s does not belong to this schema", entity.getName()));
                }
            } else if (eCard.getConnObj() instanceof Relationship) {
                ERConnectableObj relationship = eCard.getConnObj();
                if (Relationship.queryByID(relationship.getID(), false) == null) {
                    throw new ERException(String.format("relationship with ID: %d not found", relationship.getID()));
                }
                if (!relationship.getSchemaID().equals(this.ID)) {
                    throw new ERException(String.format("relationship: %s does not belong to this schema", relationship.getName()));
                }
            }
        }
        Relationship relationship = new Relationship(0L, relationshipName, this.ID, false, new ArrayList<>(), new ArrayList<>(), null, new Date(), new Date());
        for (ConnObjWithCardinality eCard : connObjWithCardinalityList) {
            RelationshipEdge relationshipEdge = new RelationshipEdge(0L, relationship.getID(), this.ID, eCard.getConnObj(), eCard.getCardinality(), false, -1, -1, new Date(), new Date());
            relationship.getEdgeList().add(relationshipEdge);
        }
        this.relationshipList.add(relationship);
        return relationship;
    }

    /**
     * Delete the target relationship from both the list of relationships and the database
     *
     * @param relationship the target relationship
     */
    public void deleteRelationship(Relationship relationship) {
        this.relationshipList.remove(relationship);
        relationship.deleteDB();
    }

    private void insertDB() {
        try {
            SchemaDO schemaDO = new SchemaDO(0L, this.name, 0, this.gmtCreate, this.gmtModified);
            int ret = SchemaDAO.insert(schemaDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = schemaDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    /**
     * Simply checks if this ER schema can be rebuilt from scratch
     *
     * @throws ERException throws ERException if the schema cannot be rebuilt
     */
    public void sanityCheck() throws ERException {
        Map<Long, Integer> weakEntityKeyRelationshipCountMap = new HashMap<>();
        for (Entity entity : entityList) {
            int primaryKeyNum = 0;
            for (Attribute attribute : entity.getAttributeList()) {
                if (attribute.getIsPrimary()) {
                    primaryKeyNum += 1;
                }
            }
//            System.out.println("222: " + entity.getEntityType());
            switch (entity.getEntityType()) {
                case WEAK:
//                    if (primaryKeyNum != 0) {
//                        throw new ERException(String.format("weak entity (%s) cannot have primary key", entity.getName()));
//                    }
                    break;
                case STRONG:
//                    if (primaryKeyNum < 1) {
//                        throw new ERException(String.format("strong entity (%s) must have one primary key", entity.getName()));
//                    }
                    break;
                case SUBSET:
//                    if (primaryKeyNum != 0) {
//                        throw new ERException(String.format("subset (%s) cannot have primary key", entity.getName()));
//                    }
//                    if (entity.getBelongStrongEntity() == null || entity.getBelongStrongEntity().getEntityType() != EntityType.STRONG) {
                    if (entity.getBelongStrongEntity() == null || entity.getBelongStrongEntity().getEntityType() != EntityType.STRONG
                        && entity.getBelongStrongEntity().getEntityType() != EntityType.SUBSET) {
                        throw new ERException(String.format("subset (%s) must have a relying on strong entity", entity.getName()));
                    }
                    break;
                default:
                    throw new ERException(String.format("unknown entity type of entity (%s)"));
            }
        }
        for (Relationship relationship : relationshipList) {
            for (Attribute attribute : relationship.getAttributeList()) {
                if (attribute.getIsPrimary()) {
                    throw new ERException(String.format("attribute (%s) of relationship (%s) cannot be primary key", attribute.getName(), relationship.getName()));
                }
            }
//            if (relationship.getEdgeList().size() < 2) {
//                throw new ERException(String.format("relationship (%s) must have more than one edges", relationship.getName()));
//            }
            // check if this is a duplicated relationship in which all the objects have already been connected
//            List<ERConnectableObj> belongObjList = new ArrayList<>();
//            for (RelationshipEdge edge : relationship.getEdgeList()) {
//                belongObjList.add(edge.getConnObj());
//            }
//            if (RelationshipEdge.checkInSameRelationship(relationship.getID(), belongObjList)) {
//                throw new ERException(String.format("duplicated relationship: %s, the same set of entities have already been connected", relationship.getName()));
//            }
            for (RelationshipEdge edge : relationship.getEdgeList()) {
                if (edge.getIsKey()) {
                    // key relationship can only be used by weak entity
                    boolean isWeakEntity = false;
                    if (edge.getConnObjType() == BelongObjType.ENTITY) {
                        Entity entity = Entity.queryByID(edge.getConnObj().getID());
                        if (entity.getEntityType() == EntityType.WEAK) {
                            isWeakEntity = true;
                            Integer previous = weakEntityKeyRelationshipCountMap.getOrDefault(entity.getID(), 0);
                            weakEntityKeyRelationshipCountMap.put(entity.getID(), previous + 1);
                        }
                    }
                    if (!isWeakEntity) {
                        throw new ERException(String.format("key relationship can only be used by weak entity, while (%s) is not", edge.getConnObj().getName()));
                    }
                    if (edge.getCardinality() != Cardinality.OneToOne) {
                        throw new ERException(String.format("key relationship of (%s) must have 1:1 cardinality", edge.getConnObj().getName()));
                    }
                }
            }
        }

        for (Entity entity : entityList) {
            if (entity.getEntityType() == EntityType.WEAK) {
                Integer keyRelationshipCount = weakEntityKeyRelationshipCountMap.getOrDefault(entity.getID(), 0);
                if (keyRelationshipCount == 0) {
                    throw new ERException(String.format("weak entity (%s) must have at least one key relationship", entity.getName()));
                }
            }
        }
    }

    /**
     * Comprehensively check the validity before generating DDL
     *
     * @throws ERException throws ERException if DDL cannot be generated from this
     */
    public void comprehensiveCheck() throws ERException {
        Map<Long, Integer> weakEntityKeyRelationshipCountMap = new HashMap<>();
        for (Entity entity : entityList) {
            int primaryKeyNum = 0;
            for (Attribute attribute : entity.getAttributeList()) {
                if (attribute.getIsPrimary()) {
                    primaryKeyNum += 1;
                }
            }
            switch (entity.getEntityType()) {
                case WEAK:
//                    if (primaryKeyNum != 0) {
//                        throw new ERException(String.format("weak entity (%s) cannot have primary key", entity.getName()));
//                    }
                    break;
                case STRONG:
//                    if (primaryKeyNum < 1) {
//                        throw new ERException(String.format("strong entity (%s) must have one primary key", entity.getName()));
//                    }
                    break;
                case SUBSET:
//                    if (primaryKeyNum != 0) {
//                        throw new ERException(String.format("subset (%s) cannot have primary key", entity.getName()));
//                    }
//                    if (entity.getBelongStrongEntity() == null || entity.getBelongStrongEntity().getEntityType() != EntityType.STRONG) {
                    if (entity.getBelongStrongEntity() == null || entity.getBelongStrongEntity().getEntityType() != EntityType.STRONG
                    && entity.getBelongStrongEntity().getEntityType() != EntityType.SUBSET) {
                        throw new ERException(String.format("subset (%s) must have a relying on strong entity", entity.getName()));
                    }
                    break;
                default:
                    throw new ERException(String.format("unknown entity type of entity (%s)", entity.getName()));
            }
        }
        for (Relationship relationship : relationshipList) {
            for (Attribute attribute : relationship.getAttributeList()) {
                if (attribute.getIsPrimary()) {
                    throw new ERException(String.format("attribute (%s) of relationship (%s) cannot be primary key", attribute.getName(), relationship.getName()));
                }
            }
//            if (relationship.getEdgeList().size() < 2) {
//                throw new ERException(String.format("relationship (%s) must have more than one edges", relationship.getName()));
//            }
            // check if this is a duplicated relationship in which all the objects have already been connected
//            List<ERConnectableObj> belongObjList = new ArrayList<>();
//            for (RelationshipEdge edge : relationship.getEdgeList()) {
//                belongObjList.add(edge.getConnObj());
//            }
//            if (RelationshipEdge.checkInSameRelationship(relationship.getID(), belongObjList)) {
//                throw new ERException(String.format("duplicated relationship: %s, the same set of entities have already been connected", relationship.getName()));
//            }
            int keyRelationshipEdgeCount = 0;
            for (RelationshipEdge edge : relationship.getEdgeList()) {
                if (edge.getIsKey()) {
                    keyRelationshipEdgeCount++;
                    // key relationship can only be used by weak entity
                    boolean isWeakEntity = false;
                    if (edge.getConnObjType() == BelongObjType.ENTITY) {
                        Entity entity = Entity.queryByID(edge.getConnObj().getID());
                        if (entity.getEntityType() == EntityType.WEAK) {
                            isWeakEntity = true;
                            Integer previous = weakEntityKeyRelationshipCountMap.getOrDefault(entity.getID(), 0);
                            weakEntityKeyRelationshipCountMap.put(entity.getID(), previous + 1);
                        }
                    }
                    if (!isWeakEntity) {
                        throw new ERException(String.format("key relationship can only be used by weak entity, while (%s) is not", edge.getConnObj().getName()));
                    }
                    if (edge.getCardinality() != Cardinality.OneToOne) {
                        throw new ERException(String.format("key relationship of (%s) must have 1:1 cardinality", edge.getConnObj().getName()));
                    }
                }
            }
            if (keyRelationshipEdgeCount >= 2) {
                throw new ERException(String.format("relationship %s cannot have more than one key relationship edges", relationship.getName()));
            }
            if (keyRelationshipEdgeCount == 1) {
                for (RelationshipEdge edge : relationship.getEdgeList()) {
                    if (edge.getIsKey()) {
                        continue;
                    }
                    if (edge.getConnObjType() == BelongObjType.ENTITY) {
                        Entity entity = Entity.queryByID(edge.getConnObj().getID());
                        if (entity.getEntityType() != EntityType.STRONG && entity.getEntityType() != EntityType.WEAK) {
                            throw new ERException(String.format("relationship %s with key relationship edge can only connect to strong entities", relationship.getName()));
                        }
                    } else {
                        throw new ERException(String.format("relationship %s with key relationship edge can only connect to strong entities", relationship.getName()));
                    }
                }
            }
        }

        // check if there are cycles in the schema
        ParserUtil.generateRelationshipTopologySeq(relationshipList);

        for (Entity entity : entityList) {
            if (entity.getEntityType() == EntityType.WEAK) {
                Integer keyRelationshipCount = weakEntityKeyRelationshipCountMap.getOrDefault(entity.getID(), 0);
                if (keyRelationshipCount == 0) {
                    throw new ERException(String.format("weak entity (%s) must have at least one key relationship", entity.getName()));
                }
            }
        }
    }


    /**
     * Transform the current ER schema to json string
     *
     * @return a json string
     */
    public String toJSON() {
        sanityCheck();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Schema.class, new SchemaSerializer(false));
        module.addSerializer(Entity.class, new EntitySerializer(false));
        module.addSerializer(Relationship.class, new RelationshipSerializer(false));
        module.addSerializer(RelationshipEdge.class, new RelationshipEdgeSerializer(false));
        module.addSerializer(Attribute.class, new AttributeSerializer(false));
        module.addSerializer(LayoutInfo.class, new LayoutInfoSerializer(false));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * Transform the current ER schema to a json string that can be rendered by html
     *
     * @return a json string that can be rendered by html
     */
    public String toRenderJSON() {
//        sanityCheck();
        System.out.println("====================");
        for (Relationship relationship: relationshipList) {
            System.out.println("relationship name: " + relationship.getName());
            for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
                System.out.println("relationshipEdge name: " + relationshipEdge.getConnObj().getName());
            }
        }
        SimpleModule module = new SimpleModule();
        module.addSerializer(Schema.class, new SchemaSerializer(true));
        module.addSerializer(Entity.class, new EntitySerializer(true));
        module.addSerializer(Relationship.class, new RelationshipSerializer(true));
        module.addSerializer(RelationshipEdge.class, new RelationshipEdgeSerializer(true));
        module.addSerializer(Attribute.class, new AttributeSerializer(true));
        module.addSerializer(LayoutInfo.class, new LayoutInfoSerializer(true));


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }


    /**
     * Returns all the schemas in the database exhaustively
     *
     * @return A list of schemas
     */
    public static List<Schema> queryAll() {
        return ObjConv.ConvSchemaListFromDB(SchemaDAO.selectAll(), true);
    }

    /**
     * Returns all the schemas in the database
     *
     * @param exhaustive whether to fetch the entities and relationships in a schema
     * @return A list of schemas
     */
    public static List<Schema> queryAll(boolean exhaustive) {
        return ObjConv.ConvSchemaListFromDB(SchemaDAO.selectAll(), exhaustive);
    }

    /**
     * Query the list of schemas that have the same data specified by schemaDO exhaustively
     *
     * @param schemaDO the values of some attributes of a schema
     * @return a list of schemas
     */
    public static List<Schema> queryBySchema(SchemaDO schemaDO) {
        List<SchemaDO> schemaDOList = SchemaDAO.selectBySchema(schemaDO);
        return ObjConv.ConvSchemaListFromDB(schemaDOList, true);
    }

    /**
     * Query schema by ID exhaustively
     *
     * @param ID the ID of the schema
     * @return the found schema
     */
    public static Schema queryByID(Long ID) {
        List<Schema> schemaDOList = queryBySchema(new SchemaDO(ID));
        if (schemaDOList.size() == 0) {
            throw new ERException(String.format("Schema with ID: %d not found", ID));
        } else {
            return schemaDOList.get(0);
        }
    }

    /**
     * Delete the current schema from the database and cascade delete all the components in this schema
     */
    protected void deleteDB() {
        // cascade delete the entities and relationships in this schema
        for (Entity entity : entityList) {
            entity.deleteDB();
        }
        for (Relationship relationship : relationshipList) {
            relationship.deleteDB();
        }
        SchemaDAO.deleteByID(this.ID);
    }

    /**
     * Update the information of a schema
     *
     * @param name the new name of this relationship
     */
    public void updateInfo(String name) {
        if (name != null) {
            this.name = name;
        }
        SchemaDAO.updateByID(new SchemaDO(this.ID, this.name, 0, this.gmtCreate, new Date()));
    }

    private final static String templateHTMLPath = "render/template.html";
    private final static String renderHTMLPath = "render.html";

    public String renderAsImage(String fileName) throws ParseException {
        try {
            writeRenderHTML(toRenderJSON());
        } catch (IOException e) {
            throw new ParseException("Fail to write the json string to file: " + e.getMessage());
        }
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setDownloadImages(true);
        HtmlPage myPage = null;
        try {
            myPage = webClient.getPage(new File(renderHTMLPath).toURI().toURL());
        } catch (IOException e) {
            throw new ParseException("Fail to read the file: " + renderHTMLPath);
        }
        String baseImageCode = myPage.getElementById("image").getTextContent();
        webClient.close();

        if (fileName != null) {
            decodeImageCodeToPNG(baseImageCode, fileName);
        }
        return baseImageCode;
    }

    public void writeRenderHTML(String jsonString) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templateHTMLPath);
        if (inputStream == null) {
            throw new ERException("templateHTML not found");
        }
        String allContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        allContent = allContent.replace("##", "" + "{\"schema\":" + jsonString + "}");
        File writeFile = new File(renderHTMLPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile))) {
            writer.append(allContent);
        }
    }

    private void decodeImageCodeToPNG(String baseImageCode, String filename) {
        if (filename.equals("")) {
            filename = this.name;
        }
        filename += ".png";
        String base64Data = baseImageCode.split(",")[1];
        try (FileOutputStream imageOutFile = new FileOutputStream(filename)) {
            byte[] imageByteArray = Base64.getDecoder().decode(base64Data.getBytes(StandardCharsets.UTF_8));
            imageOutFile.write(imageByteArray);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
    }


    /**
     * Transform er model to data definition language
     *
     * @return -  Sql Statement of current schema
     * @throws ParseException Exception that fail to mapping entity, relationship and attribute to table and column
     */
    public String generateSqlStatement() throws ERException, ParseException {
//        comprehensiveCheck();
        Map<Long, Table> tableDTOList;
//        tableDTOList = ParserUtil.parseRelationshipsToAttribute(this.getEntityList(), this.getRelationshipList());
        tableDTOList = ParserUtil.parseRelationshipsToAttribute(this.getEntityList(), this.getRelationshipList());
//        String sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);

        for (Entity entity: this.getEntityList()) {
            System.out.println("entity: " + entity.getName() + entity.getID());
        }
        for (Relationship relationship: this.getRelationshipList()) {
            System.out.println("relationship: " + relationship.getName() + relationship.getID());
        }

        for (Table table: tableDTOList.values()) {
            System.out.println("after generateSqlStatement: " + table.getName() + ", " + table.getId());
            for (Column column: table.getColumnList()) {
                System.out.println("    " + column.getName() + ", " + column.getDataType());
            }
        }

        String sqlStatement;
        if (this.oldTables.size() == 0) {
            sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);
        } else {
            sqlStatement = GenerationSqlUtil.generateSqlStatements(tableDTOList, this.oldTables);
        }

        // Here we need to reset the oldTables.
        this.oldTables = new ArrayList<>();
        this.oldTables.addAll(tableDTOList.values());

        return sqlStatement;
    }

    public void setOldTables(List<Table> oldTables) {
        this.oldTables = oldTables;
    }
}