package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
@AllArgsConstructor
public enum Cardinality {
    Unknown(0, ""),
    ZeroToOne(1, "0:1"),
    ZeroToMany(2, "0:N"),
    OneToOne(3, "1:1"),
    OneToMany(4, "1:N");
    private final Integer code;
    private final String value;
    private static final Map<String, Cardinality> EnumFromValue;
    private static final Map<Integer, Cardinality> EnumFromCode;

    static {
        Map<String, Cardinality> map = new ConcurrentHashMap<>();
        Map<Integer, Cardinality> mapFromCode = new ConcurrentHashMap<>();
        for (Cardinality instance : Cardinality.values()) {
            map.put(instance.getValue(), instance);
            mapFromCode.put(instance.getCode(), instance);
        }
        EnumFromValue = Collections.unmodifiableMap(map);
        EnumFromCode = Collections.unmodifiableMap(mapFromCode);
    }

    public static Cardinality getFromValue(String name) {
        return EnumFromValue.get(name);
    }

    public static Cardinality getFromCode(Integer code) {
        return EnumFromCode.get(code);
    }
}
