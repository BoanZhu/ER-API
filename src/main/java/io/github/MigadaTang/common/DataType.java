package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type of data type an attribute has
 */
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
    DATETIME,
    NUMERIC,
    INT4,
    DATE
}
