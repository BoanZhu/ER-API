package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ic.er.common.Cardinality;
import com.ic.er.common.EntityType;
import com.ic.er.common.EntityWithCardinality;
import com.ic.er.common.SchemaDeserializer;
import com.ic.er.entity.EntityDO;
import com.ic.er.entity.SchemaDO;
import com.ic.er.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@JsonDeserialize(using = SchemaDeserializer.class)
@JsonIgnoreProperties({"id", "creator", "gmtCreate", "gmtModified"})
public class Schema {
    private Long ID;
    private String name;
    private List<Entity> entityList;
    private List<Relationship> relationshipList;
    private String creator;
    private Date gmtCreate;
    private Date gmtModified;

    protected Schema(Long ID, String name, List<Entity> entityList, List<Relationship> relationshipList, String creator, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.entityList = entityList;
        this.relationshipList = relationshipList;
        this.creator = creator;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            insertDB();
        }
    }

    // addEntity add strong entity by default
    public Entity addEntity(String entityName) {
        return addEntity(entityName, EntityType.STRONG, null);
    }

    public Entity addSubset(String entityName, Entity strongEntity) {
        // check if the specified strong entity that this subset relies on exists
        try {
            Entity entity = Entity.queryByID(strongEntity.getID());
        } catch (ERException erex) {
            throw new ERException("addSubset fail: the specified strong entity does not exist");
        }
        return addEntity(entityName, EntityType.SUBSET, strongEntity.getID());
    }

    public Entity addWeakEntity(String entityName, Entity strongEntity, String relationshipName, Entity weakEntityCardinality, Entity strongEntityCardinality) {
        // todo
        // check if the specified strong entity that this subset relies on exists
        // add weak entity
        // add relationship
        return addEntity(entityName, EntityType.STRONG, null);
    }

    // addEntity base method for addEntity, for internal use only,
    // users should add entity through other public methods
    private Entity addEntity(String entityName, EntityType entityType, Long belongStrongEntityID) {
        if (entityName.equals("")) {
            throw new ERException("entityName cannot be empty");
        }
        List<Entity> entities = Entity.query(new EntityDO(entityName, this.ID, null));
        if (entities.size() != 0) {
            throw new ERException(String.format("entity with name: %s already exists", entityName));
        }
        Entity entity = new Entity(0L, entityName, this.ID, entityType, belongStrongEntityID, new ArrayList<>(), Integer.valueOf(-1), null, new Date(), new Date());
        this.entityList.add(entity);
        return entity;
    }

    public void deleteEntity(Entity entity) {
        this.entityList.remove(entity);
//        List<Relationship> relationships = Relationship.queryByRelationship(new RelationshipDO(null, null, this.ID, entity.getID(), null, null, null, null, null, null));
//        for (Relationship relationship : relationships) {
//            deleteRelationship(relationship);
//        }
//        relationships = Relationship.queryByRelationship(new RelationshipDO(null, null, this.ID, null, entity.getID(), null, null, null, null, null));
//        for (Relationship relationship : relationships) {
//            deleteRelationship(relationship);
//        }
        entity.deleteDB();
    }

    public Relationship createRelationship(String relationshipName, Entity firstEntity, Entity secondEntity, Cardinality firstCardinality, Cardinality secondCardinality) {
        ArrayList<EntityWithCardinality> entityWithCardinalityList = new ArrayList<>();
        entityWithCardinalityList.add(new EntityWithCardinality(firstEntity, firstCardinality));
        entityWithCardinalityList.add(new EntityWithCardinality(secondEntity, secondCardinality));
        return createNaryRelationship(relationshipName, entityWithCardinalityList);
    }

    // createNaryRelationship
    public Relationship createNaryRelationship(String relationshipName, List<EntityWithCardinality> entityWithCardinalityList) {
        if (relationshipName.equals("")) {
            throw new ERException("relationshipName cannot be empty");
        }
        if (entityWithCardinalityList.size() <= 1) {
            throw new ERException("must have more than 2 entities to create relationship");
        }
        for (EntityWithCardinality eCard : entityWithCardinalityList) {
            Entity entity = eCard.getEntity();
            if (Entity.queryByID(entity.getID()) == null) {
                throw new ERException(String.format("entity with ID: %d not found", entity.getID()));
            }
            if (!entity.getSchemaID().equals(this.ID)) {
                throw new ERException(String.format("entity: %s does not belong to this schema", entity.getName()));
            }
            // todo check if there is already a relationship between all these entities
        }
        Relationship relationship = new Relationship(0L, relationshipName, this.ID, new ArrayList<>(), new ArrayList<>(), null, new Date(), new Date());
        for (EntityWithCardinality eCard : entityWithCardinalityList) {
            Entity entity = eCard.getEntity();
            Cardinality cardinality = eCard.getCardinality();
            RelationshipEdge relationshipEdge = new RelationshipEdge(0L, relationship.getID(), this.ID, entity, cardinality, -1, -1, new Date(), new Date());
            relationship.getEdgeList().add(relationshipEdge);
        }
        this.relationshipList.add(relationship);
        return relationship;
    }

    public void deleteRelationship(Relationship relationship) {
        this.relationshipList.remove(relationship);
        relationship.deleteDB();
    }

    private void insertDB() {
        try {
            SchemaDO schemaDO = new SchemaDO(0L, this.name, this.creator, 0L, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.schemaMapper.insert(schemaDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = schemaDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    public String toJSON() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public static List<Schema> queryAll() {
        return Trans.TransSchemaListFromDB(ER.schemaMapper.selectAll());
    }

    public static List<Schema> queryBySchema(SchemaDO SchemaDO) {
        List<SchemaDO> schemaDOList = ER.schemaMapper.selectBySchema(SchemaDO);
        return Trans.TransSchemaListFromDB(schemaDOList);
    }

    public static Schema queryByID(Long ID) {
        List<Schema> schemaDOList = queryBySchema(new SchemaDO(ID));
        if (schemaDOList.size() == 0) {
            throw new ERException(String.format("Schema with ID: %d not found", ID));
        } else {
            return schemaDOList.get(0);
        }
    }

    protected void deleteDB() {
        // cascade delete the entities and relationships in this schema
        for (Entity entity : entityList) {
            entity.deleteDB();
        }
        for (Relationship relationship : relationshipList) {
            relationship.deleteDB();
        }
        ER.schemaMapper.deleteByID(this.ID);
    }

    public void updateInfo(String name) {
        if (name != null) {
            this.name = name;
        }
        ER.schemaMapper.updateByID(new SchemaDO(this.ID, this.name, this.creator, 0L, 0, this.gmtCreate, new Date()));
    }
}