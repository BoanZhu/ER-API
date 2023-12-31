package io.github.MigadaTang.common;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The different types of entities
 */
@AllArgsConstructor
@Getter
public enum EntityType {
    UNKNOWN(0, "UNKNOWN"),
    STRONG(1, "STRONG"),
    WEAK(2, "WEAK"),
    SUBSET(3, "SUBSET"),
    GENERALISATION(4, "GENERALISATION");

    private final Integer code;
    private final String value;
    private static final Map<String, EntityType> EnumFromValue;
    private static final Map<Integer, EntityType> EnumFromCode;

    static {
        Map<String, EntityType> map = new ConcurrentHashMap<>();
        Map<Integer, EntityType> mapFromCode = new ConcurrentHashMap<>();
        for (EntityType instance : EntityType.values()) {
            map.put(instance.getValue(), instance);
            mapFromCode.put(instance.getCode(), instance);
        }
        EnumFromValue = Collections.unmodifiableMap(map);
        EnumFromCode = Collections.unmodifiableMap(mapFromCode);
    }

    public static EntityType getFromValue(String name) {
        return EnumFromValue.get(name);
    }

    public static EntityType getFromCode(Integer code) {
        return EnumFromCode.get(code);
    }
}
