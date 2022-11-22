package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public enum DataType {
    UNKNOWN("UNKNOW"),
    CHAR("CHAR"),
    VARCHAR("VARCHAR"),
    TEXT("TEXT"),
    TINYINT("TINYINT"),
    SMALLINT("SMALLINT"),
    INT("INT"),
    BIGINT("BIGINT"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    DATETIME("DATATIME");


    private final String value;
    private static final Map<String, DataType> EnumFromValue;


    static {
        Map<String, DataType> map = new ConcurrentHashMap<>();
        for (DataType instance : DataType.values()) {
            map.put(instance.getValue(), instance);
        }
        EnumFromValue = Collections.unmodifiableMap(map);
    }

    public static DataType getFromValue(String name) {
        return EnumFromValue.get(name);
    }
}
