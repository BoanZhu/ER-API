package com.ic.er.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.UUID;

public class RandomUtils {
    public static Long generateID() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}


