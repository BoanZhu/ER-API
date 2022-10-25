package com.ic.er.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ic.er.ER;
import com.ic.er.Entity;
import com.ic.er.Relationship;
import com.ic.er.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewDeserializer extends StdDeserializer<View> {

    public ViewDeserializer() {
        this(null);
    }

    public ViewDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public View deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Map<String, Entity> entityNameMap = new HashMap<>();

        JsonNode viewJSONNode = jp.getCodec().readTree(jp);
        String viewName = viewJSONNode.get("name").textValue();
        String viewCreator = viewJSONNode.get("creator").textValue();
        View view = ER.createView(viewName, viewCreator);
        for (JsonNode entityJSONNode : viewJSONNode.get("entityList")) {
            String entityName = entityJSONNode.get("name").textValue();
            Entity entity = view.addEntity(entityName);
            for (JsonNode attributeJSONNode : entityJSONNode.get("attributeList")) {
                String attributeName = attributeJSONNode.get("name").textValue();
                DataType attributeDataType = DataType.valueOf(attributeJSONNode.get("dataType").textValue());
                Integer attributeIsPrimary = attributeJSONNode.get("isPrimary").intValue();
                Integer attributeIsForeign = attributeJSONNode.get("isForeign").intValue();
                entity.addAttribute(attributeName, attributeDataType, attributeIsPrimary, attributeIsForeign);
                entityNameMap.put(entityName, entity);
            }
            JsonNode layoutInfoJSONNode = entityJSONNode.get("layoutInfo");
            entity.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble(), layoutInfoJSONNode.get("height").asDouble(), layoutInfoJSONNode.get("width").asDouble());
        }

        for (JsonNode relationshipJSONNode : viewJSONNode.get("relationshipList")) {
            String relationshipName = relationshipJSONNode.get("name").textValue();
            String firstEntityName = relationshipJSONNode.get("firstEntity").textValue();
            String secondEntityName = relationshipJSONNode.get("secondEntity").textValue();
            String cardinality = relationshipJSONNode.get("cardinality").textValue();
            Relationship relationship = view.createRelationship(relationshipName, entityNameMap.get(firstEntityName), entityNameMap.get(secondEntityName), Cardinality.valueOf(cardinality));
            JsonNode layoutInfoJSONNode = relationshipJSONNode.get("layoutInfo");
            relationship.updateLayoutInfo(layoutInfoJSONNode.get("layoutX").asDouble(), layoutInfoJSONNode.get("layoutY").asDouble(), layoutInfoJSONNode.get("height").asDouble(), layoutInfoJSONNode.get("width").asDouble());
        }

        return view;
    }
}