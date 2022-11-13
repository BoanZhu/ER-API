package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.common.EntityType;

import java.io.IOException;

public class EntitySerializer extends StdSerializer<Entity> {

    public EntitySerializer() {
        this(null);
    }

    public EntitySerializer(Class<Entity> t) {
        super(t);
    }

    @Override
    public void serialize(
            Entity entity, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField("name", entity.getName());
        jgen.writeStringField("entityType", entity.getEntityType().getValue());
        if (entity.getEntityType() == EntityType.SUBSET || entity.getEntityType() == EntityType.WEAK) {
            if (entity.getBelongStrongEntity() != null) {
                jgen.writeStringField("belongStrongEntity", entity.getBelongStrongEntity().getName());
            }
        }
        if (entity.getAttributeList() != null && entity.getAttributeList().size() != 0) {
            jgen.writeObjectField("attributeList", entity.getAttributeList());
        }
        if (entity.getAimPort() != -1) {
            jgen.writeNumberField("aimPort", entity.getAimPort());
        }
        if (entity.getLayoutInfo() != null) {
            jgen.writeObjectField("layoutInfo", entity.getLayoutInfo());
        }
        jgen.writeEndObject();
    }
}