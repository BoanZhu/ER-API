package io.github.MigadaTang;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.common.EntityWithCardinality;
import io.github.MigadaTang.exception.ERException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchemaDeserializer extends StdDeserializer<Schema> {

    private Map<String, Entity> entityNameMap = new HashMap<>();


    public SchemaDeserializer() {
        this(null);
    }

    public SchemaDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Schema deserialize(JsonParser jp, DeserializationContext ctxt)
            throws ERException, IOException {

        JsonNode schemaJSONNode = jp.getCodec().readTree(jp);
        String schemaName = schemaJSONNode.get("name").textValue();
        Schema schema = ER.createSchema(schemaName, "");

        parseEntityList(schema, schemaJSONNode.get("entityList"));
        parseRelationshipList(schema, schemaJSONNode.get("relationshipList"));

        return schema;
    }

    private void parseEntityList(Schema schema, JsonNode entityList) {
        if (entityList == null) {
            return;
        }
        // first round of iteration: create all strong entities, because subset and weak entities rely on these strong entities
        for (JsonNode entityJSONNode : entityList) {
            parseEntityNodeWithEntityType(schema, entityJSONNode, EntityType.STRONG);
        }
        // second round create subset and weak entities
        for (JsonNode entityJSONNode : entityList) {
            parseEntityNodeWithEntityType(schema, entityJSONNode, EntityType.WEAK, EntityType.SUBSET);
        }
    }

    private void parseEntityNodeWithEntityType(Schema schema, JsonNode entityJSONNode, EntityType... types) {
        String entityName = entityJSONNode.get("name").textValue();
        EntityType entityType = EntityType.getFromValue(entityJSONNode.get("entityType").textValue());
        JsonNode belongStrongEntity = entityJSONNode.get("belongStrongEntity");
        String strongEntityName = null;
        if (belongStrongEntity != null) {
            strongEntityName = belongStrongEntity.textValue();
        }
        Entity entity = null;
        for (EntityType type : types) {
            if (type == entityType) {
                switch (entityType) {
                    case STRONG: {
                        entity = schema.addEntity(entityName);
                        break;
                    }
                    case WEAK: {
                        entity = schema.addIsolatedWeakEntity(entityName, entityNameMap.get(strongEntityName));
                        break;
                    }
                    case SUBSET: {
                        entity = schema.addSubset(entityName, entityNameMap.get(strongEntityName));
                        break;
                    }
                    default: {
                        throw new ERException(String.format("deserialize schema fail: fail to create entity: %s", entityName));
                    }
                }
            }
        }
        if (entity == null) {
            return;
        }
        if (entityJSONNode.get("attributeList") != null) {
            for (JsonNode attributeJSONNode : entityJSONNode.get("attributeList")) {
                String attributeName = attributeJSONNode.get("name").textValue();
                DataType attributeDataType = DataType.getFromValue(attributeJSONNode.get("dataType").textValue());
                Boolean attributeIsPrimary = attributeJSONNode.get("isPrimary").booleanValue();
                Boolean attributeNullable = attributeJSONNode.get("nullable").booleanValue();
                Attribute attribute = entity.addAttribute(attributeName, attributeDataType, attributeIsPrimary, attributeNullable);
                JsonNode aimPortNode = attributeJSONNode.get("aimPort");
                if (aimPortNode != null) {
                    attribute.updateAimPort(aimPortNode.intValue());
                }
                JsonNode layoutInfoJSONNode = entityJSONNode.get("layoutInfo");
                if (layoutInfoJSONNode != null) {
                    attribute.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble());
                }
            }
            JsonNode aimPortNode = entityJSONNode.get("aimPort");
            if (aimPortNode != null) {
                entity.updateAimPort(aimPortNode.intValue());
            }
            JsonNode layoutInfoJSONNode = entityJSONNode.get("layoutInfo");
            if (layoutInfoJSONNode != null) {
                entity.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble());
            }
        }
        entityNameMap.put(entityName, entity);
    }

    private void parseRelationshipList(Schema schema, JsonNode relationshipList) {
        if (relationshipList == null) {
            return;
        }
        for (JsonNode relationshipJSONNode : relationshipList) {
            String relationshipName = relationshipJSONNode.get("name").textValue();
            JsonNode edgeList = relationshipJSONNode.get("edgeList");
            if (edgeList == null || edgeList.size() == 0) {
                throw new ERException(String.format("deserialize schema fail edgeList of relationship: %s cannot be empty", relationshipName));
            }
            ArrayList<EntityWithCardinality> eCardList = new ArrayList<>();
            for (JsonNode edgeJsonNode : edgeList) {
                String entityName = edgeJsonNode.get("entity").textValue();
                Cardinality cardinality = Cardinality.getFromValue(edgeJsonNode.get("cardinality").textValue());
                Entity entity = entityNameMap.get(entityName);
                eCardList.add(new EntityWithCardinality(entity, cardinality));
            }
            Relationship relationship = schema.createNaryRelationship(relationshipName, eCardList);

            JsonNode attributeList = relationshipJSONNode.get("attributeList");
            if (attributeList != null) {
                for (JsonNode attributeJSONNode : attributeList) {
                    String attributeName = attributeJSONNode.get("name").textValue();
                    DataType attributeDataType = DataType.valueOf(attributeJSONNode.get("dataType").textValue());
                    Boolean attributeNullable = attributeJSONNode.get("nullable").booleanValue();
                    Attribute attribute = relationship.addAttribute(attributeName, attributeDataType, attributeNullable);
                    JsonNode aimPortNode = attributeJSONNode.get("aimPort");
                    if (aimPortNode != null) {
                        attribute.updateAimPort(aimPortNode.intValue());
                    }
                    JsonNode layoutInfoJSONNode = attributeJSONNode.get("layoutInfo");
                    if (layoutInfoJSONNode != null) {
                        attribute.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble());
                    }
                }
            }
            JsonNode layoutInfoJSONNode = relationshipJSONNode.get("layoutInfo");
            if (layoutInfoJSONNode != null) {
                relationship.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble());
            }
        }
    }
}