package com.ic.er.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ic.er.ER;
import com.ic.er.Entity;
import com.ic.er.Relationship;
import com.ic.er.Schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchemaDeserializer extends StdDeserializer<Schema> {

    public SchemaDeserializer() {
        this(null);
    }

    public SchemaDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Schema deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Map<String, Entity> entityNameMap = new HashMap<>();

        JsonNode schemaJSONNode = jp.getCodec().readTree(jp);
        String schemaName = schemaJSONNode.get("name").textValue();
        Schema schema = ER.createSchema(schemaName, "");
        for (JsonNode entityJSONNode : schemaJSONNode.get("entityList")) {
            String entityName = entityJSONNode.get("name").textValue();
            Entity entity = schema.addEntity(entityName);
            for (JsonNode attributeJSONNode : entityJSONNode.get("attributeList")) {
                String attributeName = attributeJSONNode.get("name").textValue();
                DataType attributeDataType = DataType.valueOf(attributeJSONNode.get("dataType").textValue());
                Boolean attributeIsPrimary = attributeJSONNode.get("isPrimary").booleanValue();
                Boolean attributeNullable = attributeJSONNode.get("nullable").booleanValue();
                entity.addAttribute(attributeName, attributeDataType, attributeIsPrimary, attributeNullable);
                entityNameMap.put(entityName, entity);
            }
            JsonNode layoutInfoJSONNode = entityJSONNode.get("layoutInfo");
            entity.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble(), 0.0, 0.0);
        }

        for (JsonNode relationshipJSONNode : schemaJSONNode.get("relationshipList")) {
            String relationshipName = relationshipJSONNode.get("name").textValue();
            String firstEntityName = relationshipJSONNode.get("firstEntity").textValue();
            String secondEntityName = relationshipJSONNode.get("secondEntity").textValue();
            String firstCardinality = relationshipJSONNode.get("firstCardinality").textValue();
            String secondCardinality = relationshipJSONNode.get("secondCardinality").textValue();
            Relationship relationship = schema.createRelationship(relationshipName, entityNameMap.get(firstEntityName), entityNameMap.get(secondEntityName), Cardinality.getFromValue(firstCardinality), Cardinality.getFromValue(secondCardinality));
            JsonNode layoutInfoJSONNode = relationshipJSONNode.get("layoutInfo");
            relationship.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble(), 0.0, 0.0);
        }

        return schema;
    }
}