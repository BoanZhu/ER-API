package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.common.EntityType;

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
            jgen.writeNumberField("ID", entity.getID());
            jgen.writeStringField("name", entity.getName());
            jgen.writeStringField("entityType", entity.getEntityType().getValue());
            jgen.writeObjectField("attributeList", entity.getAttributeList());
            if (entity.getAimPort() == -1) {
                jgen.writeNumberField("aimPort", 1);
            } else {
                jgen.writeNumberField("aimPort", entity.getAimPort());
            }

            if (entity.getBelongStrongEntity() != null) {
                jgen.writeStringField("belongStrongEntityID", entity.getBelongStrongEntity().getID().toString());
            }
            else {
                jgen.writeNullField("belongStrongEntityID");
            }

            if (entity.getLayoutInfo() != null) {
                jgen.writeObjectField("layoutInfo", entity.getLayoutInfo());
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