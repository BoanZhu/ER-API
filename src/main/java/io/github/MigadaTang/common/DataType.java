package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataType {
    UNKNOWN,
    CHAR,
    VARCHAR,
    TEXT,
    TINYINT,
    SMALLINT,
    INT,
    BIGINT,
    FLOAT,
    DOUBLE,
    DATETIME
}
