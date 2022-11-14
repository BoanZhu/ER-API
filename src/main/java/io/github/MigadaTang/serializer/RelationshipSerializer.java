package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.Attribute;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.RelationshipEdge;

import java.io.IOException;
import java.util.List;

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
        List<Attribute> attributeList = relationship.getAttributeList();
        if (attributeList != null && attributeList.size() != 0) {
            jgen.writeObjectField("attributeList", attributeList);
        }
        List<RelationshipEdge> edgeList = relationship.getEdgeList();
        if (edgeList != null && edgeList.size() != 0) {
            jgen.writeObjectField("edgeList", edgeList);
        }
        if (relationship.getLayoutInfo() != null) {
            jgen.writeObjectField("layoutInfo", relationship.getLayoutInfo());
        }
        jgen.writeEndObject();
    }
}