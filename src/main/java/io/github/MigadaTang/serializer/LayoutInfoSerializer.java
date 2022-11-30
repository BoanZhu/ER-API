package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.MigadaTang.LayoutInfo;

import java.io.IOException;

public class LayoutInfoSerializer extends JsonSerializer<LayoutInfo> {
    private boolean isRenderFormat;

    public LayoutInfoSerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(
            LayoutInfo layoutInfo, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();


        jgen.writeNumberField("layoutX", layoutInfo.getLayoutX());
        jgen.writeNumberField("layoutY", layoutInfo.getLayoutY());

        jgen.writeEndObject();
    }
}