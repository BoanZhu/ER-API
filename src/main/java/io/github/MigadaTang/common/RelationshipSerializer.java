package io.github.MigadaTang.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.Relationship;

import java.io.IOException;

public class RelationshipSerializer extends StdSerializer<Relationship> {

    public RelationshipSerializer() {
        this(null);
    }

    public RelationshipSerializer(Class<Relationship> t) {
        super(t);
    }

    @Override
    public void serialize(
            Relationship relationship, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", relationship.getName());
//        jgen.writeStringField("firstEntity", relationship.getFirstEntity().getName());
//        jgen.writeStringField("firstCardinality", relationship.getFirstCardinality().getValue());
//        jgen.writeStringField("secondEntity", relationship.getSecondEntity().getName());
//        jgen.writeStringField("secondCardinality", relationship.getSecondCardinality().getValue());
        jgen.writeObjectField("layoutInfo", relationship.getLayoutInfo());
        jgen.writeEndObject();
    }
}