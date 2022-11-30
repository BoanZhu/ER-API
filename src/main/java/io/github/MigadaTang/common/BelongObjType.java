package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type of the object that the current object belongs to
 */
@AllArgsConstructor
@Getter
public enum BelongObjType {
    UNKNOWN(0),
    ATTRIBUTE(1),
    ENTITY(2),
    RELATIONSHIP(3);

    private final Integer value;
}
