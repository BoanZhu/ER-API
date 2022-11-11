package com.ic.er.common;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public enum EntityType {
    UNKNOWN(0, "UNKNOWN"),
    STRONG(0, "STRONG"),
    WEAK(1, "WEAK"),
    SUBSET(2, "SUBSET");

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
