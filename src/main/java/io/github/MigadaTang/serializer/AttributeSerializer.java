package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.Attribute;

import java.io.IOException;

public class AttributeSerializer extends StdSerializer<Attribute> {

    public AttributeSerializer() {
        this(null);
    }

    public AttributeSerializer(Class<Attribute> t) {
        super(t);
    }

    @Override
    public void serialize(
            Attribute attribute, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", attribute.getName());
        jgen.writeStringField("dataType", attribute.getDataType().toString());
        jgen.writeBooleanField("isPrimary", attribute.getIsPrimary());
        jgen.writeBooleanField("nullable", attribute.getNullable());
        if (attribute.getAimPort() != -1) {
            jgen.writeNumberField("aimPort", attribute.getAimPort());
        }
        if (attribute.getLayoutInfo() != null) {
            jgen.writeObjectField("layoutInfo", attribute.getLayoutInfo());
        }
        jgen.writeEndObject();
    }
}