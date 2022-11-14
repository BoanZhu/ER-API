package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.RelationshipEdge;

import java.io.IOException;

public class RelationshipEdgeSerializer extends StdSerializer<RelationshipEdge> {

    public RelationshipEdgeSerializer() {
        this(null);
    }

    public RelationshipEdgeSerializer(Class<RelationshipEdge> t) {
        super(t);
    }

    @Override
    public void serialize(
            RelationshipEdge edge, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("entity", edge.getEntity().getName());
        jgen.writeStringField("cardinality", edge.getCardinality().getValue());
        if (edge.getPortAtRelationship() != -1) {
            jgen.writeNumberField("portAtRelationship", edge.getPortAtRelationship());
        }
        if (edge.getPortAtEntity() != -1) {
            jgen.writeNumberField("portAtEntity", edge.getPortAtEntity());
        }
        jgen.writeEndObject();
    }
}