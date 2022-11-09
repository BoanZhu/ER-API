package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RelatedObjType {
    UNKNOWN(0),
    ATTRIBUTE(1),
    ENTITY(2),
    RELATIONSHIP(3);

    private final Integer value;
}
