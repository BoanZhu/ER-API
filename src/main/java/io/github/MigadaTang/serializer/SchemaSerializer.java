package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.MigadaTang.Schema;

import java.io.IOException;

public class SchemaSerializer extends JsonSerializer<Schema> {
    private boolean isRenderFormat;

    public SchemaSerializer() {
    }

    public SchemaSerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(Schema schema, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {

        jgen.writeStartObject();

        if (isRenderFormat) {
            jgen.writeNumberField("id", schema.getID());
            jgen.writeStringField("name", schema.getName());
            jgen.writeObjectField("entityList", schema.getEntityList());
            jgen.writeObjectField("relationshipList", schema.getRelationshipList());
        } else {
            jgen.writeStringField("name", schema.getName());
            if ((schema.getEntityList() != null && schema.getEntityList().size() != 0)) {
                jgen.writeObjectField("entityList", schema.getEntityList());
            }
            if (schema.getRelationshipList() != null && schema.getRelationshipList().size() != 0) {
                jgen.writeObjectField("relationshipList", schema.getRelationshipList());
            }
        }

        jgen.writeEndObject();
    }
}
