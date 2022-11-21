package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public enum AttributeType {
    // 1-mandatory, 2-optional, 3-multi-valued, 4-both optional and multivalued
    UNKNOWN(0, "Unknown"),
    Mandatory(1, "Mandatory"),
    Optional(2, "Optional"),
    Multivalued(3, "Multivalued"),
    Both(4, "Both");

    private final Integer code;
    private final String value;
    private static final Map<String, AttributeType> EnumFromValue;
    private static final Map<Integer, AttributeType> EnumFromCode;

    static {
        Map<String, AttributeType> map = new ConcurrentHashMap<>();
        Map<Integer, AttributeType> mapFromCode = new ConcurrentHashMap<>();
        for (AttributeType instance : AttributeType.values()) {
            map.put(instance.getValue(), instance);
            mapFromCode.put(instance.getCode(), instance);
        }
        EnumFromValue = Collections.unmodifiableMap(map);
        EnumFromCode = Collections.unmodifiableMap(mapFromCode);
    }

    public static AttributeType getFromValue(String name) {
        return EnumFromValue.get(name);
    }

    public static AttributeType getFromCode(Integer code) {
        return EnumFromCode.get(code);
    }
}
