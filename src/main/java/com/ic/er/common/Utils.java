package com.ic.er.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.UUID;

public class Utils {
    public static Long generateID() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}


