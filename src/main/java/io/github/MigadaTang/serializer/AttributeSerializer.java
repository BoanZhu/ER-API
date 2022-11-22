package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.MigadaTang.Attribute;

import java.io.IOException;

public class AttributeSerializer extends JsonSerializer<Attribute> {
    private boolean isRenderFormat;

    public AttributeSerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(
            Attribute attribute, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();


        if (isRenderFormat) {
            jgen.writeNumberField("id", attribute.getID());
            jgen.writeStringField("name", attribute.getName());
            jgen.writeNumberField("dataType", attribute.getDataType().ordinal());
            jgen.writeBooleanField("isPrimary", attribute.getIsPrimary());
            jgen.writeStringField("attributeType", attribute.getAttributeType().toString());
            jgen.writeNumberField("aimPort", attribute.getAimPort());
            if (attribute.getLayoutInfo() != null) {
                jgen.writeObjectField("layoutInfo", attribute.getLayoutInfo());
            }
        } else {
            jgen.writeStringField("name", attribute.getName());
            jgen.writeStringField("dataType", attribute.getDataType().toString());
            jgen.writeBooleanField("isPrimary", attribute.getIsPrimary());
            jgen.writeStringField("attributeType", attribute.getAttributeType().toString());
            if (attribute.getAimPort() != -1) {
                jgen.writeNumberField("aimPort", attribute.getAimPort());
            }
            if (attribute.getLayoutInfo() != null) {
                jgen.writeObjectField("layoutInfo", attribute.getLayoutInfo());
            }
        }


        jgen.writeEndObject();
    }
}