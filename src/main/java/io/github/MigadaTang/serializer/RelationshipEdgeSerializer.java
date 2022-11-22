package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.RelationshipEdge;

import java.io.IOException;

public class RelationshipEdgeSerializer extends JsonSerializer<RelationshipEdge> {
    private boolean isRenderFormat;

    public RelationshipEdgeSerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(
            RelationshipEdge edge, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();

        if (isRenderFormat) {
            jgen.writeNumberField("id", edge.getID());
            jgen.writeNumberField("relationshipID", edge.getRelationshipID());
            jgen.writeNumberField("entityID", edge.getEntity().getID());
            jgen.writeNumberField("cardinality", edge.getCardinality().getCode());
        } else {
            jgen.writeStringField("entity", edge.getEntity().getName());
            jgen.writeStringField("cardinality", edge.getCardinality().getValue());
        }

        jgen.writeNumberField("portAtRelationship", edge.getPortAtRelationship());
        jgen.writeNumberField("portAtEntity", edge.getPortAtEntity());

        jgen.writeEndObject();
    }
}