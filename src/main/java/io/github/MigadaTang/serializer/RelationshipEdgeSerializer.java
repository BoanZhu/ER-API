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
            jgen.writeNumberField("ID", edge.getID());
            jgen.writeNumberField("relationshipID", edge.getRelationshipID());
            jgen.writeNumberField("entityID", edge.getEntity().getID());
        } else {
            jgen.writeStringField("entity", edge.getEntity().getName());
        }

        jgen.writeStringField("cardinality", edge.getCardinality().getValue());
        if (edge.getPortAtRelationship() != -1) {
            jgen.writeNumberField("portAtRelationship", edge.getPortAtRelationship());
        } else if (isRenderFormat) {
            jgen.writeNumberField("portAtRelationship", 1);
        }
        if (edge.getPortAtEntity() != -1) {
            jgen.writeNumberField("portAtEntity", edge.getPortAtEntity());
        } else if (isRenderFormat) {
            jgen.writeNumberField("portAtEntity", 1);
        }

        jgen.writeEndObject();
    }
}