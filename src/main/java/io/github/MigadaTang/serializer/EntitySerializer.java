package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.MigadaTang.Entity;

import java.io.IOException;

public class EntitySerializer extends JsonSerializer<Entity> {
    private boolean isRenderFormat;

    public EntitySerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(
            Entity entity, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();

        if (isRenderFormat) {
            jgen.writeNumberField("id", entity.getID());
            jgen.writeStringField("name", entity.getName());
            jgen.writeNumberField("entityType", entity.getEntityType().getCode());
            jgen.writeObjectField("attributeList", entity.getAttributeList());
            jgen.writeNumberField("aimPort", entity.getAimPort());
            if (entity.getBelongStrongEntity() != null) {
                jgen.writeStringField("belongStrongEntityID", entity.getBelongStrongEntity().getID().toString());
            } else {
                jgen.writeNullField("belongStrongEntityID");
            }
            if (entity.getLayoutInfo() != null) {
                jgen.writeObjectField("layoutInfo", entity.getLayoutInfo());
            } else {
                jgen.writeNullField("layoutInfo");
            }
        } else {

            jgen.writeStringField("name", entity.getName());
            jgen.writeStringField("entityType", entity.getEntityType().getValue());
            if (entity.getAttributeList() != null && entity.getAttributeList().size() != 0) {
                jgen.writeObjectField("attributeList", entity.getAttributeList());
            }
            if (entity.getAimPort() != -1) {
                jgen.writeNumberField("aimPort", entity.getAimPort());
            }
            if (entity.getBelongStrongEntity() != null) {
                jgen.writeStringField("belongStrongEntity", entity.getBelongStrongEntity().getName());
            }
            if (entity.getLayoutInfo() != null) {
                jgen.writeObjectField("layoutInfo", entity.getLayoutInfo());
            }
        }


        jgen.writeEndObject();
    }
}