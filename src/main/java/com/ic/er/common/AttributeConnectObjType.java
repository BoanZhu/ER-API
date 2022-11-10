package com.ic.er.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AttributeConnectObjType {
    UNKNOWN(0),
    ENTITY(1),
    RELATIONSHIP(2);

    private final Integer value;
}
